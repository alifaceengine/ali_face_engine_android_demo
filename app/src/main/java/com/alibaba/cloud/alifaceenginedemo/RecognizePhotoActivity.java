package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.cloud.faceengine.DetectParameter;
import com.alibaba.cloud.faceengine.Face;
import com.alibaba.cloud.faceengine.FaceDetect;
import com.alibaba.cloud.faceengine.FaceRecognize;
import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Group;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;
import com.alibaba.cloud.faceengine.RecognizeResult;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class RecognizePhotoActivity extends Activity {
    private static String TAG = "AFE_RecognizePhoto";
    private TextView title;
    private Spinner groupSpin;
    private Button btn;
    private ImageView iv;
    private FrameLayout frame;
    private static final int ALBUM_OK = 0;
    private Bitmap bitmap;
    private float bitmapHeight, bitmapWidth, frameHeight, frameWidth;
    private Image mImage;
    Group[] mGroups;
    private FaceRegister mFaceRegister;
    private FaceRecognize mFaceRecognize;
    private FaceDetect mFaceDetect;
    private List<String> mGroupNames;
    private Face[] faces;
    private FaceFrameView[] mFaceFrameViews;
    private RecognizeResult[] results;
    private float mwidth, mheight;
    private ViewGroup.MarginLayoutParams params;
    private FrameLayout.LayoutParams layoutParams;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_recognizephoto);
        initData();
        init();
        initView();
    }

    private void initData() {
        mFaceRegister = mFaceRegister.createInstance();

        mFaceDetect = FaceDetect.createInstance(Mode.TERMINAL);
        DetectParameter detectParameter = mFaceDetect.getPictureParameter();
        detectParameter.checkAge = 1;
        detectParameter.checkLiveness = 1;
        detectParameter.checkQuality = 1;
        detectParameter.checkGender = 1;
        detectParameter.checkExpression = 1;
        detectParameter.checkGlass = 1;
        mFaceDetect.setPictureParameter(detectParameter);

        mGroups = mFaceRegister.getAllGroups();
        mGroupNames = new ArrayList<String>();

        if (mGroups != null) {
            for (int i = 0; i < mGroups.length; i++) {
                if (mGroups[i].modelType == ModelType.MODEL_100K) {
                    mGroupNames.add(mGroups[i].name + " |100K");
                } else {
                    mGroupNames.add(mGroups[i].name + " |3K");
                }
            }
        }
    }

    private void initView() {
        title.setText(R.string.recognize_photo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentIv = new Intent(Intent.ACTION_PICK, null);
                intentIv.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intentIv, ALBUM_OK);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecognizePhotoActivity.this, R.layout.support_simple_spinner_dropdown_item, mGroupNames);
        groupSpin.setAdapter(adapter);
        groupSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mFaceRecognize != null) {
                    FaceRecognize.deleteInstance(mFaceRecognize);
                    mFaceRecognize = null;
                }

                if (mGroups[position].modelType == ModelType.MODEL_100K) {
                    mFaceRecognize = FaceRecognize.createInstance(mGroups[position].name, Mode.CLOUD);
                } else {
                    mFaceRecognize = FaceRecognize.createInstance(mGroups[position].name, Mode.TERMINAL);
                }

                if (bitmap != null) {
                    if (faces != null && faces.length > 0) {
                        Log.d(TAG, "recognizePicture begin");
                        results = mFaceRecognize.recognizePicture(mImage, faces);
                        Log.d(TAG, "recognizePicture end");
                    }

                    if (mFaceFrameViews != null) {
                        for (int j = 0; j < mFaceFrameViews.length; j++) {
                            frame.removeView(mFaceFrameViews[j]);
                        }
                    }
                    if (faces != null) {
                        mFaceFrameViews = new FaceFrameView[faces.length];
                        for (int i = 0; i < faces.length; i++) {
                            draw(faces[i], i);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (mFaceRecognize != null) {
                    FaceRecognize.deleteInstance(mFaceRecognize);
                    mFaceRecognize = null;
                }
            }
        });

        if (mGroups != null) {
            if (mGroups[groupSpin.getSelectedItemPosition()].modelType == ModelType.MODEL_100K) {
                mFaceRecognize = FaceRecognize.createInstance(mGroups[groupSpin.getSelectedItemPosition()].name, Mode.CLOUD);
            } else {
                mFaceRecognize = FaceRecognize.createInstance(mGroups[groupSpin.getSelectedItemPosition()].name, Mode.TERMINAL);
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizePhotoActivity.this.finish();
            }
        });
    }

    private void init() {
        title = (TextView) findViewById(R.id.currency_tv_title);
        groupSpin = (Spinner) findViewById(R.id.activity_recognizephoto_sp);
        btn = (Button) findViewById(R.id.activity_recognizephoto_btn);
        iv = (ImageView) findViewById(R.id.activity_recognizephoto_iv);
        frame = (FrameLayout) findViewById(R.id.activity_recognizephoto_frame);
        btnBack = (ImageButton) findViewById(R.id.currency_btn_back);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ALBUM_OK == requestCode) {
            ContentResolver cr = this.getContentResolver();
            if (data != null) {
                Uri uri = data.getData();
                String path = Utils.getFilePathByUri(RecognizePhotoActivity.this, uri);
                float degree = Utils.readPictureDegree(path);
                try {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    if (degree != 0) {
                        // 旋转图片
                        Matrix m = new Matrix();
                        m.postRotate(degree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), m, true);
                    }
                    bitmapWidth = bitmap.getWidth();
                    bitmapHeight = bitmap.getHeight();
                    frameHeight = frame.getHeight();
                    frameWidth = frame.getWidth();
                    mheight = frameHeight / bitmapHeight;
                    mwidth = frameWidth / bitmapWidth;
                    mImage = new Image();
                    mImage.data = Utils.bitmap2RGB(bitmap);
                    mImage.format = com.alibaba.cloud.faceengine.ImageFormat.RGB888;
                    mImage.rotation = ImageRotation.ANGLE_0;
                    mImage.height = bitmap.getHeight();
                    mImage.width = bitmap.getWidth();
                    iv.setImageBitmap(bitmap);
                    faces = mFaceDetect.detectPicture(mImage);
                    if (faces != null && faces.length > 0) {
                        Log.d(TAG, "recognizePicture begin");
                        if (mFaceRecognize != null) {
                            results = mFaceRecognize.recognizePicture(mImage, faces);
                        }
                        Log.d(TAG, "recognizePicture end");
                    }
                    if (mFaceFrameViews != null) {
                        for (int j = 0; j < mFaceFrameViews.length; j++) {
                            frame.removeView(mFaceFrameViews[j]);
                        }
                    }
                    if (faces != null) {
                        mFaceFrameViews = new FaceFrameView[faces.length];
                        for (int i = 0; i < faces.length; i++) {
                            draw(faces[i], i);
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw(Face face, int i) {
        int left = face.rect.left;
        int right = face.rect.right;
        int top = face.rect.top;
        int bottom = face.rect.bottom;
        float mleft = left * mwidth;
        float mright = right * mwidth;
        float mtop = top * mheight;
        float mbottom = bottom * mheight;
        boolean mresult = false;
        String mText = this.getString(R.string.mismatch);
        RecognizeResult result = getRecognizeResult(face.trackId);

        if (result != null) {
            if (result.similarity >= 70.0) {
                mresult = true;
                mText = result.personName + "（" + result.similarity + "）";
            } else {
                mresult = false;
                mText = result.personName + "（" + result.similarity + "）";
            }
        }
        mFaceFrameViews[i] = (FaceFrameView) mAddView(mtop, mleft, mbottom, mright, mresult, mText);
        params = new ViewGroup.MarginLayoutParams(mFaceFrameViews[i].getLayoutParams());
        params.setMargins(Math.round(mleft), Math.round(mtop), Math.round(mright), Math.round(mbottom));
        //params.setMargins(left, top, right, bottom);
        layoutParams = new FrameLayout.LayoutParams(params);
        frame.addView(mFaceFrameViews[i], layoutParams);

    }

    RecognizeResult getRecognizeResult(int trackId) {
        if (results == null) {
            return null;
        }
        for (int i = 0; i < results.length; i++) {
            if (results[i].trackId == trackId) {
                return results[i];
            }
        }
        return null;
    }

    private View mAddView(float top, float left, float bottom, float right, boolean result, String text) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(right - left), Math.round(bottom - top));
        FaceFrameView view = new FaceFrameView(RecognizePhotoActivity.this, Math.round(top), Math.round(bottom), Math.round(left), Math.round(right), result, text);
        view.setLayoutParams(lp);
        return view;
    }

}
