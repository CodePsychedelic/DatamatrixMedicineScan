package com.example.datamatrixMedicineScan;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dbFunctions.CategoryFunctions;
import dbFunctions.ProductFunctions;
import dbHelper.Category;
import dbHelper.Field;
import dbHelper.GTIN;
import dbHelper.SerialNumber;
import dbHelper.Type;


// List by GTIN code
public class ProductsListActivity extends AppCompatActivity {

	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> listItems;

	private EditText searchFieldText;
	private ListView productList;
	private Button goBackButton;
	private CheckBox changeCategoryCheckbox;
	private Class<?> activitiesList[]=new Class<?>[]{MainActivity.class, ProductSerialList.class};
	private HashMap<String,Class<?>> classMap=new HashMap<String,Class<?>>();
	private Context context=this;
	private boolean change=false;


	// text watcher for GTIN search
	// --------------------------------------------------------------------------------------
	TextWatcher watcher=new TextWatcher(){

		// ONTEXTCHANGED -- EACH TIME TEXT CHANGES, SEARCH PRODUCT BY GTIN
		@Override
		public void onTextChanged(CharSequence s,int start,int before,int count){
			// TODO Auto-generated method stub
			String searchText=searchFieldText.getText().toString();	// get user input

			// if text len > 1 search, else re-init product list
			if(searchText.length()>=1){
				searchProduct(searchText);
			}else{
				initializeProductsList();
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

	private DialogInterface.OnClickListener categorySelectListener=new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog,int which){
			// TODO Auto-generated method stub
			dialog.dismiss();
			printMessage("selection",""+which,null);

		}
	};

	// onItemClickListener -- Product clicked
	// --------------------------------------------------------------------------------------
	OnItemClickListener onItemClickListener=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> list,View arg1,int pos,long arg3){

			String listSelection=list.getItemAtPosition(pos).toString();	// get item
			String tokens[]=listSelection.split(" ");				// split by space

			// change category checkbox is checked
			// --------------------------------------------------------------------------------------
			if(changeCategoryCheckbox.isChecked()){
				change=true;
				String category=tokens[1];	// get category
				CategoryFunctions cf=new CategoryFunctions(context);	// init category functions
				try{
					// select all available category options besides all category
					List<Category> categoriesList=cf.qb(2).where().ne("category",category).and().ne("category","all").query();
					String categories[]=new String[categoriesList.size()];	// init a string list
					for(int i=0;i<categoriesList.size();i++){
						// populate it with category string plus and id seperated by ; --> category;id
						categories[i]=categoriesList.get(i).getCategory()+";"+categoriesList.get(i).getId();
					}

					printSelection(categories,tokens[0]);	// printSelection is a function
				}catch(SQLException e){
					e.printStackTrace();
				}
			// --------------------------------------------------------------------------------------
			}
			// change category option is not checked -- will call a new activity
			// --------------------------------------------------------------------------------------
			else{
				String productCode=tokens[0];	// get product code
				try{
					HashMap<String,Object> extra=new HashMap<String,Object>();	// new hasmap for extra fields -- new activity with data
					extra.put("code",productCode);								// needs product code as "code"

					// will need category id -- fetch it
					extra.put("category_id", String.valueOf(Tools.pf.qb(2).where().eq("GTIN_code",productCode).queryForFirst().getProductCategory().getId()));

					// will need product id -- fetch it
					extra.put("product_id",String.valueOf(Tools.pf.qb(2).where().eq("GTIN_code",productCode).queryForFirst().getId()));

					// pass change
					extra.put("change",change);

					// create view serials activities to inspect serials under the GTIN selected
					createActivity("viewSerialNumbers", extra);
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
	};
	// --------------------------------------------------------------------------------------


	// button listener for return to mainActivity
	// --------------------------------------------------------------------------------------
	OnClickListener buttonListener=new OnClickListener(){

		@Override
		public void onClick(View v){
			createActivity("start",null);
		}
	};
	// --------------------------------------------------------------------------------------



	// createActivity method -- shortcut for creating a new activity
	// --------------------------------------------------------------------------------------
	public void createActivity(String activity, HashMap<String,Object> extra){
		Intent intent=new Intent(this,classMap.get(activity));	// fetch class from classMap (hashmap)

		// if extra not null, set it dynamically
		if(extra!=null){
			Set<String> keySet=extra.keySet();
			Object keys[]=keySet.toArray();
			for(int i=0;i<keys.length;i++){
				intent.putExtra(keys[i].toString(),extra.get(keys[i]).toString());
			}
		}
		startActivity(intent);	// start new activity
	}
	// --------------------------------------------------------------------------------------

	// same method, using class not string
	// --------------------------------------------------------------------------------------
	public void createActivity(Class<?> activity, HashMap<String,Object> extra){
		Intent intent=new Intent(this,activity);
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


	// searchProduct method -- will be called to search by GTIN code
	// --------------------------------------------------------------------------------------
	public void searchProduct(String code){

		Log.d("searching:",code);

		ProductFunctions pf=new ProductFunctions(this);					// init product functions
		List<GTIN> result=pf.selectLike("GTIN_code",code,2);	// filter using GTIN code

		listItems.clear();														// clear the list of products
		for(int i=0;i<result.size();i++){
			listItems.add(result.get(i).getCode());								// populate list of products (Result)
		}
		listAdapter.notifyDataSetChanged();										// adapter update
	}
	// --------------------------------------------------------------------------------------


	// initializeProductList method -- will obviously initialize products list
	// --------------------------------------------------------------------------------------
	public void initializeProductsList(){
		ProductFunctions pf=new ProductFunctions(this);	// pf object
		List<GTIN> result=pf.selectAll(2);				// select all


		// set listItems presented
		if(listItems==null) listItems=new ArrayList<String>();		// if null init
		else listItems.clear();										// else clear


		// if we have a result, populate with code <space> category
		// else we have an empty database --> return on main activity, while informing
		// ---------------------------------------------------------------------------------------
		if(!result.isEmpty()){
			for(int i=0;i<result.size();i++){
				listItems.add(result.get(i).getCode()+" "+result.get(i).getProductCategory().getCategory());
			}
		} else{
			printMessage("Empty","Empty Database", MainActivity.class);	// redirection
		}
		// ---------------------------------------------------------------------------------------

		// fix listAdapter
		// if null, init and set
		// if not null, notify for dataset change
		// ---------------------------------------------------------------------------------------
		if(listAdapter==null){
			listAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
			productList.setAdapter(listAdapter);
		}
		else{
			listAdapter.notifyDataSetChanged();
		}
		// ---------------------------------------------------------------------------------------
	}
	// --------------------------------------------------------------------------------------


	// prints a selectbox for category selection
	// arguments:	String categoriesInfo[] -> contains the categories_string;categories_id (foreach category)
	//				String productCode 		-> contains the GTIN code
	// --------------------------------------------------------------------------------------
	public void printSelection(String categoriesInfo[],String productCode){
		Builder selectDialog=new Builder(this);			// select dialog
		final String code=productCode;							// create final string for gtin code
		String selections[]=new String[categoriesInfo.length]; 	// new table for categories selection
		String id[]=new String[categoriesInfo.length]; 			// new table for categories id

		//parse each categoriesInfo
		for(int i=0;i<selections.length;i++){
			String tokens[]=categoriesInfo[i].split(";");
			selections[i]=tokens[0];
			id[i]=tokens[1];
		}

		//final strings for selections and ids
		final String fSelections[]=selections;
		final String fid[]=id;

		// create dialog, set items and clickListener
		selectDialog.setItems(fSelections,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog,int which){

				//onclick dismiss the dialog
				//get the category id that was selected
				//===================
				dialog.dismiss();
				String id=fid[which];
				//===================

				ProductFunctions pf=new ProductFunctions(context);
				UpdateBuilder<GTIN,Integer> pub=pf.ub(2);


				try{
					//update where gtin code
					//set category id to new category
					//================================
					pub.where().eq("GTIN_code",code);
					pub.updateColumnValue("category_id",id);
					pub.update();
					//================================

					// get product database id using GTIN
					int pid= Tools.pf.qb(2).where().
							eq("GTIN_code",code).queryForFirst().getId();

					// get product serial numbers scanned, using product id
					List<SerialNumber> sns= Tools.sf.qb(2).where().
							eq("product_id",pid).query();

					// get type based on category id
					Type defaultType= Tools.tf.qb(2).where().
							eq("category_id",id).
							queryForFirst();


					/*
					where.and(
        where.not().like("supertypes", '%' + SuperType.BASIC.name() + '%'),
        where.like("types", '%' + CardType.LAND.name() + '%')
    );
					 */

					// get the new fields for our new category -- exclude buttons and labels
					List <Field> newFields= Tools.fs.qb(2).where().
							eq("type_id",defaultType.getId()).
							and().not().like("fieldName","%Button").
							and().not().like("fieldName","%Label").
							query();


					for(int i=0;i<sns.size();i++){
						List<Field> fields= Tools.fs.qb(2).where().
								eq("type_id",sns.get(i).getType().getId()).query();

						Tools.reallocFields(Tools.pif.db(2),fields,newFields,sns.get(i));

					}

					UpdateBuilder<SerialNumber,Integer> sub= Tools.sf.ub(2);
					sub.where().eq("product_id",pid);
					sub.updateColumnValue("type_id",defaultType.getId());
					sub.update();


					//List<Field> fields=Tools.fs.qb(2).where().eq()


					//reallocFields(Tools.pif.db(2),fields,newFields,code)
					//for(int i=0;i<sns.size();i++){
						//sns.get(i).setType(defaultType);
					//}
					//re initialize
					initializeProductsList();
				}catch(SQLException e){e.printStackTrace();}
			}
		});
		selectDialog.show();

	}

	public void printMessage(String title,String message,Class<?> activity){
		final Class<?> a=activity;

		AlertDialog alertDialog=new Builder(this).create();
		alertDialog.setTitle(title);
		//alertDialog.setMessage("������� �� �� ��������� ����� ����� ������������ �� ������ ���������� �����");
		alertDialog.setMessage(message);

		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
				"OK",
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog,int which){
						// TODO Auto-generated method stub
						dialog.dismiss();
						if(a!=null){
							createActivity(a,null);
						}
					}
				});

		alertDialog.show();
	}



	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products_list);
		Tools.initializeTools(this);
		classMap.put("start",activitiesList[0]);
		classMap.put("viewSerialNumbers",activitiesList[1]);


		searchFieldText=(EditText)findViewById(R.id.pla_searchFieldText);
		productList=(ListView)findViewById(R.id.pla_productList);
		goBackButton=(Button)findViewById(R.id.pla_goBackButton);
		changeCategoryCheckbox=(CheckBox)findViewById(R.id.pla_changeCategoryCheckbox);

		initializeProductsList();

		searchFieldText.addTextChangedListener(watcher);
		productList.setOnItemClickListener(onItemClickListener);
		goBackButton.setOnClickListener(buttonListener);

	}


}
