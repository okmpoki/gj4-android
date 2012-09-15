package org.gamejam4;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

public class CameraPreviewDefaultView extends CameraBaseView implements OnTouchListener {

	private Mat mRgba;
	private Mat mBgra;
	private Mat mHSV;
	private Mat mRgba2;
	private Mat splittedMatrix;	
	private Mat targetRectangle;
	private Mat targetRectangleMat;
	private Mat targetRectangleMatThreshold;
	
	private Scalar redLowerBound = new Scalar(170, 100, 100);
	private Scalar redUpperBound = new Scalar(190, 255, 255);
	
	private Scalar greenLowerBound = new Scalar(45, 70, 70);
	private Scalar greenUpperBound = new Scalar(80, 255, 255);
	
	private Scalar blueLowerBound = new Scalar(115, 100, 100);
	private Scalar blueUpperBound = new Scalar(130, 255, 255);
	
	
	// Logcat tag
	private static final String TAG = "Example/ColorBlobDetection";
	
	public CameraPreviewDefaultView(Context context)
	{
        super(context);
        setOnTouchListener(this);
	}
	
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this) {
            // initialize Mat before usage
        	mRgba = new Mat();
        	mHSV = new Mat();
    		new Mat();
    		mRgba2 = new Mat();
    		mBgra = new Mat();
    		targetRectangle = new Mat();
    		targetRectangleMat = new Mat();
    		targetRectangleMatThreshold = new Mat();
    		splittedMatrix = new Mat();
        }
        
        super.surfaceCreated(holder);
    }

	@Override
	protected Bitmap processFrame(VideoCapture capture) {
		capture.retrieve(mBgra, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGB);
			      		
		Imgproc.cvtColor(mBgra, mHSV , Imgproc.COLOR_RGB2HSV);
		
		targetRectangleMat = mHSV.submat(	480 / 2 - 100, 
				480 / 2 + 100,				
				800 / 2 - 100, 
				800 / 2 + 100 												
		);

		Core.inRange(targetRectangleMat, this.redLowerBound, this.redUpperBound, targetRectangleMatThreshold);
		Log.e(TAG, "Count Red: " + Core.countNonZero(targetRectangleMatThreshold));
		
		Core.inRange(targetRectangleMat, this.greenLowerBound, this.greenUpperBound, targetRectangleMatThreshold);
		Log.e(TAG, "Count Green: " + Core.countNonZero(targetRectangleMatThreshold));
		
		Core.inRange(targetRectangleMat, this.blueLowerBound, this.blueUpperBound, targetRectangleMatThreshold);
		Log.e(TAG, "Count Blue: " + Core.countNonZero(targetRectangleMatThreshold));

		Core.extractChannel(mHSV, splittedMatrix, 1);
		
		splittedMatrix.setTo(new Scalar(this.currentGreyscaleLevel));
				
		Core.insertChannel(splittedMatrix, mHSV, 1);
		
		Imgproc.cvtColor(mHSV, mRgba2, Imgproc.COLOR_HSV2RGB);
		
    	Core.rectangle(mRgba2, 
    					new Point(800 / 2 - 100, 480 / 2 - 100),
    					new Point(800 / 2 + 100, 480 / 2 + 100),
    					 new Scalar(0,0,255),
    					 1
    	);    					
		
		Bitmap bmp = Bitmap.createBitmap(mRgba2.cols(), mRgba2.rows(), Bitmap.Config.ARGB_8888);

		try {
        	
        	Utils.matToBitmap(mRgba2, bmp);
        } catch(Exception e) {
        	Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            bmp = null;
        }
        
        return bmp;
	}
	
    @Override
    public void run() {
        super.run();

        synchronized (this) {
            // Explicitly deallocate Mats
            if (mRgba != null)
                mRgba.release();

            mRgba = null;
        }
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
