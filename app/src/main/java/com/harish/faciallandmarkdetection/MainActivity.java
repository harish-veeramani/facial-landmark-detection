package com.harish.faciallandmarkdetection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    JavaCameraView mJCV;
    Mat mRGBA;
    public static final String TAG = "MainActivity";

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) mJCV.enableView();
            else super.onManagerConnected(status);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mJCV = findViewById(R.id.java_camera_view);
        mJCV.setVisibility(SurfaceView.VISIBLE);
        mJCV.setCvCameraViewListener(this);
    }

    protected void onResume(){
        super.onResume();

        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV loaded successfully");
            mLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG, "Not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }
    }

    protected void onPause(){
        super.onPause();
        if (mJCV != null) mJCV.disableView();
    }

    protected void onStop(){
        super.onStop();
        if (mJCV != null) mJCV.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
}
