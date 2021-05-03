package com.example.datamatrixMedicineScan;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dbFunctions.ProductFunctions;
import dbFunctions.SerialFunctions;
import dbHelper.Field;
import dbHelper.GTIN;
import dbHelper.ProductAttributes;
import dbHelper.SerialNumber;
import dbHelper.Type;

public class ProductSerialList extends AppCompatActivity {

	private ArrayList<String> serialNumbers=new ArrayList<String>();
	private String GTIN_code;
	private int productId;
	private int productsNumber=0;
	private TextView serialView;
	private TextView productsNumberView;
	private EditText searchFieldText;
	private ListView serialList;
	private CheckBox chagneTypeCheckbox;
	private Context context=this;
	private HashMap<String,Class<?>> classMap=new HashMap<String,Class<?>>();
	TextWatcher watcher=new TextWatcher(){
		
		@Override
		public void onTextChanged(CharSequence s,int start,int before,int count){
			// TODO Auto-generated method stub
			String text=s.toString();
			if(text.length()>=1){
				searchSerial(text);
			}else{
				initializeSerialList();
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s,int start,int count,int after){
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s){
			// TODO Auto-generated method stub
			
		}
	};

	
	private OnItemClickListener onItemClickListener=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int pos,long arg3){
			// TODO Auto-generated method stub
			String text=serialList.getItemAtPosition(pos).toString();
			//String tokens[]=text.split(" ");
			String tokens[]=new String[2];
			tokens[0]=text.substring(0,text.indexOf(" "));
			tokens[1]=text.substring(text.indexOf(" ")+1,text.length());
			String productSerial=tokens[0];
			String productType=tokens[1];
			if(chagneTypeCheckbox.isChecked()){
				try{
					List<Type> types= Tools.tf.qb(2).
							where().
							ne("type",productType).
							and().eq("category_id",category_id).query();
					Type ct= Tools.tf.qb(2).
							where().
							eq("type",productType).
							and().
							eq("category_id",category_id).
							queryForFirst();

					String typeInformation[]=new String[types.size()];
					for(int i=0;i<types.size();i++){
						typeInformation[i]=types.get(i).getType()+";"+types.get(i).getId();
					}

					printSelection(typeInformation,productSerial,ct.getId());
				}catch(SQLException e){
					e.printStackTrace();
				}
			}else{
				HashMap<String,Object> extra=new HashMap<String,Object>();
				extra.put("code",productSerial);
				extra.put("GTIN",GTIN_code);
				createActivity("productInformation",extra);
			}
		}
	};


	public void createActivity(String activity,HashMap<String,Object> extra){
		Intent intent=new Intent(this,classMap.get(activity));
		if(extra!=null){
			Set<String> keySet=extra.keySet();
			Object keys[]=keySet.toArray();
			for(int i=0;i<keys.length;i++){
				intent.putExtra(keys[i].toString(),extra.get(keys[i]).toString());
			}
		}
		startActivity(intent);

	}

	//prints a selectbox for type selection
	//arguments:String categoriesInfo[]-> contains the categories_string;categories_id (foreach category)
//				String productCode -> contains the GTIN code
	public void printSelection(String typesInfo[],String productSerial,int ctId){
		Builder selectDialog=new Builder(this);
		//create final string for gtin code
		final String code=productSerial;
		final int currentTypeId=ctId;
		//new table for categories selection
		String selections[]=new String[typesInfo.length];
		//new table for categories id
		String id[]=new String[typesInfo.length];
		//parse each categoriesInfo
		for(int i=0;i<selections.length;i++){
			String tokens[]=typesInfo[i].split(";");
			selections[i]=tokens[0];
			id[i]=tokens[1];
		}
		//final strings for selections and ids
		final String fSelections[]=selections;
		final String fid[]=id;
		selectDialog.setItems(fSelections,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog,int which){
				// TODO Auto-generated method stub
				//onclick dismiss the dialog
				//get the category id that was selected
				//===================
				dialog.dismiss();
				String id=fid[which];
				//===================

				UpdateBuilder<SerialNumber,Integer> sub= Tools.sf.ub(2);

				try{
					//update where gtin code
					//set category id to new category
					//================================
					sub.where().eq("serialNumber",code).and().eq("product_id",productId);
					sub.updateColumnValue("type_id",id);
					sub.update();
					//================================

					List <Field> fields= Tools.fs.qb(2).where().eq("type_id",currentTypeId).query();
					List <Field> newFields= Tools.fs.qb(2).where().
							eq("type_id",id).
							and().
							not().like("fieldName","%Button").
							and().
							not().like("fieldName","%Label").
							query();



					DeleteBuilder<ProductAttributes,Integer> padb= Tools.pif.db(2);

					SerialNumber sn= Tools.sf.qb(2).where().eq("serialNumber",code).and().eq("product_id",productId).queryForFirst();
					Tools.reallocFields(padb,fields,newFields,sn);



					//re initialize
					initializeSerialList();
				}catch(SQLException e){e.printStackTrace();}
			}
		});
		selectDialog.show();

	}



	public String category_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_serial_list);
		classMap.put("productInformation", ProductInformationActivity.class);
		Intent intent=getIntent();
		GTIN_code=intent.getStringExtra("code");
		category_id=intent.getStringExtra("category_id");

		productId=getProductId(GTIN_code);

		serialView=(TextView)findViewById(R.id.psla_serialView);
		productsNumberView=(TextView)findViewById(R.id.psla_totalProductsView);
		searchFieldText=(EditText)findViewById(R.id.psla_searchFieldText);
		serialList=(ListView)findViewById(R.id.psla_serialList);
		chagneTypeCheckbox=(CheckBox)findViewById(R.id.psla_changeTypeCheckbox);

		if(intent.getBooleanExtra("change",false)){
			try{
				Type p= Tools.tf.qb(2).where().eq("category_id",category_id).queryForFirst();
				
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		serialView.setText(serialView.getText().toString()+GTIN_code);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serialNumbers);
		serialList.setAdapter(adapter);
		initializeSerialList();
		
		serialList.setOnItemClickListener(onItemClickListener);
		searchFieldText.addTextChangedListener(watcher);
	}

	
	
	
	public int getProductId(String code){
		ProductFunctions pf=new ProductFunctions(this);
		try{
			GTIN product=pf.qb(2).where().eq("GTIN_code",code).queryForFirst();
			return product.getId();
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public void initializeSerialList(){
		SerialFunctions sf=new SerialFunctions(this);
		List<SerialNumber> result;
		serialNumbers.clear();
		try{
			result=sf.qb(2).orderBy("serialNumber",true).where().eq("product_id",productId).query();
			productsNumber=result.size();
			productsNumberView.setText("Total Products: "+productsNumber);
			if(!result.isEmpty()){
				for(int i=0;i<result.size();i++){
					serialNumbers.add(result.get(i).getSerialNumber()+" "+result.get(i).getType().getType());
				}
				
			}
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serialNumbers);
		serialList.setAdapter(adapter);

		
	}

	
	
	public void searchSerial(String serial){
	
		Log.d("search text:",serial);
		SerialFunctions sf=new SerialFunctions(this);
		List<SerialNumber> result;
		serialNumbers.clear();
		try{
			result=sf.qb(2).where().
					like("serialNumber",serial+"%").
					and().
					eq("product_id",productId).
					query();
			
			if(!result.isEmpty()){
				
				for(int i=0;i<result.size();i++){
					serialNumbers.add(result.get(i).getSerialNumber()+" "+result.get(i).getType().getType());
					Log.d("serial:",result.get(i).getSerialNumber());
				}
			
			}
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serialNumbers);
	
		serialList.setAdapter(adapter);

	}
	
	
	

}
