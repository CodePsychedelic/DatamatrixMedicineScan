package com.example.datamatrixMedicineScan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import dbFunctions.FSFunctions;
import dbFunctions.PIFunctions;
import dbFunctions.PatternFunctions;
import dbFunctions.ProductFunctions;
import dbFunctions.SerialFunctions;
import dbHelper.Field;
import dbHelper.GTIN;
import dbHelper.Pattern;
import dbHelper.ProductAttributes;
import dbHelper.SerialNumber;

public class ProductInformationActivity extends AppCompatActivity {
	private LinearLayout root;

	private List<ProductAttributes> initialValues;
	
	private int serialId;
	private String serialNumber;
	
	private int gtinId;
	private String gtinCode;
	
	
	
	private OnClickListener buttonListener=new OnClickListener(){
		
		@Override
		public void onClick(View v){
			// TODO Auto-generated method stub
			String buttonPressed=((Button)v).getText().toString();
			if(buttonPressed.equals("update")){
				updateProductInformation();
			}else if(buttonPressed.equals("delete")){
				deleteProductInformation();
			}
			else if(buttonPressed.equals("return to list")){
				createActivity(ProductSerialList.class);
			}else{
				createActivity(MainActivity.class);
			}
		}
	};
	
	private class TextF extends EditText{
		private String name;
		private String ai="-1";
		
		public TextF(Context context,String name){
			super(context);
			this.name=name;
		}
		
		private String getName(){
			return name;
		}
		
		private String getAI(){
			return ai;
		}
		
		private void setName(String name){
			this.name=name;
		}
	
		private void setAI(String ai){
			this.ai=ai;
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

	
	
	public void createActivity(Class <?> activity){
		Intent intent=new Intent(this,activity);
		startActivity(intent);
	}
	
	public void deleteProductInformation(){
		ProductFunctions pf=new ProductFunctions(this);
		PIFunctions pif=new PIFunctions(this);
		SerialFunctions sf=new SerialFunctions(this);
		
		//HashMap<String,Object> params=new HashMap<String,Object>();
		//params.put("code",serialNumber);
		//List<GTIN> res=pf.selectWhere(params,2);
		
		//GTIN product=res.get(0);
		try{
			GTIN product=pf.qb(2).where().eq("GTIN_code",gtinCode).queryForFirst();
			
			DeleteBuilder <SerialNumber,Integer> db=sf.db(2);
			db.where().eq("serialNumber",serialNumber).and().eq("product_id",product.getId());
			db.delete();
			
			List<SerialNumber> productSerialList=sf.qb(2).where().
			eq("product_id",product.getId()).query();
			//
			if(productSerialList.size()==0){
				DeleteBuilder <GTIN,Integer> pdb=pf.db(2);
				pdb.where().eq("GTIN_code",gtinCode);
				pdb.delete();
			}
		
			
			AlertDialog alertDialog=new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Success");
			alertDialog.setMessage("� �������� ����� �� ��������");
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
					"OK",
					new DialogInterface.OnClickListener(){
						
						@Override
						public void onClick(DialogInterface dialog,int which){
							// TODO Auto-generated method stub
							dialog.dismiss();
							createActivity(ProductsListActivity.class);
						}
					});
			
			alertDialog.show();
		}catch(SQLException e){e.printStackTrace();}
	}
	
	//update the product information
	public void updateProductInformation(){
		
		int id=1;
		View v;
		//variables newCode, vakyesMaping and values
		//newCode stores the updatedCode
		//valuesMapping is like (property->[oldValue,newValue])
		//
		String newGTIN="";
		String newSerial="";
		HashMap<String,String[]> valuesMapping=new HashMap<String,String[]>();
		HashMap<String,Object> values=new HashMap<String,Object>();
		
		//while there are views with the incrementing id
		while((v=findViewById(id++))!=null){
			//if the view is TextF 
			if(v instanceof TextF){
				//get it
				TextF field=((TextF)v);
				Log.d("textField "+((TextF)v).getAI()+" "+((TextF)v).getName()+":",((TextF)v).getText().toString());
				//if the name of the field is the product code store it to the newCode
				//if(field.getAI().equals())
				int result;
			
				Error e=checkRules(field,field.getAI());
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
				
				
				
				if(field.getName().equals("serialNumber")){
					newSerial=field.getText().toString();
				}else if(field.getName().equals("productGTIN")){
					newGTIN=field.getText().toString();
				}else{
					//else put the other data in the values hashmap.
					//like fieldName->fieldValue
					//values.put(field.getName(),field.getText().toString());
					//values.put(field.getName(),field);
					
					valuesMapping.put(field.getName(),new String[]{"",field.getText().toString()});
				}
			}
		}
		
		
		/*
		//get the keys (field names or properties values) of values hashmap
		Object keys[]=values.keySet().toArray();
		//foreach of the keys
		for(int i=0;i<keys.length;i++){
			//add the new values to the values mapping 
			TextF field=((TextF)values.get(keys[i]));
			valuesMapping.put(keys[i].toString(),new String[]{"",field.getText().toString()});
		}
		
		*/
		//foreach initialValues
		for(int i=0;i<initialValues.size();i++){
			//add the old values to the values mapping
			
			//get the table container of values from the values mapping using the 
			//stored ProductInfromationSchema object getProperty as pointer
			String table[]=valuesMapping.get(initialValues.get(i).getPropertyId().getFieldName());
			//set the 0 position to old value with getValue
			table[0]=initialValues.get(i).getValue();
			//put the table back 
			valuesMapping.put(initialValues.get(i).getPropertyId().getFieldName(),table);
			Log.d("Report"+initialValues.get(i).getPropertyId().getFieldName()+":",
					"Old value:"+table[0]+" ,New Value:"+table[1]);
		}
		
		
		
		
		
		//
		ProductFunctions pf=new ProductFunctions(this);
		SerialFunctions sf=new SerialFunctions(this);
		PIFunctions pif=new PIFunctions(this);
		boolean r=false;
		ProductAttributes pisOld=new ProductAttributes();
		
		//get the product with the old code
		
		GTIN updatedGTIN=null;
		SerialNumber updatedSerial=null;
		
		try{
			//get the old gtin object (product) from the database
			GTIN oldProduct=pf.qb(2).where()
					.eq("GTIN_code",gtinCode).
					queryForFirst();
			//get the old serial number from the database
			//based on serial number value and gtin id
			SerialNumber oldSn=sf.qb(2).where().
					eq("serialNumber",serialNumber).
					and().
					eq("product_id",oldProduct.getId()).
					queryForFirst();
			
		
			
			//check if there is a product with gtin of value
			//that we want (newGTIN)
			GTIN existsProduct=pf.qb(2).
					where().eq("GTIN_code",newGTIN).
					queryForFirst();
	
			
			//CASE OF EXISTING PRODUCT GLOBAL TRADE INFORMATION NUMBER (GTIN)
			//===============================================================
			
			//if product with new GTIN code exists
			if(existsProduct!=null){
				//check if serial number with new GTIN combination exists
				SerialNumber existsSn=sf.qb(2).where().
						eq("serialNumber",newSerial).
						and().
						eq("product_id",existsProduct.getId()).
						queryForFirst();
				
				updatedGTIN=existsProduct;
				//updatedSerial=new SerialNumber(newSerial,existsProduct);
				//if exists
				if(existsSn!=null){
					//if the existing product gtin code is not the same
					//with the old product gtin code
					//and the existing serial is not the same with the old serial
					//then we cannot use the comp
			
					
					if(existsProduct.getCode().equals(oldProduct.getCode())
							&&
							!existsSn.getSerialNumber().equals(oldSn.getSerialNumber())
							)
					{
						//same gtin different serial
						printMessage("already exists","� ������� ������ ������� ���� ���� GTIN",null);
						return;
					}else if(!existsProduct.getCode().equals(oldProduct.getCode())){
						printMessage("already exists","� ������� ������ ������� �� ����������� GTIN",null);
						return;
					}
	
					updatedSerial=oldSn;
						//}
				}else{
					//if the serial that we want to give is not existent then we update
					//it according to the GTIN of existing product
					
					if(existsProduct.getCode().equals(oldProduct.getCode())){
						//update in current gtin
						UpdateBuilder<SerialNumber,Integer> ub=sf.ub(2);
						ub.where().
						eq("serialNumber",serialNumber).
						and().
						eq("product_id",oldProduct.getId());
						
						ub.updateColumnValue("serialNumber",newSerial);
						ub.update();
						
						updatedSerial=sf.qb(2).
								where().
								eq("serialNumber",newSerial).
								and().
								eq("product_id",oldProduct.getId()).
								queryForFirst();
		
					}else{
						
						//gtin exists not the same
						//update the gtin pointer in old serial
						//update the old serial to new serial...
						//check the remaining serial to old gtin.
						
						
						
						if(existsProduct.getProductCategory().getId()==oldProduct.getProductCategory().getId()){
								
							//set the update builder to point
							//the serial number of product with old GTIN
							//============================================
							UpdateBuilder<SerialNumber,Integer> ub=sf.ub(2);
							ub.where().
							eq("serialNumber",serialNumber).
							and().
							eq("product_id",oldProduct.getId());
							//============================================
							//update the serial number value and gtin pointer
							//===============================================
							ub.updateColumnValue("product_id",existsProduct.getId());
							ub.updateColumnValue("serialNumber",newSerial);
							//===============================================
							
							ub.update();
							
							//get the extra fields of old category
							
							
							
							
							List<SerialNumber> slist=sf.qb(2).where().eq("product_id",oldProduct.getId()).query();
							Log.d("LIST SIZE",slist.size()+"");
							if(slist.size()==0){
								DeleteBuilder<GTIN,Integer>pdb=pf.db(2);
								pdb.where().eq("GTIN_code",gtinCode);
								pdb.delete();
							}
							//SerialNumber newSn=new SerialNumber(newSerial,existsProduct);
							//sf.insert(newSn,2);
							
							updatedSerial=sf.qb(2).
									where().
									eq("serialNumber",newSerial).
									and().
									eq("product_id",existsProduct.getId()).
									queryForFirst();
							
							
								/*List<Field> fields=Tools.fs.qb(2).where().
										eq("type_id",oldSn.getType().getId()).query();
								
								Type defaultType=Tools.tf.qb(2).where().
										eq("category_id",existsProduct.getProductCategory().getId()).queryForFirst();
								
								List<Field> newFields=Tools.fs.qb(2).where().
										eq("type_id",defaultType.getId()).
										and().not().like("fieldName","%Button").
										and().not().like("fieldName","%Label").query();
								
								Tools.reallocFields(Tools.pif.db(2),fields,newFields,updatedSerial);
								
								r=true;*/
						}else{
							printMessage("category","clone OR same category change",null);
							return ;
						}
				
					}
				}
				
				
				
				
				
			}
			//===============================================================
			
			//CASE OF NON EXISTENT PRODUCT GLOBAL TRADE INFORMATION NUMBER (PRODUCT GTIN)
			//===============================================================
			else{
				//if we do not have a product with the new GTIN
				//we can create it and insert the serial number
				
				//create the new GTIN
				//update the GTIN pointer in old serial to new GTIN
				//update the old serial to new serial
				//check list of old gtin
			
		
				GTIN ngtin=new GTIN(newGTIN,oldProduct.getProductCategory());
				pf.insert(ngtin,2);
				ngtin=pf.qb(2).where().eq("GTIN_code",newGTIN).queryForFirst();
				
				UpdateBuilder <SerialNumber,Integer> ub=sf.ub(2);
				ub.where().
				eq("serialNumber",serialNumber).
				and().
				eq("product_id",oldProduct.getId());
				
				ub.updateColumnValue("serialNumber",newSerial);
				ub.updateColumnValue("product_id",ngtin.getId());
				
				ub.update();
				
				List<SerialNumber> slist=sf.qb(2).where().eq("product_id",oldProduct.getId()).query();
				Log.d("LIST SIZE:",slist.size()+"");
				if(slist.size()==0){
					DeleteBuilder<GTIN,Integer> pdb=pf.db(2);
					pdb.where().eq("GTIN_code",gtinCode);
					pdb.delete();
				}
				
				
				updatedGTIN=pf.qb(2).where().
						eq("GTIN_code",newGTIN).
						and().
						eq("category_id",ngtin.getProductCategory().getId()).
						queryForFirst();
				
				//updatedSerial=newSerialNumber;
				updatedSerial=sf.qb(2).where().
						eq("serialNumber",newSerial).
						and().
						eq("product_id",ngtin.getId()).
						queryForFirst();
			}
			
			//===============================================================

		}catch(SQLException e){e.printStackTrace();}
		
		
		Object []keys=valuesMapping.keySet().toArray();
		try{
			for(int i=0;i<keys.length;i++){
				String val[]=valuesMapping.get(keys[i]);
				HashMap<String,Object> oldValues=new HashMap<String,Object>();
				HashMap <String,Object> newValues=new HashMap<String,Object>();
				
				
				FSFunctions fs=new FSFunctions(this);
				Field field=fs.qb(2).where().eq("fieldName",keys[i].toString()).queryForFirst();
				Log.d("Field",field.getFieldName());
				
				ProductAttributes oldS=pif.qb(2).where().
						eq("serial_id",updatedSerial.getId()).
						and().
						eq("property_id",field).
						and().
						eq("value",val[0]).queryForFirst();
				
				//pisOld=oldStuff.get(0);
				
				
				if(oldS==null){
					Log.d("not","existent");
					Log.d("info",updatedSerial.getId()+" "+field.getFieldName()+" "+val[0]);
				}else{
					newValues.put("property",field);
					newValues.put("value",val[1]);
					newValues.put("serial",updatedSerial);
					pif.update(oldS,newValues,2);
				}
				
				//update the product information with the old schema and new schema
				
				
				}
			//QueryBuilder<ProductInformationSchema,Integer> qb=pif.getQb(2);
			AlertDialog alertDialog=new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Success");
			alertDialog.setMessage("� ������ ����� �� ��������");
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
					"OK",
					new DialogInterface.OnClickListener(){
						
						@Override
						public void onClick(DialogInterface dialog,int which){
							// TODO Auto-generated method stub
							dialog.dismiss();
							createActivity(ProductsListActivity.class);
						}
					});
			
			alertDialog.show();
			
	
		
		
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		

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
	
	
	public void getRules(){
		PatternFunctions pf=new PatternFunctions(this);
		try{
			List<Pattern> patterns=pf.qb(2).query();
			for(Pattern pattern:patterns){
				Log.d("pattern information:"+pattern.getPattern(),"pattern rule:"+pattern.getPatternType());
			}
		}catch(SQLException e){e.printStackTrace();}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_information);
		
		
		getRules();
		Intent intent=getIntent();
		gtinCode=intent.getStringExtra("GTIN");
		String code=intent.getStringExtra("code");
		serialNumber=code;
		serialId=getProductSerialId(code);
		//HashMap<String,Object> values=(HashMap<String,Object>)intent.getSerializableExtra("values");
		
		root=(LinearLayout)findViewById(R.id.ma_linearLayout);
		
		createView();
		
	}
	
	
	public int getProductGtinId(String gtinCode){
		ProductFunctions pf=new ProductFunctions(this);
		try{
			GTIN gtin=pf.qb(2).where().eq("GTIN_code",gtinCode).queryForFirst();
			return gtin.getId();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getProductSerialId(String serialNumber){
		SerialFunctions sf=new SerialFunctions(this);
		//ProductFunctions pf=new ProductFunctions(this);
		try{
			//GTIN gtin=pf.qb(2).where().eq("GTIN_code",gtinCode).queryForFirst();
			int pid=getProductGtinId(gtinCode);
			SerialNumber serial=sf.qb(2).where()
					.eq("serialNumber",serialNumber)
					.and()
					.eq("product_id",pid).queryForFirst();
			return serial.getId();
		}catch(SQLException e){e.printStackTrace();}
		return 0;
	}
	
	
	//createView function
	public void createView(){
		//create the main layout and sublayout
		//each time we add the TextView and EditText to the subLayout
		//We add the subLayout to the main layout.
		//=======================================
		LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout subLayout=new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);
		PatternFunctions pf=new PatternFunctions(this);
		FSFunctions fsf=new FSFunctions(this);
		//=======================================
		//create textview for GTIN Code
		//=======================================
		TextView codeView=new TextView(this);
		codeView.setText("Product GTIN:");
		//=======================================
		
		//create TextF for GTIN code value.Set the AI of field to 01 and initial
		//id to 1
		//=======================================
		TextF codeText;
		try{
			Field f=fsf.qb(2).where().eq("fieldName","GTIN").queryForFirst();
			Pattern p=pf.qb(2).where().eq("field_id",f.getId()).queryForFirst();
			codeText=new TextF(this,"productGTIN");
			codeText.setText(gtinCode);
			
			codeText.setAI(p.getPattern()+";"+p.getPatternType());
			codeText.setId(1);
		}catch(SQLException e){
			e.printStackTrace();
			return;
		}
		
		//=======================================
		
		//add the TextView and EditText to subLayout and then add the
		//subLayout to main Layout.
		//=======================================
		subLayout.addView(codeView);
		subLayout.addView(codeText);
		layout.addView(subLayout);
		//=======================================
		
		//reinitialize the subLayout
		//=======================================
		subLayout=new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);
		//=======================================
		
		//create textview for product serial
		//=======================================
		codeView=new TextView(this);
		codeView.setText("Product Serial:");
		//=======================================
		//create edittext input (textf) for serial number.Give 
		//AI of 21
		//=======================================
		try{
			Field f=fsf.qb(2).where().eq("fieldName","MedicineSN").queryForFirst();
			Pattern p=pf.qb(2).where().eq("field_id",f.getId()).queryForFirst();
			codeText=new TextF(this,"serialNumber");
			codeText.setText(serialNumber);
			codeText.setAI(p.getPattern()+";"+p.getPatternType());
			codeText.setId(2);
		}catch(SQLException e){
			e.printStackTrace();
			return;
		}
		//=======================================
		
		//same add
		//=======================================
		subLayout.addView(codeView);
		subLayout.addView(codeText);
		layout.addView(subLayout);
		//=======================================
	
		//product attributes functions object
		PIFunctions pif=new PIFunctions(this);
		try{
			//get the attributes of product with selected serial number 
		
			
			initialValues=pif.qb(2).orderBy("id",true).where().eq("serial_id",serialId).query();
			//foreach attribute
			for(int i=0;i<initialValues.size();i++){
				//re init subLayout
				//=======================================
				subLayout=new LinearLayout(this);
				subLayout.setOrientation(LinearLayout.VERTICAL);
				//=======================================
				Log.d("ID!!!::::",""+initialValues.get(i).getId());
				//create TextView for attribute
				//==============================================
				TextView field=new TextView(this);
				field.setText(initialValues.get(i).getPropertyId().getFieldName());
				//==============================================
				
				//get the field with attribute name and it's id
				//==============================================
				//FSFunctions fs=new FSFunctions(this);
				//Field ifield=fs.qb(2).where().eq("id",initialValues.get(i).getPropertyId()).queryForFirst();
				//int fid=ifield.getId();
				int fid=initialValues.get(i).getPropertyId().getId();
				Log.d("Field id:",initialValues.get(i).getPropertyId()+"");
				//==============================================
				
				//get the pattern with the id of field 
				//==============================================
				Pattern p=pf.qb(2).where().eq("field_id",fid).queryForFirst();
				//==============================================
				
				//create EditText with property value as value.
				//set the id from 4 to initialValues.size()-2 (i+3)
				//=======================================
				TextF value=new TextF(this,initialValues.get(i).getPropertyId().getFieldName().toString());
				value.setText(initialValues.get(i).getValue());
				value.setId(i+3);
				//=======================================
				
				//if we have a field with pattern then set it.
				//=======================================
				if(p!=null){
					Log.d("Field ai:",p.getPattern());
					value.setAI(p.getPattern()+";"+p.getPatternType());
					//value.setAI(p.getPattern());
					//value.setText(p.getPattern());
				}
				//=======================================
				
				//same add
				//=======================================
				subLayout.addView(field);
				subLayout.addView(value);
				layout.addView(subLayout);
				//=======================================
			}
		}catch(SQLException e){e.printStackTrace();}
		//create the layout buttons.
		//=======================================
		Button updateButton=new Button(this);
		Button deleteButton=new Button(this);
		Button returnButton=new Button(this);
		Button returnMainMenuButton=new Button(this);
		//=======================================
		
		//set the text of buttons.
		//=======================================
		updateButton.setText("update");
		deleteButton.setText("delete");
		returnButton.setText("return to list");
		returnMainMenuButton.setText("return to main menu");
		//=======================================
		
		//add the buttons to main layout
		//=======================================
		layout.addView(updateButton);
		layout.addView(deleteButton);
		layout.addView(returnButton);
		layout.addView(returnMainMenuButton);
		//=======================================
		
		//set click listeners for buttons
		//=======================================
		updateButton.setOnClickListener(buttonListener);
		deleteButton.setOnClickListener(buttonListener);
		returnButton.setOnClickListener(buttonListener);
		returnMainMenuButton.setOnClickListener(buttonListener);
		//=======================================
		
		//finally add the main layout (the sum of all the stuff) to 
		//the root layout
		root.addView(layout);
	}


}
