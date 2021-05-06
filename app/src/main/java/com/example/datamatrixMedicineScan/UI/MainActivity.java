package com.example.datamatrixMedicineScan.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.datamatrixMedicineScan.R;
import com.example.datamatrixMedicineScan.dbHelper.DBMiddle;
import com.example.datamatrixMedicineScan.tools.Shared;


public class MainActivity extends AppCompatActivity {

	// buttons declaration
	// ---------------------------------------------------------
	private Button scanstartButton;
	private Button viewproductsButton;
	private Button viewcategoriesButton;
	// ---------------------------------------------------------


	// context, activities connected to MainActivity
	// ---------------------------------------------------------
	private Context cxt=this;
	private Class<?> activities[]=new Class<?>[]{ProductsListActivity.class,ScanningActivity.class};
	private HashMap<String,Class<?>> classTable=new HashMap<String,Class<?>>();
	// ---------------------------------------------------------

	// opencv algorithms
	// ---------------------------------------------------------
	private RadioGroup algorithmRadioGroup;
	private RadioButton withAlgorithmRadio;
	private RadioButton withoutAlgorithmRadio;

	
	private CheckBox grayscaleCheckBox;
	private CheckBox contrastCheckBox;
	private CheckBox blurCheckBox;
	private CheckBox deCheckBox;
	private CheckBox binarizeCheckBox;
	private CheckBox borderizeCheckBox;
	private CheckBox finalDilateCheckBox;
	// ---------------------------------------------------------



	// checkboxes
	// ---------------------------------------------------------
	private ArrayList<CheckBox> aCheckboxes;	// main algorithm flow checkboxes
	private ArrayList<CheckBox> bCheckboxes;	// border checkboxes
	private ArrayList<CheckBox> cCheckboxes;	// blur checkboxes
	private class CheckboxRule{
		private String rule;
		private CheckBox checkbox;
		
		public CheckboxRule(String rule,CheckBox checkbox){
			this.rule=rule;
			this.checkbox=checkbox;
		}
	}
	// ---------------------------------------------------------
	
	OnCheckedChangeListener checkedGrpListener=new OnCheckedChangeListener(){


		// RADIOBUTTON CHECKED CHANGED LISTENER
		// -----------------------------------------------------------------------------------------
		@Override
		public void onCheckedChanged(RadioGroup group,int checkedId){

			// if radiobutton "withAlgorithm" is selected, the algorithm checkboxes will be visible
			if(checkedId == R.id.ma_withAlgorithmRadio){
				for(int i=0; i<aCheckboxes.size(); i++){
					aCheckboxes.get(i).setVisibility(View.VISIBLE);								// set a_group visible
					if(i<bCheckboxes.size())bCheckboxes.get(i).setVisibility(View.VISIBLE);		// set b_group visible
					if(i<cCheckboxes.size())cCheckboxes.get(i).setVisibility(View.VISIBLE);		// set c_group visible
				}
				
			}else{
				// else disable selections and set invisible
				for(int i=0; i<aCheckboxes.size(); i++){
					aCheckboxes.get(i).setVisibility(View.INVISIBLE);							// invisible - a
					aCheckboxes.get(i).setChecked(false);										// disable - a

					// same for b,c
					if(i<bCheckboxes.size()){
						bCheckboxes.get(i).setVisibility(View.INVISIBLE);
						bCheckboxes.get(i).setChecked(false);
					}
					if(i<cCheckboxes.size()){
						cCheckboxes.get(i).setVisibility(View.INVISIBLE);
						cCheckboxes.get(i).setChecked(false);
					}
				}
				
			}
		}
	};
	// -----------------------------------------------------------------------------------------



	// ONCHECKED LISTENER FOR CHECKBOXES
	// ----------------------------------------------------------------------------------------
	CompoundButton.OnCheckedChangeListener checkedListener=new CompoundButton.OnCheckedChangeListener(){
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
			// find which checkbox was selected
			for(int i=0;i<aCheckboxes.size();i++){
				if(buttonView.getText().equals(aCheckboxes.get(i).getText())){
					if(isChecked){
						if(i!=aCheckboxes.size()-1){
							aCheckboxes.get(i+1).setEnabled(true);	// enable the next algorithm checkbox
						}
						break;
					}else {
						// else clear until that
						int limit = i + 1;
						for (i = aCheckboxes.size() - 1; i >= limit; i--) {
							aCheckboxes.get(i).setEnabled(false);
							aCheckboxes.get(i).setChecked(false);
						}
						break;
					}
				}
			}

		}
	};
	// ----------------------------------------------------------------------------------------

	View.OnClickListener buttonListener=new View.OnClickListener(){
		
		@Override
		public void onClick(View v){
			
			//get pressed button text.
			String txt=((Button)v).getText().toString();
			//compare the text to resources string of scan,viewProduct and viewList.
			//if scan then create a scan intent.
			//if viewProduct then create ...
			//if viewList then create...
			
			if(txt.equals(getString(R.string.ma_truncateDatabaseString))){
				truncateTable();
				return;
			}
			Shared.algorithmInfo=new HashMap<String,Boolean>();
			//save algorithm configuration
			//================================
			for(int i=0;i<aCheckboxes.size();i++){
				Shared.algorithmInfo.put(aCheckboxes.get(i).getText().toString(),aCheckboxes.get(i).isChecked());
				if(i<bCheckboxes.size()){
					Shared.algorithmInfo.put(bCheckboxes.get(i).getText().toString(),bCheckboxes.get(i).isChecked());
				}
				if(i<cCheckboxes.size()){
					Shared.algorithmInfo.put(cCheckboxes.get(i).getText().toString(),cCheckboxes.get(i).isChecked());
				}
			}
			
			createActivity(classTable.get(txt));
		}
	};
	
	
	
	
	public void printMessage(String title,String message,Class<?> activity){
		final Class<?> a=activity;
		
		AlertDialog alertDialog=new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		//alertDialog.setMessage("Check if numerics are correct");
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

	public void truncateTable(){
		//VALIDATE

		
		AlertDialog alertDialog=new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Truncate");
		alertDialog.setMessage("Are you sure?");
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
				"YES",
				new DialogInterface.OnClickListener(){
					
					@Override
					public void onClick(DialogInterface dialog,int which){
						// TODO Auto-generated method stub
						DBMiddle hlp=new DBMiddle(cxt);
						hlp.clearAll();
						hlp.dbSeed();
						dialog.dismiss();
						AlertDialog ad=new AlertDialog.Builder(cxt).create();
						ad.setTitle("Success");
						ad.setMessage("Truncated Successfully");
						ad.setButton(AlertDialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){
							
							@Override
							public void onClick(DialogInterface dialog,int which){
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						ad.show();
						
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"NO",new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog,int which){
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		alertDialog.show();
	}
	
	public void createActivity(Class<?> activity){
		Intent intent=new Intent(this,activity);
		startActivity(intent);
	}
	
	public void initializeCheckboxes(){
		/*CheckboxRule a1=new CheckboxRule("A1",grayscaleCheckBox);
		CheckboxRule a2=new CheckboxRule("A2",contrastCheckBox);
		CheckboxRule a3=new CheckboxRule("A3",deCheckBox);
		CheckboxRule a4=new CheckboxRule("A4",binarizeCheckBox);
		CheckboxRule a5=new CheckboxRule("A5",finalDilateCheckBox);
		
		CheckboxRule b1=new CheckboxRule("B1",blurCheckBox);
		CheckboxRule c1=new CheckboxRule("C1",borderizeCheckBox);
		*/
		aCheckboxes=new ArrayList<CheckBox>();
		bCheckboxes=new ArrayList<CheckBox>();
		cCheckboxes=new ArrayList<CheckBox>();
		
		aCheckboxes.add(grayscaleCheckBox);
		aCheckboxes.add(contrastCheckBox);
		aCheckboxes.add(deCheckBox);
		aCheckboxes.add(binarizeCheckBox);
		aCheckboxes.add(finalDilateCheckBox);
		
		grayscaleCheckBox.setChecked(true);
		contrastCheckBox.setChecked(true);
		deCheckBox.setChecked(true);
		binarizeCheckBox.setChecked(true);
		finalDilateCheckBox.setChecked(true);
		
		bCheckboxes.add(blurCheckBox);
		cCheckboxes.add(borderizeCheckBox);
		
		
	}

	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //get the buttons from the view.
        //=======================================================
        scanstartButton=(Button)findViewById(R.id.ma_scanStartButton);
        viewproductsButton=(Button)findViewById(R.id.ma_viewProductsButton);
        viewcategoriesButton=(Button)findViewById(R.id.ma_truncateDatabaseButton);
        
        algorithmRadioGroup=(RadioGroup)findViewById(R.id.ma_algorithmRadioGroup);
        withAlgorithmRadio=(RadioButton)findViewById(R.id.ma_withAlgorithmRadio);
        withoutAlgorithmRadio=(RadioButton)findViewById(R.id.ma_withoutAlgorithmRadio);
        
    	grayscaleCheckBox=(CheckBox)findViewById(R.id.ma_grayscaleCheckbox);
    	contrastCheckBox=(CheckBox)findViewById(R.id.ma_contrastCheckbox);
    	blurCheckBox=(CheckBox)findViewById(R.id.ma_blurCheckbox);
    	deCheckBox=(CheckBox)findViewById(R.id.ma_deCheckbox);
    	binarizeCheckBox=(CheckBox)findViewById(R.id.ma_binarizeCheckbox);
    	borderizeCheckBox=(CheckBox)findViewById(R.id.ma_borderizeCheckbox);
    	finalDilateCheckBox=(CheckBox)findViewById(R.id.ma_finalDilateCheckbox);
        
    	
    
    	
        //=======================================================
    	initializeCheckboxes();
        classTable.put(getString(R.string.ma_viewProductString),activities[0]);
        classTable.put(getString(R.string.ma_scanCodeString),activities[1]);
        //classTable.put(getString(R.string.ma_truncateDatabaseString),activities[2]);
     


        //set click listeners
        //=======================================================
        scanstartButton.setOnClickListener(buttonListener);
        viewproductsButton.setOnClickListener(buttonListener);
        viewcategoriesButton.setOnClickListener(buttonListener);
        
       // withAlgorithmRadio.setOnCheckedChangeListener(checkedListener);
        algorithmRadioGroup.setOnCheckedChangeListener(checkedGrpListener);
        
        for(int i=0;i<aCheckboxes.size();i++){
        	aCheckboxes.get(i).setOnCheckedChangeListener(checkedListener);
        	aCheckboxes.get(i).setEnabled(false);
        	
        	if(i<bCheckboxes.size()){
        		bCheckboxes.get(i).setOnCheckedChangeListener(checkedListener);
        		bCheckboxes.get(i).setEnabled(false);
        	}
        	if(i<cCheckboxes.size()){
        		cCheckboxes.get(i).setOnCheckedChangeListener(checkedListener);
        		cCheckboxes.get(i).setEnabled(false);
        	}
        	aCheckboxes.get(0).setEnabled(true);
        	bCheckboxes.get(0).setEnabled(true);
        	cCheckboxes.get(0).setEnabled(true);
        }
        
        
        
        //=======================================================
    }

}

