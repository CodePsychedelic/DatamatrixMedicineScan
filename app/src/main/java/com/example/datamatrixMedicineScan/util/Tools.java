package com.example.datamatrixMedicineScan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

import com.example.datamatrixMedicineScan.dbFunctions.CategoryFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.FSFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.PIFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.PatternFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.ProductFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.SerialFunctions;
import com.example.datamatrixMedicineScan.dbFunctions.TypeFunctions;
import com.example.datamatrixMedicineScan.dbHelper.Field;
import com.example.datamatrixMedicineScan.dbHelper.ProductAttributes;
import com.example.datamatrixMedicineScan.dbHelper.SerialNumber;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Tools{
	public static ProductFunctions pf;
	public static TypeFunctions tf;
	public static SerialFunctions sf;
	public static FSFunctions fs;
	public static PatternFunctions paf;
	public static CategoryFunctions cf;
	public static PIFunctions pif;



	private static double min;
	private static double max;

	public static void initializeTools(Context context){
		pf=new ProductFunctions(context);
		tf=new TypeFunctions(context);
		sf=new SerialFunctions(context);
		fs=new FSFunctions(context);
		paf=new PatternFunctions(context);
		cf=new CategoryFunctions(context);
		pif=new PIFunctions(context);
		
	}
	
	
	public static void reallocFields(DeleteBuilder<ProductAttributes,Integer> padb, List<Field> fields, List<Field> newFields, SerialNumber code) throws SQLException{
		for(int i=0;i<fields.size();i++){
			padb.where().eq("property_id",fields.get(i).getId()).and().eq("serial_id",code.getId());
			padb.delete();
		}
		for(int i=0;i<newFields.size();i++){
			//SerialNumber sn=Tools.sf.qb(2).where().eq("serialNumber",code).and().eq("product_id",productId).queryForFirst();
			Tools.pif.create(code,newFields.get(i),"-",2);
		}
		
	}


	//imAdjust implements matlab imadjust
	// ------------------------------------------------------------------------------------------------------------------------------
	public static Mat imAdjust(Mat frame){
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
	// ------------------------------------------------------------------------------------------------------------------------------

	// create bitmap
	// ------------------------------------------------------------------------------------------------------------------------------
	public static Bitmap createBitMap(Mat frame){
		Bitmap output=Bitmap.createBitmap(frame.cols(),frame.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(frame,output);
		return output;
	}
	// ------------------------------------------------------------------------------------------------------------------------------


	// convert to grayscale
	// ------------------------------------------------------------------------------------------------------------------------------
	public static Mat convertToGrayscale(Mat frame){
		Mat grayscale=new Mat(frame.height(),frame.width(), CvType.CV_8UC2);
		Imgproc.cvtColor(frame,grayscale,Imgproc.COLOR_RGB2GRAY);
		return grayscale;
	}
	// ------------------------------------------------------------------------------------------------------------------------------


	// find the vertical and horizontal edges of the frame
	// ------------------------------------------------------------------------------------------------------------------------------

	public static void verticalEdges(Mat edged, int []colValues){
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

	// minRow = row
	public static void horizontalEdges(Mat edged,int []rowValues){
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
	// ------------------------------------------------------------------------------------------------------------------------------



	// borderize will try to find the borders of the image -- trying to isolate the 2D barcode
	// ------------------------------------------------------------------------------------------------------------------------------
	public static Mat borderizeImage(Mat proccessFrame,int []rowValues,int []colValues){
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
	// ------------------------------------------------------------------------------------------------------------------------------
	
}
