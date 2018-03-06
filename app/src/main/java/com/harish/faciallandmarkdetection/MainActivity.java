package com.harish.faciallandmarkdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private void saveImage(Mat subImg){
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(subImg.cols(), subImg.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(subImg, bitmap);
        } catch (CvException e){
            Log.d(TAG, e.getMessage());
        }

        subImg.release();

        FileOutputStream out = null;
        String filename = "frame.png";
        File file = new File(Environment.getExternalStorageDirectory() + "/frames");

        boolean success = true;
        if (!file.exists()){
            success = file.mkdir();
        }

        if (success){
            File dest = new File(file, filename);

            try {
                out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                //PNG is a lossless format so the compression factor is ignored
            } catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            } finally {
                try {
                    if (out != null){
                        out.close();
                        Log.d(TAG, "OK");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    public void onClickGo(View view){
        saveImage(mRGBA);
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();
        return mRGBA;
    }
}
