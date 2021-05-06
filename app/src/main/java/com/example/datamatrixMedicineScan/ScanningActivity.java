package com.example.datamatrixMedicineScan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.HashMap;


public class ScanningActivity extends AppCompatActivity {


private JavaCameraView mOpenCvCameraView;
private Mat frameForProcess;
private Mat ffp;
private Class<?> activitiesList[]=new Class<?>[]{MainActivity.class, ImageActivity.class};
private HashMap<String,Class<?>> classMap=new HashMap<String,Class<?>>();



private int img_height;
private int img_width;

private int height_start;
private int width_start;

private int height_end;
private int width_end;


private final int box_width = 400;
private final int box_height = 400;

/*
* 	ADDED THIS LINE OF CODE TO MAKE IT RUN ON EMULATOR
*
* */
	static {
		OpenCVLoader.initDebug();

	}


	View.OnTouchListener touchListener=new View.OnTouchListener(){

		@Override
		public boolean onTouch(View v,MotionEvent event){
			// TODO Auto-generated method stub
			//createImageProcessing();

			// capture the image inside the green box
			// -------------------------------------------------------------------------------
			ffp=new Mat(box_height,box_width,frameForProcess.type());
			int row=0;
			int col=0;
			for(int i=height_start;i<=height_end;i++){

				for(int j=width_start;j<=width_end;j++){
					ffp.put(row,col++,frameForProcess.get(i,j));
				}
				row++;
				col=0;
			}
			// -------------------------------------------------------------------------------

			Shared.frameForProcess=ffp;
			createActivity(getString(R.string.sa_captureImageString));



			return false;
		}
	};



	//Camera frame listener
	//=========================================================================
	private CvCameraViewListener2 cameraListener=new CvCameraViewListener2(){
		
		@Override
		public void onCameraViewStopped(){
			// TODO Auto-generated method stub
			frameForProcess.release();
		}
		
		@Override
		public void onCameraViewStarted(int width,int height){
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public Mat onCameraFrame(CvCameraViewFrame inputFrame){
			frameForProcess = inputFrame.rgba();
		
	
			
			Thread t=new Thread(){
				public synchronized void start() {
					
				}


				public void run() {
					Size size=frameForProcess.size();
					img_height=(int)size.height;
					img_width=(int)size.width;
					
					width_start=img_width/2-box_width/2;
					width_end=img_width/2+box_width/2;
					
					height_start=img_height/2-box_height/2;
					height_end=img_height/2+box_height/2;
					
					for(int i=height_start;i<=height_end;i++){
						//for(int j=width_start;j<width_end;j++){
							double pixel[]=new double[4];
							//pixel[0]=pixel[1]=pixel[2]=pixel[3]=255;
							pixel[0]=0;pixel[1]=255;pixel[2]=0;pixel[3]=0;
							frameForProcess.put(i,width_start,pixel);
							frameForProcess.put(i,width_end,pixel);
							//}
						}
						
						for(int j=width_start;j<=width_end;j++){
							double pixel[]=new double[4];
							//pixel[0]=pixel[1]=pixel[2]=pixel[3]=255;
							pixel[0]=0;pixel[1]=255;pixel[2]=0;pixel[3]=0;
							frameForProcess.put(height_start,j,pixel);
							frameForProcess.put(height_end,j,pixel);
						}





// TODO - Implement decode without tap
/*
					// capture region of interest
					// capture the image inside the green box
					// -------------------------------------------------------------------------------
					ffp=new Mat(box_height,box_width,frameForProcess.type());
					int row=0;
					int col=0;
					for(int i=height_start;i<=height_end;i++){

						for(int j=width_start;j<=width_end;j++){
							ffp.put(row,col++,frameForProcess.get(i,j));
						}
						row++;
						col=0;
					}
					// -------------------------------------------------------------------------------

					// try and decode
					Bitmap bmInput=createBitMap(frameForProccess);
					Mat mOutput=optimizeImage(frameForProccess);

					//Bitmap bmOutput=createBitMap(grayscale);
					Bitmap bmOutput=createBitMap(mOutput);

*/
						
				}
			};
			t.run();
			return frameForProcess;
			
		}
	};
	//=========================================================================



//callback to check if opencv connected successfully 
	//========================================================================
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
		@Override
		public void onManagerConnected(int status){
		
			//if connected enable camera view.
			//else call default status.
			switch(status){
				case LoaderCallbackInterface.SUCCESS:{
					Log.i("a","opencv loaded successfully");
					mOpenCvCameraView.enableView();
					break;
				}
				default:{
					super.onManagerConnected(status);
				}
			}
		}
	};
	//========================================================================

	
	public void createActivity(String activity){
		Intent intent=new Intent(this,classMap.get(activity));
		startActivity(intent);
	}
	
	//TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanning);
		classMap.put(getString(R.string.sa_goBackString),activitiesList[0]);
		classMap.put(getString(R.string.sa_captureImageString),activitiesList[1]);
		
		//set flag for keeping the screen from going of.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		
		
		//get the JavaCameraView from the view and set it's visibility to visible 
	    mOpenCvCameraView = (JavaCameraView)findViewById(R.id.scanCamera);
	    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	   
	    //set listeners 
	    //set CvCameraViewListener with name cameraListener to get frames.
	    //set OnTouchListener with name touchListener for interaction.
	    mOpenCvCameraView.setCvCameraViewListener(cameraListener);
	    mOpenCvCameraView.setOnTouchListener(touchListener);
	    
	    //set the camera view focusable.
	    mOpenCvCameraView.setFocusable(true);
	    

		
		
	}
	
	
	
	/*
	*  COMMENTED OUT PREVIOUS IMPLEMENTATION
	*  IMPLEMENTED STATIC SO THAT IT CAN RUN ON EMULATOR
	* */
	
	@Override
    public void onResume(){
    	super.onResume();
    	//OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9,this,mLoaderCallback);
		if(mOpenCvCameraView != null) {
			mOpenCvCameraView.enableView();
		}
    }
	
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mOpenCvCameraView!=null){
			mOpenCvCameraView.disableView();
		}
	}
	
	
	
	


}
