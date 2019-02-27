package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.cloud.faceengine.Codec;
import com.alibaba.cloud.faceengine.DetectParameter;
import com.alibaba.cloud.faceengine.Face;
import com.alibaba.cloud.faceengine.FaceDetect;
import com.alibaba.cloud.faceengine.FaceRecognize;
import com.alibaba.cloud.faceengine.FaceVerify;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.VerifyResult;

import java.io.FileNotFoundException;

/**
 * Created by h on 2018/9/10.
 */

public class VerifyPhotoActivity extends Activity {
    private static final String TAG = "AFE_VerifyPicture";
    private static final int ALBUM_OK1 = 0, ALBUM_OK2 = 2;

    private ImageView iv1, iv2;
    private Button btn1, btn2;
    private FrameLayout frame;
    private TextView mChannelTitleView;

    private Bitmap bitmap;

    private FaceVerify mFaceVerify;
    private FaceDetect mFaceDetect;

    private Image mImage;
    private Face[] mfaces;
    private FaceFrameView[] mfaceFrameViews, myfaceFrameViews;
    private VerifyResult[] mVerifyResults;
    private float bitmapHeight, bitmapWidth, frameHeight, frameWidth;
    private ViewGroup.MarginLayoutParams params;
    private FrameLayout.LayoutParams layoutParams;
    private float ratioX, ratioY;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyphoto);

        init();
        initView();

        mFaceVerify = FaceVerify.createInstance(Mode.TERMINAL);
        mFaceDetect = FaceDetect.createInstance(Mode.TERMINAL);

        DetectParameter detectParameter = mFaceDetect.getPictureParameter();
        detectParameter.checkAge = 1;
        detectParameter.checkLiveness = 1;
        detectParameter.checkQuality = 1;
        detectParameter.checkGender = 1;
        detectParameter.checkExpression = 1;
        detectParameter.checkGlass = 1;
        mFaceDetect.setPictureParameter(detectParameter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        FaceDetect.deleteInstance(mFaceDetect);
        FaceVerify.deleteInstance(mFaceVerify);
    }

    private void initView() {
        mChannelTitleView.setText(R.string.verify_photo);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentIv = new Intent(Intent.ACTION_PICK, null);
                intentIv.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intentIv, ALBUM_OK1);

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentIv = new Intent(Intent.ACTION_PICK, null);
                intentIv.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intentIv, ALBUM_OK2);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyPhotoActivity.this.finish();
            }
        });
    }

    private void init() {
        iv1 = (ImageView) findViewById(R.id.activity_verifyphoto_iv1);
        iv2 = (ImageView) findViewById(R.id.activity_verifyphoto_iv2);
        btn1 = (Button) findViewById(R.id.activity_verifyphoto_btn1);
        btn2 = (Button) findViewById(R.id.activity_verifyphoto_btn2);
        mChannelTitleView = (TextView) findViewById(R.id.currency_tv_title);
        frame = (FrameLayout) findViewById(R.id.activity_verifyphoto_frame2);
        btnBack = (ImageButton) findViewById(R.id.currency_btn_back);
        btn2.setEnabled(false);
        btn2.setTextColor(ContextCompat.getColor(VerifyPhotoActivity.this, R.color.textColorN));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver cr = this.getContentResolver();
        if (data != null) {
            Uri uri = data.getData();
            String path = Utils.getFilePathByUri(VerifyPhotoActivity.this, uri);
            float degree = Utils.readPictureDegree(path);
            try {
                if (ALBUM_OK1 == requestCode) {
                    btn2.setEnabled(true);
                    btn2.setTextColor(Color.BLACK);
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    if (degree != 0) {
                        // 旋转图片
                        Matrix m = new Matrix();
                        m.postRotate(degree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), m, true);
                    }
                    mImage = new Image();
                    mImage.data = Utils.bitmap2RGB(bitmap);
                    mImage.format = com.alibaba.cloud.faceengine.ImageFormat.RGB888;
                    mImage.rotation = ImageRotation.ANGLE_0;
                    mImage.height = bitmap.getHeight();
                    mImage.width = bitmap.getWidth();
                    mfaces = mFaceDetect.detectPicture(mImage);

                    if (mfaces != null) {
                        for (int i = 0; i < mfaces.length; i++) {
                            Log.d(TAG, "faces[" + i + "]=" + mfaces[i]);
                        }
                    }

                    iv1.setImageBitmap(bitmap);
                    if (mImage != null) {
                        if (myfaceFrameViews != null) {
                            for (int j = 0; j < myfaceFrameViews.length; j++) {
                                frame.removeView(myfaceFrameViews[j]);
                            }
                        }
                        iv2.setImageResource(R.drawable.photo_back);
                    }
                }
                if (ALBUM_OK2 == requestCode) {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    if (degree != 0) {
                        // 旋转图片
                        Matrix m = new Matrix();
                        m.postRotate(degree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), m, true);
                    }
                    iv2.setImageBitmap(bitmap);
                    bitmapWidth = bitmap.getWidth();
                    bitmapHeight = bitmap.getHeight();
                    frameHeight = frame.getHeight();
                    frameWidth = frame.getWidth();
                    ratioY = frameHeight / bitmapHeight;
                    ratioX = frameWidth / bitmapWidth;
                    Image image = new Image();
                    image.data = Utils.bitmap2RGB(bitmap);
                    image.format = com.alibaba.cloud.faceengine.ImageFormat.RGB888;
                    image.rotation = ImageRotation.ANGLE_0;
                    image.height = bitmap.getHeight();
                    image.width = bitmap.getWidth();

                    Face[] faces = mFaceDetect.detectPicture(image);
                    if (faces != null) {
                        for (int i = 0; i < faces.length; i++) {
                            Log.d(TAG, "faces[" + i + "]=" + faces[i]);
                        }
                    }
                    if (bitmap != null && faces != null) {
                        Log.d(TAG, "verifyPicture begin");
                        mVerifyResults = mFaceVerify.verifyPicture(mImage, mfaces[0], image, faces);
                        Log.d(TAG, "verifyPicture end");
                    }
                    if (faces != null) {
                        mfaceFrameViews = new FaceFrameView[faces.length];
                        for (int i = 0; i < faces.length; i++) {
                            if (myfaceFrameViews != null) {
                                for (int j = 0; j < myfaceFrameViews.length; j++) {
                                    frame.removeView(myfaceFrameViews[j]);
                                }
                            }
                            drow(image, faces[i], i);
                        }
                        myfaceFrameViews = mfaceFrameViews;
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void drow(Image image, Face face, int i) {
        int mleft = face.rect.left;
        int mtop = face.rect.top;
        int mright = face.rect.right;
        int mbottom = face.rect.bottom;
        boolean mresult = false;
        String mText = this.getString(R.string.select_photo);
        VerifyResult result = getVerifyResult(face.trackId);
        if (result != null) {
            if (result.similarity >= 70.0) {
                mresult = true;
                mText = result.similarity + "";
            } else {
                mresult = false;
                mText = result.similarity + "";
            }
        }

        mfaceFrameViews[i] = (FaceFrameView) mAddView(mtop * ratioY, mleft * ratioX, mbottom * ratioY, mright * ratioX, mresult, mText);
        params = new ViewGroup.MarginLayoutParams(mfaceFrameViews[i].getLayoutParams());
//        params.setMargins(Math.round((image.width - mright) * ratioX), Math.round(mtop * ratioY), Math.round(mleft * ratioX), Math.round(mbottom * ratioY));
        params.setMargins(Math.round(mleft * ratioX), Math.round(mtop * ratioY), Math.round(mright * ratioX), Math.round(mbottom * ratioY));
        layoutParams = new FrameLayout.LayoutParams(params);
        frame.addView(mfaceFrameViews[i], layoutParams);
    }

    VerifyResult getVerifyResult(int trackId) {
        if (mVerifyResults == null) {
            return null;
        }

        for (int i = 0; i < mVerifyResults.length; i++) {
            if (mVerifyResults[i].trackId == trackId) {
                return mVerifyResults[i];
            }
        }

        return null;
    }

    private View mAddView(float top, float left, float bottom, float right, boolean result, String text) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(right - left), Math.round(bottom - top));
        FaceFrameView view = new FaceFrameView(VerifyPhotoActivity.this, Math.round(top), Math.round(bottom), Math.round(left), Math.round(right), result, text);
        view.setLayoutParams(lp);
        return view;
    }


}
