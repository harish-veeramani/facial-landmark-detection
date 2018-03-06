package com.harish.faciallandmarkdetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by hdv98 on 3/3/2018.
 */

public class DetailActivity extends AppCompatActivity {
    ImageView mImageView;
    Button mAcceptButton;
    Bitmap mBMPInput, mBMPOutput;
    Mat mMatInput, mMatOutput;

    static {
        System.loadLibrary("MyLibs");
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mImageView = findViewById(R.id.picture_view);
        mAcceptButton = findViewById(R.id.accept_button);

        //get frame path
        String photoPath = Environment.getExternalStorageDirectory() + "/frames/frame.png";

        //get bitmap frame
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mBMPInput = BitmapFactory.decodeFile(photoPath, options);

        mImageView.setImageBitmap(mBMPInput);

        //convert bitmap to mat for native function
        mMatInput = convertBitmapToMat(mBMPInput);
        mMatOutput = new Mat(mMatInput.rows(), mMatInput.cols(), CvType.CV_8UC3);

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "Calling Native Function", Toast.LENGTH_SHORT).show();
                NativeClass.LandmarkDetection(mMatInput.getNativeObjAddr(), mMatOutput.getNativeObjAddr());
                mBMPOutput = convertMatToBitmap(mMatOutput);
                mImageView.setImageBitmap(mBMPOutput);
            }
        });
    }
}
