package org.gamejam4;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
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
    		
    		splittedMatrix = new Mat();
        }
        
        super.surfaceCreated(holder);
    }

	@Override
	protected Bitmap processFrame(VideoCapture capture) {
		capture.retrieve(mBgra, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGB);
		
		Imgproc.cvtColor(mBgra, mHSV , Imgproc.COLOR_RGB2HSV);

		Core.extractChannel(mHSV, splittedMatrix, 1);
		
		splittedMatrix.setTo(new Scalar(this.currentGreyscaleLevel));
				
		Core.insertChannel(splittedMatrix, mHSV, 1);
		
		Imgproc.cvtColor(mHSV, mRgba2, Imgproc.COLOR_HSV2RGB);
		
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
