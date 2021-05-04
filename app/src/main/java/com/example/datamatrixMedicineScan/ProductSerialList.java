package com.example.datamatrixMedicineScan;


import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.example.datamatrixMedicineScan.dbFunctions.ProductFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.SerialFunctions;
import com.example.datamatrixMedicineScan.dbHelper.GTIN;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;


// LISTS PRODUCTS CATEGORIZED BY SELECTED GTIN, CATEGORY -> SERIAL
public class ProductSerialList extends AppCompatActivity {

	private ArrayAdapter<String> adapter;

	private ArrayList<String> serialNumbers=new ArrayList<String>();
	private String GTIN_code;
	private int productId;
	private int productsNumber=0;
	private TextView serialView;
	private TextView productsNumberView;
	private EditText searchFieldText;
	private ListView serialList;
	private HashMap<String,Class<?>> classMap=new HashMap<String,Class<?>>();

	public String category_id;

	// text watcher for serial search
	// --------------------------------------------------------------------------------------
	TextWatcher watcher=new TextWatcher(){

		// on serial searchbox change
		@Override
		public void onTextChanged(CharSequence s,int start,int before,int count){
			String text=s.toString();	// get serial

			// if input serial len >= 1 search it
			// else re-init the serial list
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
	// --------------------------------------------------------------------------------------


	// onItemClickListener -- Product clicked
	// --------------------------------------------------------------------------------------
	private OnItemClickListener onItemClickListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int pos,long arg3){
			String text=serialList.getItemAtPosition(pos).toString();		// get text of product clicked

			// get the serial string
			String productSerial=text.split(" ")[0];

			// put serial code and GTIN as extras and call the productInformation activity
			HashMap<String,Object> extra=new HashMap<String,Object>();
			extra.put("code",productSerial);
			extra.put("GTIN",GTIN_code);
			createActivity("productInformation",extra);
		}
	};
	// --------------------------------------------------------------------------------------


	// Method createAcitvity same as always
	// --------------------------------------------------------------------------------------
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
	// --------------------------------------------------------------------------------------


	// returns the GTIN code product ID (used onCreate) -- it basically returns GTIN id.
	// ----------------------------------------------------------------------------------------------
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
	// ----------------------------------------------------------------------------------------------

	// initializes serial list with products
	// ----------------------------------------------------------------------------------------------
	public void initializeSerialList(){
		SerialFunctions sf=new SerialFunctions(this);	// get functions
		List<SerialNumber> result;								// result set
		serialNumbers.clear();									// clear the existing list

		try{
			// get the serial numbers of the GTIN with id = product_id (product_id is basically the GTIN_id)
			result=sf.qb(2).orderBy("serialNumber",true).where().eq("product_id",productId).query();

			// if resultset is not empty
			if(!result.isEmpty()){
				productsNumber=result.size();									// get result size
				productsNumberView.setText("Total Products: "+productsNumber);	// and show it

				// populate serial number list with "serial number"_"type" format
				for(int i=0;i<result.size();i++){
					serialNumbers.add(result.get(i).getSerialNumber()+" "+result.get(i).getType().getType());
				}
			}else{
				// result set is empty --> should return on GTIN list
				createActivity("GTIN_list",null);
				return;
			}
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set the adapter on serial list -- we want an array adapter with specific layout and data --> serialNumbers
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serialNumbers);
		serialList.setAdapter(adapter);

	}
	// ----------------------------------------------------------------------------------------------

	// search by serial
	// ----------------------------------------------------------------------------------------------
	public void searchSerial(String serial){
	
		Log.d("search text:",serial);
		SerialFunctions sf=new SerialFunctions(this);	// get functions
		List<SerialNumber> result;								// resultset
		serialNumbers.clear();									// clear the list
		try{
			// we want the serial number UNDER the GTIN (product) id
			result=sf.qb(2).where().
					like("serialNumber",serial+"%").
					and().
					eq("product_id",productId).
					query();

			// if found something, populate our serial list accordingly, no need for else -- it will be taken care of on text watcher
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

		// notify -- DATA CHANGED on our adapter -- correct way
		adapter.notifyDataSetChanged();
	}
	// ----------------------------------------------------------------------------------------------


	// onCreate
	// --------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_serial_list);
		classMap.put("productInformation", ProductInformationActivity.class);
		classMap.put("GTIN_list", ProductsListActivity.class);

		// get information from ProductsList -- problem when reverse
		Intent intent=getIntent();
		GTIN_code=intent.getStringExtra("code");
		category_id=intent.getStringExtra("category_id");

		productId=getProductId(GTIN_code);	// get productId using GTIN_code

		// init views
		serialView=(TextView)findViewById(R.id.psla_serialView);
		productsNumberView=(TextView)findViewById(R.id.psla_totalProductsView);
		searchFieldText=(EditText)findViewById(R.id.psla_searchFieldText);
		serialList=(ListView)findViewById(R.id.psla_serialList);


		// set the head label initialized with GTIN code and set the serial number list adapter
		serialView.setText(serialView.getText().toString()+GTIN_code);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,serialNumbers);
		serialList.setAdapter(adapter);
		initializeSerialList();

		// add listeners
		serialList.setOnItemClickListener(onItemClickListener);
		searchFieldText.addTextChangedListener(watcher);
	}


}
