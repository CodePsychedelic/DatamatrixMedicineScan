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
import com.example.datamatrixMedicineScan.tools.Shared;
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

public class ImageActivity extends AppCompatActivity {

	private ImageView input;
	private ImageView output;
	
	private TextView codeView;
	private TextView notifyMessageView;
	private Button signProductButton;
	private Button backToScanActivityButton;
	private Button backToMainActivityButton;
	
	private String scannedCode;
	
	private Class<?> activitiesList[]=new Class<?>[]{MainActivity.class, ScanningActivity.class, SignProductDynamic.class};
	private HashMap<String,Class<?>> classMap=new HashMap<String,Class<?>>();
	private double min;
	private double max;
	private HashMap<String,Boolean> algorithmInfo= Shared.algorithmInfo;

	OnClickListener buttonListener = new OnClickListener(){

		@Override
		public void onClick(View v){
			String buttonClicked=((Button)v).getText().toString();
			if(buttonClicked.equals(getString(R.string.ia_signProductButtonString))){
				HashMap <String,Object> extra=new HashMap<String,Object>();
				extra.put("code",scannedCode);
				createActivity(getString(R.string.ia_signProductButtonString),extra);
			}else if(buttonClicked.equals(getString(R.string.ia_backToScanningActivityString))){
				createActivity(getString(R.string.ia_backToScanningActivityString),null);
			}else if(buttonClicked.equals(getString(R.string.general_mainReturnString))){
				createActivity(getString(R.string.general_mainReturnString),null);
			}

		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);


		classMap.put(getString(R.string.general_mainReturnString),activitiesList[0]);
		classMap.put(getString(R.string.ia_backToScanningActivityString),activitiesList[1]);
		classMap.put(getString(R.string.ia_signProductButtonString),activitiesList[2]);

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

	public void searchProduct(String code){

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
			notifyMessageView.setText("������� ������ �� ��� ������������ ������");
			signProductButton.setVisibility(Button.INVISIBLE);
		}else{
			notifyMessageView.setText("��� ������� ������ �� ��� �������� ������");
		}

	}


	public Bitmap createBitMap(Mat frame){
		Bitmap output=Bitmap.createBitmap(frame.cols(),frame.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(frame,output);
		return output;
	}


	public Mat convertToGrayscale(Mat frame){
		Mat grayscale=new Mat(frame.height(),frame.width(),CvType.CV_8UC2);
		Imgproc.cvtColor(frame,grayscale,Imgproc.COLOR_RGB2GRAY);


		return grayscale;
	}



	//imAdjust implements matlab imadjust
	public Mat imAdjust(Mat frame){
		//initialize min and max intensities from first pixel inside the white border
		//image inside the white border will be a mat of size(frame.rows()-2,frame.cols()-2)
		double pix[]=frame.get(1,1);
		min=pix[0];
		max=pix[0];

		//foreach pixel in the frame find the maximum and minimum intensity
		for(int i=1;i<frame.rows()-1;i++){
			for(int j=1;j<frame.cols()-1;j++){
				pix=frame.get(i,j);
				if(pix[0]>max) max=pix[0];
				if(pix[0]<min) min=pix[0];
			}

		}

		//create the new Mat to hold the adjusted image
		Mat adjustedImage=new Mat(frame.rows()-2,frame.cols()-2,frame.type());
		//foreach pixel in the mat
		for(int i=1;i<frame.rows()-1;i++){
			for(int j=1;j<frame.cols()-1;j++){
				//get the pixel and adjust it
				//((pixel-min)/(max-min))*255
				pix=frame.get(i,j);
				pix[0]=((pix[0]-min)/(max-min))*255;
				adjustedImage.put(i-1,j-1,pix);
			}
		}


		Log.d("min and max values:",min+" "+max);
		return adjustedImage;
	}


	public void verticalEdges(Mat edged,int []colValues){
		int minCol=edged.cols()-1;
		int maxCol=0;
		for(int i=0;i<edged.rows();i++){
			for(int j=0;j<edged.cols();j++){
				double pix[]=edged.get(i,j);
				if(pix[0]==255){
					if(minCol>j) minCol=j;
					if(maxCol<j) maxCol=j;
				}
			}
		}


		colValues[0]=minCol;
		colValues[1]=maxCol;
	}


	public void horizontalEdges(Mat edged,int []rowValues){
		int minRow=edged.rows()-1;
		int maxRow=0;
		for(int j=0;j<edged.cols();j++){
			for(int i=0;i<edged.rows();i++){
				double pix[]=edged.get(i,j);
				if(pix[0]==255){
					if(minRow>i) minRow=i;
					if(maxRow<i) maxRow=i;
				}
			}
		}

		rowValues[0]=minRow;
		rowValues[1]=maxRow;
	}



	public Mat borderizeImage(Mat proccessFrame,int []rowValues,int []colValues){
		//get row and col values
		//=======================
		int minRow=rowValues[0];
		int maxRow=rowValues[1];

		int minCol=colValues[0];
		int maxCol=colValues[1];
		//=======================

		//set an initial offset of dead zone to 5 pixels
		int s_offset=5;
		int e_offset=5;

		while(minRow-s_offset<0 || minCol-s_offset<0){
			s_offset--;
			if(s_offset==0) break;

		}
		/*
		if(minRow-s_offset<0 && minCol-s_offset<0){
			if(minRow<minCol) s_offset=minRow;
			else s_offset=minCol;
		}else{

			//if the minRow-offset<0 then set offset to minRow
			if(minRow-s_offset<0) s_offset=minRow;
			else s_offset=minCol;
			//if the minCol-offset<0 then set offset to minCol
			//if(minCol<offset){
				//offset=minCol;
			//}
		}
		*/

		while(maxRow+e_offset>proccessFrame.rows() || maxCol+e_offset>proccessFrame.cols()){
			e_offset--;
			if(e_offset==0) break;
		}

		//if(maxRow+s_offset>proccessFrame.rows() || maxCol+s_offset>proccessFrame.cols())
			//s_offset=0;

		Mat borderizedImage=new Mat(
				maxRow-minRow+s_offset+e_offset,
				maxCol-minCol+s_offset+e_offset,
				proccessFrame.type()
				);

		for(int i=minRow-s_offset;i<maxRow+e_offset;i++){
			for(int j=minCol-s_offset;j<maxCol+e_offset;j++){
				double pix[]=proccessFrame.get(i,j);
				borderizedImage.put(i-(minRow-s_offset),j-(minCol-s_offset),pix);
			}
		}

		return borderizedImage;
	}

	public Mat optimizeImage(Mat frame){
		Mat proccessFrame=frame;
		Mat kernel;
		Mat edged=new Mat(proccessFrame.size(),proccessFrame.type());


		if(algorithmInfo.get(getString(R.string.ma_grayscaleCheckString))) proccessFrame=convertToGrayscale(proccessFrame);
		if(algorithmInfo.get(getString(R.string.ma_contrastCheckString))) {
			proccessFrame=imAdjust(proccessFrame);
		}
		if(algorithmInfo.get(getString(R.string.ma_blurCheckString))) Imgproc.GaussianBlur(proccessFrame,proccessFrame,new Size(3,3),3);
		if(algorithmInfo.get(getString(R.string.ma_edCheckString))){
			kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
			Imgproc.erode(proccessFrame,proccessFrame,kernel);
			Imgproc.dilate(proccessFrame,proccessFrame,kernel);

		}

		if(algorithmInfo.get(getString(R.string.ma_binarizeCheckString))){
			Imgproc.threshold(proccessFrame,proccessFrame,128,255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
		}

		if(algorithmInfo.get(getString(R.string.ma_deCheckString))){
			kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
			Imgproc.dilate(proccessFrame,proccessFrame,kernel);
			Imgproc.erode(proccessFrame,proccessFrame,kernel);
		}
		if(algorithmInfo.get(getString(R.string.ma_borderizeCheckString))){
			Imgproc.Canny(proccessFrame,edged,60,190);
			int rowValues[]=new int[2];
			int colValues[]=new int[2];

			verticalEdges(edged,colValues);//
			horizontalEdges(edged,rowValues);
			proccessFrame=borderizeImage(proccessFrame,rowValues,colValues);
		}

		return proccessFrame;
	}

	public String decodeDataMatrix(Mat frameForProccess){
		//input frame from camera.
				Mat frameForProcess= Shared.frameForProcess;
				
				//input bitmap bmInput is a new biymap whith length the frameForProcess columns
				//and height the frameForProcess rows.
				//convert the frameForProcess to bitmap with matToBitmap();
				Bitmap bmInput=createBitMap(frameForProccess);
				Mat mOutput=optimizeImage(frameForProccess);
			
				//Bitmap bmOutput=createBitMap(grayscale);
				Bitmap bmOutput=createBitMap(mOutput);
			
				
				input.setImageBitmap(bmInput);
				output.setImageBitmap(bmOutput);
				
				//
				
				int intArray[]=new int[bmOutput.getWidth()*bmOutput.getHeight()];
				bmOutput.getPixels(intArray, 0, bmOutput.getWidth(), 0, 0, bmOutput.getWidth(), bmOutput.getHeight());
				
				LuminanceSource source = new RGBLuminanceSource(bmOutput.getWidth(), bmOutput.getHeight(), intArray);
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				String contents=null;
			
				Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
				
				//Reader reader=new DataMatrixReader();
				try {
				        Result result = reader.decode(bitmap);
				        contents = result.getText(); 
				        //byte[] rawBytes = result.getRawBytes(); 
				        //BarcodeFormat format = result.getBarcodeFormat(); 
				        //ResultPoint[] points = result.getResultPoints();
				    } catch (NotFoundException e) { e.printStackTrace(); } 
				    catch (ChecksumException e) { e.printStackTrace(); }
				    catch (FormatException e) { e.printStackTrace(); } 
				    
				    
				    Toast.makeText(this,"decoded: "+contents,Toast.LENGTH_LONG).show();
				    return contents;
	}

	
	
}
