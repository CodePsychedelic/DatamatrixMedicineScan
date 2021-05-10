package com.example.datamatrixMedicineScan.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datamatrixMedicineScan.R;
import com.example.datamatrixMedicineScan.util.ActivityHelper;
import com.example.datamatrixMedicineScan.util.Shared;
import com.example.datamatrixMedicineScan.util.Tools;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.example.datamatrixMedicineScan.dbFunctions.ProductFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.SerialFunctions;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;


// this is where the image processing and registration happens
public class ImageActivity extends AppCompatActivity {

	// imageviews for input and output presentation
	private ImageView input;
	private ImageView output;

	private TextView codeView;					// to present the code
	private TextView notifyMessageView;			// ?
	private Button signProductButton;			// button for registration
	private Button backToScanActivityButton;	// buttons to back
	private Button backToMainActivityButton;
	
	private String scannedCode;					// the code
	
	private Class<?> activitiesList[]=new Class<?>[]{MainActivity.class, ScanningActivity.class, SignProductDynamic.class};
	private HashMap<String,Class<?>> classMap=new HashMap<String,Class<?>>();
	private HashMap<String,Boolean> algorithmInfo= Shared.algorithmInfo;	// keeps the algorithm settings to apply
	private ActivityHelper activityHelper;

	// clicklisteners for the buttons
	// -------------------------------------------------------------------
	OnClickListener buttonListener = new OnClickListener(){

		@Override
		public void onClick(View v){
			String buttonClicked=((Button)v).getText().toString();
			if(buttonClicked.equals(getString(R.string.ia_signProductButtonString))){
				HashMap <String,Object> extra=new HashMap<String,Object>();
				extra.put("code",scannedCode);
				startActivity(activityHelper.createActivity(getString(R.string.ia_signProductButtonString),extra));
			}else if(buttonClicked.equals(getString(R.string.ia_backToScanningActivityString))){
				startActivity(activityHelper.createActivity(getString(R.string.ia_backToScanningActivityString),null));
			}else if(buttonClicked.equals(getString(R.string.general_mainReturnString))){
				startActivity(activityHelper.createActivity(getString(R.string.general_mainReturnString),null));
			}

		}
	};
	// -------------------------------------------------------------------


	public void setGtinAndSerial(String code){
		if(code.contains("~01") && code.contains("~21")){
			String searchStr=code;
			while( (searchStr.indexOf("~01"))!=-1){
				String temp=searchStr;
				searchStr="";
				for(int i=0;i<temp.length();i++){

				}
			}

			code.indexOf("~01");
			code.indexOf("~21");
		}

	}


	// should search if product already exists
	// ------------------------------------------------------------------------------------------------------------------------------
	public void searchProduct(String code){
		// todo -- perform parsing in here
		// init stuff
		// -----------------------------------------------------------------------------
		ProductFunctions productFunctions=new ProductFunctions(this);	//
		SerialFunctions sf=new SerialFunctions(this);
		HashMap<String,Object> parameters=new HashMap<String,Object>();
		parameters.put("code",code);
		// -----------------------------------------------------------------------------

		List<com.example.datamatrixMedicineScan.dbHelper.GTIN> result=null;
		com.example.datamatrixMedicineScan.dbHelper.GTIN existsProduct=null;
		SerialNumber existsSerial=null;
		boolean exists=false;
		try{
			result=productFunctions.qb(2).where().eq("GTIN_code",code).query();
			existsProduct=productFunctions.qb(2).where().eq("GTIN_code",code).queryForFirst();
			if(existsProduct!=null){
				existsSerial=sf.qb(2).where().eq("product_id",existsProduct.getId()).queryForFirst();
				if(existsSerial!=null){
					exists=true;
				}
			}
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//=productFunctions.selectWhere(parameters,2);
		//if(!result.isEmpty()){
		if(exists){
			notifyMessageView.setText("The product already exists in your database.");
			signProductButton.setVisibility(Button.INVISIBLE);
			// TODO - IMPLEMENT A SHOW PRODUCT BUTTON
		}else{
			notifyMessageView.setText("New product scanned - you can proceed into registration");
		}
	}
	// ------------------------------------------------------------------------------------------------------------------------------




	// This is where the optimization algorithms will be applied -- if selected
	// ------------------------------------------------------------------------------------------------------------------------------
	public Mat optimizeImage(Mat frame){
		Mat proccessFrame=frame;
		Mat kernel;
		Mat edged=new Mat(proccessFrame.size(), proccessFrame.type());

		if(algorithmInfo.get(getString(R.string.ma_grayscaleCheckString))) proccessFrame= Tools.convertToGrayscale(proccessFrame);
		if(algorithmInfo.get(getString(R.string.ma_blurCheckString))) Imgproc.GaussianBlur(proccessFrame,proccessFrame,new Size(3,3),3);
		if(algorithmInfo.get(getString(R.string.ma_contrastCheckString)))  proccessFrame=Tools.imAdjust(proccessFrame);
		if(algorithmInfo.get(getString(R.string.ma_edCheckString))){
			kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
			Imgproc.erode(proccessFrame,proccessFrame,kernel);
			Imgproc.dilate(proccessFrame,proccessFrame,kernel);
		}
		if(algorithmInfo.get(getString(R.string.ma_binarizeCheckString))) Imgproc.threshold(proccessFrame,proccessFrame,128,255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
		if(algorithmInfo.get(getString(R.string.ma_deCheckString))){
			kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
			Imgproc.dilate(proccessFrame,proccessFrame,kernel);
			Imgproc.erode(proccessFrame,proccessFrame,kernel);
		}
		if(algorithmInfo.get(getString(R.string.ma_borderizeCheckString))){
			Imgproc.Canny(proccessFrame,edged,60,190);
			int rowValues[]=new int[2];
			int colValues[]=new int[2];

			Tools.verticalEdges(edged,colValues);//
			Tools.horizontalEdges(edged,rowValues);
			proccessFrame=Tools.borderizeImage(proccessFrame,rowValues,colValues);
		}


		return proccessFrame;
	}
	// ------------------------------------------------------------------------------------------------------------------------------

	// receives a frame and tries to decode it
	// ------------------------------------------------------------------------------------------------------------------------------
	public String decodeDataMatrix(Mat frameForProccess){

		//input bitmap bmInput is a new bitmap with length the frameForProcess columns and height the frameForProcess rows.
		//convert the frameForProcess to bitmap with matToBitmap();
		Bitmap bmInput=Tools.createBitMap(frameForProccess);	// to bitmap
		Mat mOutput=optimizeImage(frameForProccess);	// apply algorithms
		Bitmap bmOutput=Tools.createBitMap(mOutput);			// create bitmap output

		// set them
		input.setImageBitmap(bmInput);
		output.setImageBitmap(bmOutput);

		// create a bitmap to decode
		int intArray[]=new int[bmOutput.getWidth()*bmOutput.getHeight()];
		bmOutput.getPixels(intArray, 0, bmOutput.getWidth(), 0, 0, bmOutput.getWidth(), bmOutput.getHeight());

		LuminanceSource source = new RGBLuminanceSource(bmOutput.getWidth(), bmOutput.getHeight(), intArray);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		String contents=null;

		Reader reader = new MultiFormatReader(); // use this otherwise ChecksumException

		try {
			Result result = reader.decode(bitmap);	// try to decode
			contents = result.getText(); 			// get the contents
			//byte[] rawBytes = result.getRawBytes();
			//BarcodeFormat format = result.getBarcodeFormat();
			//ResultPoint[] points = result.getResultPoints();
		}
		catch (NotFoundException e) { e.printStackTrace(); }
		catch (ChecksumException e) { e.printStackTrace(); }
		catch (FormatException e) { e.printStackTrace(); }


		Toast.makeText(this,"decoded: "+contents,Toast.LENGTH_LONG).show();
		return contents;
	}
	// ------------------------------------------------------------------------------------------------------------------------------


	// onCreate
	// ------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			// super
		setContentView(R.layout.activity_image);	// set the view

		// init classmap
		classMap.put(getString(R.string.general_mainReturnString),activitiesList[0]);
		classMap.put(getString(R.string.ia_backToScanningActivityString),activitiesList[1]);
		classMap.put(getString(R.string.ia_signProductButtonString),activitiesList[2]);

		activityHelper = new ActivityHelper(this, classMap);

		//initialize components
		//================================================================
		signProductButton=(Button)findViewById(R.id.ia_signProductButton);
		backToScanActivityButton=(Button)findViewById(R.id.ia_backButton);
		backToMainActivityButton=(Button)findViewById(R.id.ia_mainReturnButton);
		codeView=(TextView)findViewById(R.id.ia_codeView);
		notifyMessageView=(TextView)findViewById(R.id.ia_notifyMessageView);
		input=(ImageView)findViewById(R.id.imageInput);
		output=(ImageView)findViewById(R.id.imageResult);
		//================================================================


		//set listeners
		//================================================================
		signProductButton.setOnClickListener(buttonListener);
		backToScanActivityButton.setOnClickListener(buttonListener);
		backToMainActivityButton.setOnClickListener(buttonListener);
		//================================================================


		//get the value of scanned code. It will be null if the scan failed.
		String code=decodeDataMatrix(Shared.frameForProcess);

		//if it is null then notifify the user
		if(code==null) {
			codeView.setText("error-could not decode");
			signProductButton.setVisibility(Button.INVISIBLE);
		}
		else{
			// TODO -- FIX THIS SHIT
			//else set the code to our notification and
			//search if there is a product with that code.
			//if there is, show it to the user
			//else ask user to sign the product or scan again.
			setGtinAndSerial(code);
			codeView.setText(code);
			searchProduct(code);
			//set the variable productCode to the code for transfer
			scannedCode=code;
		}

	}
	// ------------------------------------------------------------------------------------------------------------------------------

}
