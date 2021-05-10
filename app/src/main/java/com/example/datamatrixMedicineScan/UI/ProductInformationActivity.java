package com.example.datamatrixMedicineScan.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datamatrixMedicineScan.R;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.example.datamatrixMedicineScan.dbFunctions.FSFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.PIFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.PatternFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.ProductFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.SerialFunctions;
import com.example.datamatrixMedicineScan.dbHelper.Field;
import com.example.datamatrixMedicineScan.dbHelper.GTIN;
import com.example.datamatrixMedicineScan.dbHelper.Pattern;
import com.example.datamatrixMedicineScan.dbHelper.ProductAttributes;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;

import com.example.datamatrixMedicineScan.util.TextF;
import com.example.datamatrixMedicineScan.util.Error;

import org.javatuples.Triplet;

// PRODUCT INFORMATION ACTIVITY -- WILL PRESENT MEDICINE PRODUCT INFORMATION
public class ProductInformationActivity extends AppCompatActivity {
	private LinearLayout root;
	private List<ProductAttributes> initialValues;
	
	private int serialId;
	private String serialNumber;
	private String gtinCode;
	
	
	// BUTTON CLICK LISTENER -- DOES ACTION ACCORDINGLY
	// ---------------------------------------------------------------------------
	private OnClickListener buttonListener=new OnClickListener(){
		
		@Override
		public void onClick(View v){

			String buttonPressed=((Button)v).getText().toString();

			if(buttonPressed.equals("update")){
				updateProductInformation();
			}else if(buttonPressed.equals("delete")){
				deleteProductInformation();
			}
			else if(buttonPressed.equals("return to list")){
				createActivity(ProductsListActivity.class);
			}else{
				createActivity(MainActivity.class);
			}
		}
	};
	// ---------------------------------------------------------------------------


	
	// simple createActivity
	// ---------------------------------------------------------------------------
	public void createActivity(Class <?> activity){
		Intent intent=new Intent(this,activity);
		startActivity(intent);
	}
	// ---------------------------------------------------------------------------

	// call deleteProductInformation service
	// ---------------------------------------------------------------------------
	public void deleteProductInformation(){
		ProductFunctions pf = new ProductFunctions(this);
		if(pf.deleteProductInformation(gtinCode, serialNumber)){
			// alert dialog that informs us and redirects us on products list
			// ----------------------------------------------------------------------------
			AlertDialog alertDialog=new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Success");
			alertDialog.setMessage("Product was deleted successfully");
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
					"OK",
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog,int which){
							dialog.dismiss();
							createActivity(ProductsListActivity.class);
						}
					});

			alertDialog.show();
			// ----------------------------------------------------------------------------
		}
	}
	// ---------------------------------------------------------------------------


	// Typical print message function
	// ----------------------------------------------------------------------------
	public void printMessage(String title,String message,Class<?> activity){
		final Class<?> a=activity;
		
		AlertDialog alertDialog=new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);

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
	// ----------------------------------------------------------------------------

	// call the updateProductInformation "Service". Inform user if problem or update and inform
	// ----------------------------------------------------------------------------
	public void updateProductInformation(){
		ProductFunctions pf = new ProductFunctions(this);
		Triplet eMsg = pf.updateProductInformation(findViewById(android.R.id.content).getRootView(), initialValues, gtinCode, serialNumber);
		if(eMsg != null){
			// error message
			printMessage(eMsg.getValue0().toString(),eMsg.getValue1().toString(),(Class)eMsg.getValue2());
			return;
		}else{
			// update inform
			AlertDialog alertDialog=new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Success");
			alertDialog.setMessage("The product was updated successfully");
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
		}
	}
	// ----------------------------------------------------------------------------

	// HELPER FUNCTION
	// ----------------------------------------------------------------------------
	public void getRules(){
		PatternFunctions pf=new PatternFunctions(this);
		try{
			List<Pattern> patterns=pf.qb(2).query();
			for(Pattern pattern:patterns){
				Log.d("pattern information:"+pattern.getPatternIdentifier(),"pattern rule:"+pattern.getPatternType());
			}
		}catch(SQLException e){e.printStackTrace();}
	}
	// ----------------------------------------------------------------------------


	//createView function CREATES THE WHOLE VIEW DYNAMICALLY
	// ----------------------------------------------------------------------------
	@SuppressLint("ResourceType")
	public void createView(){
		// create the main layout and sublayout
		// each time we add the TextView and EditText to the subLayout
		// We add the subLayout to the main layout.
		//=======================================
		LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout subLayout=new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);
		//=======================================

		// get pattern and fields functions
		PatternFunctions pf=new PatternFunctions(this);
		FSFunctions fsf=new FSFunctions(this);

		//create textview for GTIN Code
		//=======================================
		TextView codeView=new TextView(this);
		codeView.setText("Product GTIN:");
		//=======================================
		
		//create TextF for GTIN code value. Set the AI of field to 01 and initial id to 1
		//=======================================
		TextF codeText;
		try{
			Field f=fsf.qb(2).where().eq("fieldName","GTIN").queryForFirst();	// fetch GTIN field
			Pattern p=pf.qb(2).where().eq("field_id",f.getId()).queryForFirst();		// fetch its pattern
			codeText=new TextF(this,"productGTIN");
			codeText.setText(gtinCode);
			
			codeText.setAI(p.getPatternIdentifier()+";"+p.getPatternType());
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

		//create edittext input (textf) for serial number.Give AI of 21
		//=======================================
		try{
			Field f=fsf.qb(2).where().eq("fieldName","MedicineSN").queryForFirst();
			Pattern p=pf.qb(2).where().eq("field_id",f.getId()).queryForFirst();
			codeText=new TextF(this,"serialNumber");
			codeText.setText(serialNumber);
			codeText.setAI(p.getPatternIdentifier()+";"+p.getPatternType());
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

			// CREATE EACH ATTRIBUTE DYNAMICALLY
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
					Log.d("Field ai:",p.getPatternIdentifier());
					value.setAI(p.getPatternIdentifier()+";"+p.getPatternType());
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
	// ----------------------------------------------------------------------------


	// ----------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_information);
		ProductFunctions pf = new ProductFunctions(this);

		getRules();
		Intent intent=getIntent();
		gtinCode=intent.getStringExtra("GTIN");
		String code=intent.getStringExtra("code");
		serialNumber=code;
		serialId=pf.getProductSerialId(gtinCode, code);	// GET THE SELECTED SERIAL ID BY SERIAL CODE

		root=(LinearLayout)findViewById(R.id.ma_linearLayout);

		createView();	// dynamically create the view
	}
	// ----------------------------------------------------------------------------
}
