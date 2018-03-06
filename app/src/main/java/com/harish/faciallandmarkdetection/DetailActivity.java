package com.harish.faciallandmarkdetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.cvtColor;

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

    //Convert Java bitmap to Opencv Mat
    private Mat convertBitmapToMat(Bitmap bitmap){
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);

        Mat rgbMat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2BGR, 3);
        return rgbMat;
    }

    private Bitmap convertMatToBitmap(Mat mat){
        int width = mat.width();
        int height = mat.height();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Mat temp = mat.channels() == 1 ? new Mat(width, height, CvType.CV_8UC1, new Scalar(1)) : new Mat(width, height, CvType.CV_8UC4);

        try {
            if (mat.channels() == 3){
                cvtColor(mat, temp, Imgproc.COLOR_RGB2RGBA);
            } else if (mat.channels() == 1){
                cvtColor(mat, temp, Imgproc.COLOR_GRAY2RGBA);
            }
            Utils.matToBitmap(temp, bitmap);
        } catch (CvException e){
            Log.d("DetailActivity", e.getMessage());
        }

        return bitmap;
    }
}
