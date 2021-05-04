package com.example.datamatrixMedicineScan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.example.datamatrixMedicineScan.dbHelper.Category;
import com.example.datamatrixMedicineScan.dbHelper.Field;
import com.example.datamatrixMedicineScan.dbHelper.GTIN;
import com.example.datamatrixMedicineScan.dbHelper.Pattern;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;
import com.example.datamatrixMedicineScan.dbHelper.Type;

public class SignProductDynamic extends AppCompatActivity {

	private List<Integer> globalFieldsId=new ArrayList<Integer>();
	
	//HashMap<String,LinearLayout> object which we use to store the LinearLayouts 
	//of each category
	private HashMap<String,LinearLayout> layouts;
	//List<Category> object which we use to store our categories (get them from the
	//db table)
	private List<Category> categories;
	
	//private HashMap<String,Object> types=new HashMap<String,Object>();
	//int object which we use to identify the current selected category (from interface)
	
	//root layoyt
	private LinearLayout rootLayout;
	//context
	private Context context;
	int currentSelectedCategory;
	int currentSelectedType=-1;
	
	private Spinner typeSelection;
	//Spinner which we use for category selection in user interface
	private Spinner categorySelection;
	private EditText productCodeText;
	private EditText productNameText;
	
	private class TextF extends EditText{

		private String name;
		private String type;
		private String ai="-1";
		
		public TextF(Context context,String name,String type){
			super(context);
			this.name=name;
			this.type=type;
		}
		
		
		private void setName(String name){
			this.name=name;
		}
		
		private String getName(){
			return name;
		}
		
		private String getType(){
			return type;
		}
		
		private void setAI(String ai){
			this.ai=ai;
		}
		
		private String getAI(){
			return ai;
		}
		
	
	}
	
	private class Error{
		private TextF field;
		private String errorCode;
		
		public Error(TextF field,String errorCode){
			this.field=field;
			this.errorCode=errorCode;
		}
		
		public void setField(TextF field){
			this.field=field;
		}
		public TextF getField(){
			return field;
		}
		
		public void setErrorCode(String errorCode){
			this.errorCode=errorCode;
		}
		
		public String getErrorCode(){
			return errorCode;
		}
	}
	
	
	private OnItemSelectedListener typeSelectedListener=new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> spinner,View arg1,int pos,
				long arg3){

			//create a tf object

			try{
				//list to hold the results 
				//select * from types where category_id=currentSelectedCategory
				//and type=typeSelectedFromSpinner
				List <Type> typ= Tools.tf.qb(2).where().eq
						(
						"category_id",currentSelectedCategory).
						and().
						eq("type",spinner.getItemAtPosition(pos).toString()).query();
				//get the id of the type returned
				String id=String.valueOf(typ.get(0).getId());
				//set the layout with the id of type returned
				setLayout(id);
			}catch(SQLException e){e.printStackTrace();}


		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0){
			// TODO Auto-generated method stub

		}
	};



	//itemSelectedListener for spinner
	private OnItemSelectedListener categorySelectedListener=new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> spinner,View arg1,int pos,
				long arg3){

			//get Category lectic
			String category=spinner.getItemAtPosition(pos).toString();
			Category selected=null;
			//find the selected category object from category list
			for(int i=0;i<categories.size();i++){
				if(categories.get(i).getCategory().equals(category)){
					//when found set the selected to the list category
					selected=categories.get(i);
					//set the currentSelectedCategory pointer to the id of the category
					currentSelectedCategory=selected.getId();
					break;
				}
			}



			//get a query builder
			QueryBuilder<Type,Integer> qb= Tools.tf.qb(2);

			//list to get the types from query
			List<Type> types=null;
			//list for type selection ADAPTER
			List<String> typeChoices=new ArrayList<String>();

			try{
				//select * from types where category_id=selectedCategory
				//get the types of selected category with this query
				types=qb.where().eq(
						"category_id",
						selected).query();
			}catch(SQLException e){e.printStackTrace();}

			if(types!=null){
				for(int i=0;i<types.size();i++){
					//add the types lectics into the typeChoices list
					typeChoices.add(types.get(i).getType());

				}
			}

			//create the adapter for type selectBox
			ArrayAdapter<String> adapter=
				new ArrayAdapter<String>(
							context,
							android.R.layout.simple_spinner_item,typeChoices
						);
		//finaly set the adapter to the spinner
		typeSelection.setAdapter(adapter);





		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0){
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener buttonListener=new OnClickListener(){

		@Override
		public void onClick(View v){
			// TODO Auto-generated method stub

			int fieldId=Integer.parseInt(String.valueOf(currentSelectedType)+"1");
			Log.d("Type id:",fieldId+"");
			View view=layouts.get(String.valueOf(currentSelectedType)).findViewById(fieldId);

			HashMap<String,Object> values=new HashMap<String,Object>();
			HashMap<String,Object> validateValues=new HashMap<String,Object>();

			//global fields data
			for(int i=0;i<globalFieldsId.size();i++){
				int gfi=globalFieldsId.get(i);
				TextF globalF=((TextF)rootLayout.findViewById(gfi));
				Error e=checkRules(globalF,globalF.getAI());

				if(e.getErrorCode().equals("-1")){
					printMessage("numeric","������� �� �� ��������� �����"+e.getField().getName()+" ����� ������������ �� ����� ��������� ����",null);
					return;
				}else if(e.getErrorCode().equals("-21")){
					String ai=e.getField().getAI();
					String tokens[]=ai.split(";");

					tokens[1]=tokens[1].replace("s","");

					printMessage("range","����� ����������� ����� ����� ����������� ��� ����� "+e.getField().getName()+".�� ���������� ��� ������ �� ���� ���� �� ����� ����� "+tokens[1],null);
					return;
				}else if(e.getErrorCode().equals("-22")){
					String ai=e.getField().getAI();
					String tokens[]=ai.split(";");

					tokens[1]=tokens[1].replace("s","");
					tokens=tokens[1].split("-");
					printMessage("range","����� ����������� ����� ����� ����������� ��� ����� "+e.getField().getName()+".�� " +
							"��������� ���������� ��� ������ �� ���� ���� �� ����� ����� "+tokens[0],null);
					return;
				}else if(e.getErrorCode().equals("-23")){
					String ai=e.getField().getAI();
					String tokens[]=ai.split(";");

					tokens[1]=tokens[1].replace("s","");
					tokens=tokens[1].split("-");
					printMessage("range","����� ����������� ����� ����� ����������� ��� ����� "+e.getField().getName()+".�� " +
							"�� ���������� ��� ������ �� ���� ���� �� ����� ��� ������ �� ����������� ���� "+tokens[1],null);

					return;
				}
				values.put(globalF.getName(),globalF.getText().toString());

				//validateValues.put(
					//	globalF.getName(),
						//globalF.getText().toString()+";"+globalF.getType()
						//);
				Log.d("GLOBAL FIELD->"+globalF.getName(),globalF.getText().toString());
			}



			while(view!=null){
				if(view instanceof EditText){
					TextF field=(TextF)view;
					Log.d("view:","EditText");
					Log.d("name:",((TextF)view).getName());
					Log.d("Text:",((TextF)view).getText().toString());

					values.put(((TextF)view).getName(),((TextF)view).getText().toString());
					validateValues.put(
							((TextF)view).getName(),
							((TextF)view).getText().toString()+";"+((TextF)view).getType()
							);

				}
				//increment id and get next view
				fieldId++;
				view=layouts.get(String.valueOf(currentSelectedType)).findViewById(fieldId);
			}
			if(validateFieldValues(validateValues)){
				insertData(values);

			}
		}
	};


	HashMap<String, Field> AItoField=new HashMap<String, Field>();
	//function parseCode used to parse the incoming data matrix code into
	//GS1 fields
	public void parseCode(String code){


		//split the code based on character ~ (FCN1 equivalent)
		String tokens[]=code.split("~");

		try{
			//get the result from select * from types where type='all';
			//we need to get the id of type all to get the global fields dynamiclly
			Type res= Tools.tf.qb(2).where().eq("type","all").queryForFirst();

			//get the global fields
			List <Field> gfields= Tools.fs.qb(2).where().eq("type_id",res.getId()).query();
			//foreach field
			for(int i=0;i<gfields.size();i++){
				//get the field
				Field field=gfields.get(i);
				//initialize string which will store the field value from code
				String fieldV="";
				//if it is an EditText
				if(field.getFieldType().equals("EditText")){
					//get the pattern of GS1 standard attached to the field
					//and save it in a form ai->fieldName for later use
					Pattern pattern= Tools.paf.qb(2).where().eq("field_id",field.getId()).query().get(0);
					AItoField.put(pattern.getPattern(),field);
					//DEBUG
					//========
					Log.d("PATTERN:",pattern.getPattern());
					//========
					//foreach token of code
					for(int k=0;k<tokens.length;k++){
						//if the token has more than three characters
						if(tokens[k]!=null){
							if(tokens[k].length()>2){
								//turn the token into char array
								char c[]=tokens[k].toCharArray();
								//get the first two bytes to get the AI
								String ai=""+c[0]+c[1];
								Log.d("FOUND AI:",ai);
								//if the AI is the same that the pattern of the field
								//we are processing
								if(ai.equals(pattern.getPattern())){
									//save the code value to the fieldV
									for(int j=2;j<c.length;j++){
										fieldV+=c[j];
									}
									Log.d("VALUE FOR AI"+ai+"which equals pattern:"+pattern+"SHOULD BE: ",fieldV);
									//if there is a code set the EditText in the rootlayout that
									//holds the id of the field that we are processing to that code and make
									//it unfocusable.
									if(fieldV.length()>0){
										((EditText)rootLayout.findViewById(field.getFieldId())).setText(fieldV);
										((EditText)rootLayout.findViewById(field.getFieldId())).setFocusable(false);
										Log.d("DONE","DONE");
									}
									tokens[k]=null;

								}
							}
						}
					}
				}

			}




		}catch(SQLException e){e.printStackTrace();}
	}



	public Category checkGTINCategory(String code){
		String codeTokens[]=code.split("~");
		for(int i=0;i<codeTokens.length;i++){
			if(codeTokens[i].length()>=2){
				char ai[]=codeTokens[i].toCharArray();
				if(ai[0]=='0' && ai[1]=='1'){
					try{
						GTIN product= Tools.pf.qb(2).where().eq("GTIN_code",codeTokens[i].substring(2)).queryForFirst();
						if(product!=null) return product.getProductCategory();
					}catch(SQLException e){
						e.printStackTrace();
					}
					break;
				}
			}


		}
		return null;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_product_dynamic);
		Intent intent=getIntent();
		String code=intent.getStringExtra("code");
		Tools.initializeTools(this);
		//initialize the variables
		//==========================================================
		context=this;
		rootLayout=(LinearLayout)findViewById(R.id.spd_rootLayout);
		categorySelection=(Spinner)findViewById(R.id.spd_categorySelection);
		layouts=new HashMap<String,LinearLayout>();

		productCodeText=(EditText)findViewById(R.id.spd_productCode);
		productCodeText.setText(code);
		//==========================================================
		typeSelection=new Spinner(this);


		Category category=checkGTINCategory(code);
		if(category==null){
			//get all the categories from the database.
			categories= Tools.cf.selectAll(2);


		}else{
			categories=new ArrayList<Category>();
			categories.add(category);
		}

		Toast.makeText(this,categories.toString(),Toast.LENGTH_LONG).show();


		//create a list of strings for the categories selection spinner
		List<String> categoryChoices=new ArrayList<String>();

		//for each category add the category to the choices list
		//and create a unique layout with the id of category.
		for(int i=0;i<categories.size();i++){
			categoryChoices.add(categories.get(i).getCategory());
		}



		addGSFields();
		rootLayout.addView(typeSelection);




		QueryBuilder<Type,Integer> qb= Tools.tf.qb(2);
		List<Type> queryTypes=null;
		try{
			queryTypes=qb.where().ne("type","all").query();
		}catch(SQLException e){e.printStackTrace();}
		if(queryTypes!=null){
			for(int i=0;i<queryTypes.size();i++){
				createLayout(queryTypes.get(i).getId());
				//types.put(
					//	String.valueOf(queryTypes.get(i).getId()),
					//	queryTypes.get(i).getType()
					//	);
			}
		}

		Log.d("Layouts: ",layouts.toString());



		//List<String> typeChoices;
		//Log.d("DDEEBBUUGG:",layouts.toString());

		//create adapter with the choices for spinner
		ArrayAdapter<String> adapter=
				new ArrayAdapter<String>(
							this,
							android.R.layout.simple_spinner_item,categoryChoices
						);
		//finaly set the adapter to the spinner and add its listener
		categorySelection.setAdapter(adapter);
		//selection.setOnItemSelectedListener(itemSelectedListener);
		categorySelection.setOnItemSelectedListener(categorySelectedListener);
		typeSelection.setOnItemSelectedListener(typeSelectedListener);
		parseCode(code);
		//validateGSValues();
	}


	public void printMessage(String title,String message,Class<?> activity){
		final Class<?> a=activity;

		AlertDialog alertDialog=new AlertDialog.Builder(this).create();
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
							createActivity(a);
						}
					}
				});

		alertDialog.show();
	}
	public void addGSFields(){


		try{
			List <Type> type= Tools.tf.qb(2).where().eq("type","all").query();
			int id=type.get(0).getId();
			List<Field> fields= Tools.fs.qb(2).where().eq("type_id",id).query();



			for(int i=0;i<fields.size();i++){
				//Toast.makeText(this,"field:"+fields.get(i).getFieldId(),Toast.LENGTH_LONG).show();
				Field field=fields.get(i);
				try{
					if(field.getFieldType().equals("EditText")){
						Pattern p= Tools.paf.qb(2).where().eq("field_id",field.getId()).queryForFirst();

						TextF e=new TextF(this,field.getFieldName(),field.getFieldContent());
						e.setId(field.getFieldId());
						if(p!=null){
							e.setAI(p.getPattern()+";"+p.getPatternType());

						}
						rootLayout.addView(e);
						globalFieldsId.add(field.getFieldId());
					}else if(field.getFieldType().equals("Label")){
						TextView t=new TextView(this);
						t.setText(field.getFieldValue());
						rootLayout.addView(t);
					}
				}catch(SQLException e){
					e.printStackTrace();
				}

			}
		}catch(SQLException e){e.printStackTrace();}

	}

	public void createScanActivity(){
		Intent intent=new Intent(this, ScanningActivity.class);
		startActivity(intent);
	}


	public void createActivity(Class<?> activity){
		Intent intent=new Intent(this,activity);
		startActivity(intent);
	}


	//insert data of product
	public void insertData(HashMap<String,Object> values){

		//set standard AI's
		//==================
		String GTIN_ai="01";
		String SN_ai="21";
		//==================


		//get the field names according to the standard identifiers from hashmap (AI)
		//=========================================================
		String gtinFieldName=AItoField.get(GTIN_ai).getFieldName();
		String SNFieldName=AItoField.get(SN_ai).getFieldName();
		//=========================================================

		//get the field values from the values hashmap
		//=========================================================
		String productGTIN=values.get(gtinFieldName).toString();
		String productSN=values.get(SNFieldName).toString();
		//=========================================================





		//get the keys of hashmap
		Set<String> keys=values.keySet();
		Object k[]=keys.toArray();

		//create the category object of current selected category
		//and set the id to currentSelected
		Category category=new Category();
		category.setId(currentSelectedCategory);

		//create productfunctions helper object and create a product with the
		//code that we scanned and the category we have selected.
		//this product object will be used for insertion to database.
		//================================================
		GTIN product=new GTIN(productGTIN,category);
		//================================================

		try{
			//try to get a product with the GTIN we scanned and store it in productExists var
			//used to check if product exists.
			GTIN productExists= Tools.pf.qb(2).where().eq("GTIN_code",productGTIN).queryForFirst();
			//if productExists is null then insert the product of scanned GTIN into the database
			if(productExists==null){
				//insert the product
				if(!Tools.pf.insert(product,2)){
					Toast.makeText(this,"problem at product insertion",Toast.LENGTH_LONG).show();
					return;
				}
			}else{
				//else set the product id to the existent product id.
				product.setId(productExists.getId());
			}


		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		try{

			Type insertionType= Tools.tf.qb(2).where().eq("id",currentSelectedType).queryForFirst();

			//serial number insertion
			//create SerialNubmer object with values the scanned serial
			//and product_id the id of inserted or existent product.
			SerialNumber serial=new SerialNumber(productSN,product,insertionType);

			//insert the serial

			if(!Tools.sf.insert(serial,2)){
				Toast.makeText(this,"problem at product serial insertion",Toast.LENGTH_LONG).show();
				return;
			}




			//ATTRIBUTE INSERTION


			//create type object with the "all" type value.
			//TypeFunctions tf=new TypeFunctions(this);
			Type all= Tools.tf.qb(2).where().eq("type","all").queryForFirst();

			//get the selected type object from database
			Type selectedType= Tools.tf.qb(2).where().eq("id",currentSelectedType).queryForFirst();



			//get the global fields (same for all types)
			List<Field> fields= Tools.fs.qb(2).where().eq("type_id",all.getId()).or().eq("type_id",selectedType.getId()).query();

			//get the type fields (different for each type)
			//List<Field> typeFields=fs.qb(2).where().eq("type_id",selectedType.getId()).query();



			HashMap<String, Field> fieldsMap=new HashMap<String, Field>();
			for(int i=0;i<fields.size();i++){
				fieldsMap.put(fields.get(i).getFieldName(),fields.get(i));
			}


			//for each key-attribute except code insert the value to product information
			//table
			for(int i=0;i<k.length;i++){
				if(
						!k[i].toString().equals(gtinFieldName)
						&&
						!k[i].toString().equals(SNFieldName)
					){
					Log.d("K["+i+"]:",""+k[i]);
					//pi.create(serial,property,value,option)

					Field f=fieldsMap.get(k[i].toString());
					String val=values.get(k[i]).toString();



					if(!Tools.pif.create(serial,f,val,2)){
						Toast.makeText(this,"problem at attribute insertion",Toast.LENGTH_LONG).show();
					}


				}

			}
		}catch(SQLException e){
			e.printStackTrace();
			return;
		}
		AlertDialog alertDialog=new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Success");
		alertDialog.setMessage("� ������� ����� �� ��������");
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
				"OK",
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog,int which){
						// TODO Auto-generated method stub
						dialog.dismiss();
						createScanActivity();
					}
				});

		alertDialog.show();
		//createScanActivity();
	}

	public boolean validateFieldValues(HashMap<String,Object> values){
		Set<String> keys=values.keySet();
		Object k[]=keys.toArray();

		for(int i=0;i<k.length;i++){
			String format=values.get(k[i]).toString();
			String args[]=format.split(";");
			Log.d("Type:",args[1]);
			Log.d("Value",args[0]);
			if(args[1].equals("Integer")){
				try{
					Integer.parseInt(args[0]);
				}catch(NumberFormatException e){
					Toast.makeText(this,"Check your fields for valid numeric values",Toast.LENGTH_LONG).show();
					return false;
				}
			}else if(args[1].equals("String")){
				if(args[0].length()<3){
					Toast.makeText(this,"Check your fields for valid length",Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}//

		return true;

	}

	//checkRules function
	//returns an Error object defining the error Field and type
	//Error Types are: 	-1  for string value in numeric field
	//					-21 for length violation
	//					-22 for minimum length violation
	//					-23 for maximum length violation
	//will return error 0 in case of no error

	public Error checkRules(TextF field,String ai){
		//if the ai of field is not default value (-1)
		if(!ai.equals("-1")){
			//split the ai string with the ';' delimiter
			//the ai string is defined by the format: ai_Identifier;ai_Rule
			//an ai string with range is "01;s1-20
			//tokens[0]=>AI, tokens[1]=rule
			String tokens[]=ai.split(";");
			//initialize variables
			//identifier will be used for field data validation (numeric or string)
			//endRagne is set to -1 and will be the maximum length if the field has one
			String identifier="";
			String endRange="-1";
			Log.d("Data:","AI:"+tokens[0]+" Rule:"+tokens[1]);

			if(tokens[1].contains("-")){
				//if the ai rule contains range then split it by '-' character
				//and store it to ruleParts.
				//set the identifier to ruleParts[0] and
				//the endRange to ruleParts[1] ( format: [n|s]MIR-MAR)
				//ruleParts[0]= [n|s]MIR , ruleParts[1]=MAR
				String ruleParts[]=tokens[1].split("-");
				identifier=ruleParts[0];
				endRange=ruleParts[1];

			}else{
				//if the ai rule does not contain range then set the identifier
				//equal to the rule=> identifier = [n|s]LEN
				identifier=tokens[1];
			}
			//initialize min and maxRange variables
			int minRange=0;
			int maxRange=-1;
			//the typeCharacter is the first character of identifier
			String typeCharacter=identifier.substring(0,1);
			//replcace the typeCharacter in identifier section with ""
			//then parse the number to the minRange
			identifier=identifier.replace(typeCharacter,"");
			minRange=Integer.parseInt(identifier);
			//get the field value
			String text=field.getText().toString();
			//if the endRagne changes from "-1" then parse it to maxRange
			if(!endRange.equals("-1")) maxRange=Integer.parseInt(endRange);

			//if the typeCharacter is n for number
			//check if all characters are numeric
			//if not return error
			if(typeCharacter.equals("n")){
				char []t=text.toCharArray();
				for(char c:t){
					if(c<48 || c>57){
						Error e=new Error(field,"-1");
						return e;
					}
				}
			}

			//get length of input text
			int len=text.length();

			//if maxRange remains inited to -1
			//return len error (not equal)
			if(maxRange<0){
				if(len!=minRange){
					return new Error(field,"-21");
				}
			}else{
				//else
				//if length of text is less than min ragne return less than error
				if(len<minRange){
					return new Error(field,"-22");
				}
				//else if text length is greater than max range return greater than error
				if(len>maxRange){
					return new Error(field,"-23");
				}
			}




		}
		//return error zero.
		return new Error(field,"0");
	}

	public void setLayout(String id){
		//if first time then set the currentSelectedType to 1
		//add the respective layout
		if(currentSelectedType<0){
			currentSelectedType=1;
			rootLayout.addView(layouts.get("1"));
			return;
		}

		//if not first time
		//remove the view with the previous selected type and then add
		//the new selection layout.At the end set the current selected type to the id of type
		rootLayout.removeView(layouts.get(String.valueOf(currentSelectedType)));
		rootLayout.addView(layouts.get(id));
		currentSelectedType=Integer.parseInt(id);


	}


	//used to create the layout of each category.
	public void createLayout(int id){
		//init the layout
		LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setId(id);

		//PatternFunctions pf=new PatternFunctions(this);

		//create a string->Object hashmap to set the arguments for selection
		//in our Fields table. We make the selection with the category_id=id
		HashMap<String,Object> parameters=new HashMap<String,Object>();
		parameters.put("type_id",id);
		//get the fields of the category
		List<Field> fieldsResult= Tools.fs.selectWhere(parameters,2);
		//foreach field
		try{
			for(int i=0;i<fieldsResult.size();i++){
				//get the field
				Field field=fieldsResult.get(i);
				//get the type,id,name and value of field
				String type=field.getFieldType();
				int fieldId=field.getFieldId();
				String fieldName=field.getFieldName();
				String fieldValue=field.getFieldValue();
				String fieldContent=field.getFieldContent();
				/*Log.d("Field creation Debug,category:",String.valueOf(id));
				Log.d("Field creation Debug,field Id:",String.valueOf(fieldId));
				Log.d("Field creation Debug,field Name:",fieldName);
				Log.d("Field creation Debug,field Val:",fieldValue);
				Log.d("Field creation Debug,field Type:",type);*/
				if(type.equals("Label")){
					//if it's a label create a textview
					//and set the text to fieldValue.
					//finally add it to the layout
					TextView txtv=new TextView(this);
					txtv.setText(fieldValue);
					layout.addView(txtv);
				}else if(type.equals("EditText")){
					//if it's an edittext create one
					//and set it's id to the fieldId.
					//finally add it to the layout
					Pattern p= Tools.paf.qb(2).where().eq("field_id",fieldId).queryForFirst();
					
					
					TextF etxt=new TextF(this,fieldName,fieldContent);
					etxt.setId(fieldId);
					if(p!=null){
						etxt.setAI(p.getPattern()+";"+p.getPatternType());
					}
					layout.addView(etxt);
				}else if(type.equals("Button")){
					//if it's a button create one
					//and set it's text to the fieldValue
					//finally add it to the layout
					Button b=new Button(this);
					b.setText(fieldValue);
					b.setId(fieldId);
					layout.addView(b);
					b.setOnClickListener(buttonListener);
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		//Log.d("Debug:",fieldsResult.toString());
		//finally put the layout in the layouts hashmap. (category_id->layout)
		layouts.put(String.valueOf(id),layout);
		
		
		
	}
}
