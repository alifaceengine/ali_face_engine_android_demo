package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.cloud.faceengine.Codec;
import com.alibaba.cloud.faceengine.DetectParameter;
import com.alibaba.cloud.faceengine.Expression;
import com.alibaba.cloud.faceengine.Face;
import com.alibaba.cloud.faceengine.FaceAttributeAnalyze;
import com.alibaba.cloud.faceengine.FaceDetect;
import com.alibaba.cloud.faceengine.FaceEngine;
import com.alibaba.cloud.faceengine.FaceRecognize;
import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Gender;
import com.alibaba.cloud.faceengine.Glass;
import com.alibaba.cloud.faceengine.Group;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;
import com.alibaba.cloud.faceengine.RecognizeResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecognizeCameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static String TAG = "AFE_RecognizeCameraActivity";
    private SurfaceView mSurfaceView;
    private Spinner mGroupSpinner;
    private TextView mTitle;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mRotation = 0;

    private List<Group> mAllGroups;
    private List<String> mAllGroupNames;

    private FaceDetect mFaceDetect;
    private FaceRegister mFaceRegister;
    private FaceRecognize mFaceRecognize;
    private FaceAttributeAnalyze mFaceAttributeAnalyze;
    private FaceRecognize.RecognizeVideoListener mRecognizeVideoListener;
    private RecognizeResult[] mRecognizeResults;
    private long mTotalCost = 0;
    private long mDetectCost = 0;
    private long mRecognizeCost = 0;
    private long mAttributeCost = 0;

    private FaceFrameView[] mFaceViews;
    private FrameLayout mFrame;
    private int mFrameWidth, mFrameHeight;

    private String mCaller;
    private LinearLayout zcLv, spLv;
    private ImageButton mBtnBack, mBtnSwichCamera;
    private OrientationEventListener mOrientationEventListener;

    private Button mBtnForRegisterForRegister;
    private Bitmap mBitmapForRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizecamera);
        init();
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFaceRegister = FaceRegister.createInstance();
        mFaceAttributeAnalyze = FaceAttributeAnalyze.createInstance(Mode.TERMINAL);
        mFaceAttributeAnalyze.setFlag(FaceAttributeAnalyze.QUALITY
                | FaceAttributeAnalyze.GENDER
                | FaceAttributeAnalyze.AGE);

        mFaceDetect = FaceDetect.createInstance(Mode.TERMINAL);

        mAllGroups = new ArrayList<Group>();
        Group[] allGroups = mFaceRegister.getAllGroups();
        if (allGroups != null) {
            mAllGroupNames = new ArrayList<String>();
            for (int i = 0; i < allGroups.length; i++) {
                if (allGroups[i].modelType == ModelType.MODEL_100K) {
                    if (FaceEngine.supportCloud()) {
                        mAllGroupNames.add(allGroups[i].name + " (100K)");
                        mAllGroups.add(allGroups[i]);
                    }
                } else {
                    mAllGroupNames.add(allGroups[i].name + " (3K)");
                    mAllGroups.add(allGroups[i]);
                }
            }
        }

        if (mAllGroupNames != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecognizeCameraActivity.this, R.layout.support_simple_spinner_dropdown_item, mAllGroupNames);
            mGroupSpinner.setAdapter(adapter);
            mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemSelected postion:" + position + " groupId:" + mAllGroups.get(position).id);
                    if (mFaceRecognize != null) {
                        FaceRecognize.deleteInstance(mFaceRecognize);
                        mFaceRecognize = null;
                    }

                    if (mAllGroups.get(position).modelType == ModelType.MODEL_100K) {
                        mFaceRecognize = FaceRecognize.createInstance(mAllGroups.get(position).name, Mode.CLOUD);
                    } else {
                        mFaceRecognize = FaceRecognize.createInstance(mAllGroups.get(position).name, Mode.TERMINAL);
                    }
                    mFaceRecognize.setRecognizeVideoListener(mRecognizeVideoListener);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    if (mFaceRecognize != null) {
                        FaceRecognize.deleteInstance(mFaceRecognize);
                        mFaceRecognize = null;
                    }
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        if (mFaceDetect != null) {
            FaceDetect.deleteInstance(mFaceDetect);
            mFaceDetect = null;
        }
        if (mFaceRegister != null) {
            FaceRegister.deleteInstance(mFaceRegister);
            mFaceRegister = null;
        }
        if (mFaceRecognize != null) {
            FaceRecognize.deleteInstance(mFaceRecognize);
            mFaceRecognize = null;
        }
        if (mFaceAttributeAnalyze != null) {
            FaceAttributeAnalyze.deleteInstance(mFaceAttributeAnalyze);
            mFaceAttributeAnalyze = null;
        }
    }

    private void initView() {
        mTitle.setText(R.string.recognize_camera);
        if (mCaller.equals(RegisterCameraActivity.RegisteredCameraTAG)) {
            mTitle.setText(R.string.register_camera);
            spLv.setVisibility(View.GONE);
            zcLv.setVisibility(View.VISIBLE);
        }
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFixedSize(1280, 720);
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRecognizeVideoListener = new FaceRecognize.RecognizeVideoListener() {
            @Override
            public void onRecognized(Image image, RecognizeResult[] results) {
                Log.d(TAG, "onRecognized");
                if (results != null) {
                    for (int i = 0; i < results.length; i++) {
                        Log.d(TAG, "onRecognized results[" + i + "] = " + results[i]);
                    }
                }
                mRecognizeResults = results;
            }
        };

        mBtnForRegisterForRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmapForRegister == null) {
                    Log.d(TAG, "mBtnForRegisterForRegister onClick cancel");
                    return;
                }

                File file = new File(Utils.filePath + "linshi.jpg");
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    mBitmapForRegister.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.putExtra("data", file.toString());
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        mBtnSwichCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    openCamera(mSurfaceHolder, mCameraId);
                } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    openCamera(mSurfaceHolder, mCameraId);
                }
                if (mFaceViews != null) {
                    for (int j = 0; j < mFaceViews.length; j++) {
                        mFrame.removeView(mFaceViews[j]);
                    }
                }
            }
        });
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizeCameraActivity.this.finish();
            }
        });
    }

    private void initData() {
        mCaller = getIntent().getStringExtra("TAG");
    }

    private void init() {
        mSurfaceView = (SurfaceView) findViewById(R.id.activity_recognizecamera_sv);
        mGroupSpinner = (Spinner) findViewById(R.id.activity_recognizecamera_sp);
        mTitle = (TextView) findViewById(R.id.currency_tv_title);
        mFrame = (FrameLayout) findViewById(R.id.activity_recognizecamera_frame);
        zcLv = (LinearLayout) findViewById(R.id.zcopencamera_lv);
        mBtnForRegisterForRegister = (Button) findViewById(R.id.zcopencamera_btn);
        spLv = (LinearLayout) findViewById(R.id.activity_recognizecamera_lv);
        mBtnBack = (ImageButton) findViewById(R.id.currency_btn_back);
        mBtnSwichCamera = (ImageButton) findViewById(R.id.activity_recognizecamera_ib);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera(holder, mCameraId);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame");
        long onPreviewFrameTime = System.currentTimeMillis();

        if (mFaceViews != null) {
            for (int j = 0; j < mFaceViews.length; j++) {
                mFrame.removeView(mFaceViews[j]);
            }
        }
        Image image = new Image();
        image.data = data;
        image.format = com.alibaba.cloud.faceengine.ImageFormat.NV21;
        image.width = 1280;
        image.height = 720;
        image.rotation = ImageRotation.ANGLE_0;

        long beginCost = System.currentTimeMillis();
        if (mRotation == 270) {
            Codec.nv21Rotate270InPlace(image.data, image.width, image.height);
            int width = image.width;
            image.width = image.height;
            image.height = width;
        } else if (mRotation == 180) {
            Codec.nv21Rotate180InPlace(image.data, image.width, image.height);
        } else if (mRotation == 90) {
            Codec.nv21Rotate90InPlace(image.data, image.width, image.height);
            int width = image.width;
            image.width = image.height;
            image.height = width;
        }
        Log.d(TAG, "onPreviewFrame rotate image cost : " + (System.currentTimeMillis() - beginCost));

        beginCost = System.currentTimeMillis();
        Face[] faces = null;
        if (mFaceDetect != null) {
            faces = mFaceDetect.detectVideo(image);
        }
        mDetectCost = System.currentTimeMillis() - beginCost;
        Log.d(TAG, "onPreviewFrame detectVideo cost : " + mDetectCost);

        if (faces != null) {
            beginCost = System.currentTimeMillis();
            if (mFaceAttributeAnalyze != null) {
                mFaceAttributeAnalyze.analyze(image, faces);
            }
            mAttributeCost = System.currentTimeMillis() - beginCost;
            Log.d(TAG, "onPreviewFrame face attribute cost : " + mAttributeCost);
            for (int i = 0; i < faces.length; i++) {
                Log.d(TAG, "detectVideo faces[" + i + "]:" + faces[i]);
            }
        }

        if (mCaller.equals(RegisterCameraActivity.RegisteredCameraTAG)) {
            if (faces == null) {
                mBtnForRegisterForRegister.setEnabled(false);
                mBtnForRegisterForRegister.setTextColor(ContextCompat.getColor(RecognizeCameraActivity.this, R.color.textColorN));
                mBtnForRegisterForRegister.setText(R.string.no_detected);
            } else {
                mBtnForRegisterForRegister.setEnabled(true);
                mBtnForRegisterForRegister.setTextColor(Color.BLACK);
                mBtnForRegisterForRegister.setText(R.string.select_face);
            }
        }

        //在监听中保存result全局变量 跟face[]中的trackId进行比对
        if (faces != null && faces.length > 0) {
            beginCost = System.currentTimeMillis();
            if (mFaceRecognize != null) {
                mFaceRecognize.recognizeVideo(image, faces);
            }
            mRecognizeCost = System.currentTimeMillis() - beginCost;
            Log.d(TAG, "recognizePicture cost : " + mRecognizeCost);

            if (mRecognizeResults != null) {
                for (int i = 0; i < mRecognizeResults.length; i++) {
                    Log.d(TAG, "onRecognized results[" + i + "] = " + mRecognizeResults[i]);
                }
            }
        }

        mTotalCost = System.currentTimeMillis() - onPreviewFrameTime;

        if (faces != null) {
            mFaceViews = new FaceFrameView[faces.length];
            for (int i = 0; i < faces.length; i++) {
                if (mFaceViews != null) {
                    for (int j = 0; j < mFaceViews.length; j++) {
                        mFrame.removeView(mFaceViews[j]);
                    }
                }
                drawFaces(image, faces[i], i);
            }
        }

        if (mCaller.equals(RegisterCameraActivity.RegisteredCameraTAG)) {
            if (mBitmapForRegister == null) {
                mBitmapForRegister = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
            }
            Utils.displayNV21ToBitmap(mBitmapForRegister, data, 720, 1280);
        }
    }

    private void drawFaces(Image image, Face face, int i) {
        int left = face.rect.left;
        int top = face.rect.top;
        int right = face.rect.right;
        int bottom = face.rect.bottom;
        boolean mresult = false;
        String mText = this.getString(R.string.please_select_photo);
        RecognizeResult result = getRecognizeResult(face.trackId);
        if (!mCaller.equals(RegisterCameraActivity.RegisteredCameraTAG)) {
            if (result != null) {
                if (result.similarity >= 70.0) {
                    mresult = true;
                } else {
                    mresult = false;
                }

                mText = result.personName + ": " + result.similarity;
            }
        } else {
            mresult = true;
            mText = "";
        }

        if (face.attribute.liveness.score > 0) {
            mText += ",liveness: " + face.attribute.liveness.score;
        }
        if (face.attribute.age > 0) {
            mText += ",age: " + face.attribute.age;
        }
        if (face.attribute.gender != Gender.GENGER_UNKNOWN) {
            mText += ",gender: " + face.attribute.gender;
        }
        if (face.attribute.expression != Expression.EXPRESSION_UNKOWN) {
            mText += ",expression: " + face.attribute.expression;
        }
        if (face.attribute.quality.score > 0) {
            mText += ",quality: " + face.attribute.quality.score;
        }
        if (face.attribute.glass != Glass.GLASS_UNKOWN) {
            mText += ",glass: " + face.attribute.glass;
        }
        mText += ", ";
        mText += ",Detect Cost: " + mDetectCost;
        mText += ",Recognize Cost: " + mRecognizeCost;
        mText += ",Attribute Cost: " + mAttributeCost;
        mText += ",Total Cost: " + mTotalCost;


        float mratewidth = ((float) mFrameWidth) / 720;
        float mrateheight = ((float) mFrameHeight) / 1280;
        mFaceViews[i] = (FaceFrameView) mAddView(top * mrateheight, left * mratewidth, bottom * mrateheight, right * mratewidth, mresult, mText);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mFaceViews[i].getLayoutParams());

        if (mRotation == 90) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(left * mratewidth), Math.round((image.height - bottom) * mrateheight), Math.round((image.width - right) * mratewidth), Math.round((image.height - top) * mrateheight));
                mFaceViews[i].setRotation(180);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(left * mratewidth), Math.round(top * mrateheight), Math.round((image.width - right) * mratewidth), Math.round(bottom * mrateheight));
            }

        } else if (mRotation == 0) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(mFrameWidth - bottom * mratewidth), Math.round(mFrameHeight - right * mrateheight), Math.round(mFrameWidth - top * mratewidth), Math.round(mFrameHeight - left * mrateheight));
                mFaceViews[i].setRotation(90);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(mFrameWidth - bottom * mratewidth), Math.round(left * mrateheight), Math.round(mFrameWidth - top * mratewidth), Math.round(right * mrateheight));
                mFaceViews[i].setRotation(90);
            }

        } else if (mRotation == 180) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(top * mratewidth), Math.round(left * mrateheight), Math.round(bottom * mratewidth), Math.round(right * mrateheight));
                mFaceViews[i].setRotation(270);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(top * mratewidth), Math.round(mFrameHeight - right * mrateheight), Math.round(bottom * mratewidth), Math.round(mFrameHeight - left * mrateheight));
                mFaceViews[i].setRotation(270);
            }

        } else {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round((image.width - right) * mratewidth), Math.round(top * mrateheight), Math.round(left * mratewidth), Math.round(bottom * mrateheight));
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round((image.width - right) * mratewidth), Math.round((image.height - bottom) * mrateheight), Math.round(left * mratewidth), Math.round((image.height - top) * mrateheight));
                mFaceViews[i].setRotation(180);
            }

        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(params);
        mFrame.addView(mFaceViews[i], layoutParams);
    }

    private void openCamera(SurfaceHolder holder, int cameraId) {
        releaseCamera();
        mCamera = Camera.open(cameraId);
        Camera.Parameters parameters = mCamera.getParameters();
        //设置摄像机方向
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);
        //预览图像尺寸
        Log.e(TAG, "perview-size-values:" + parameters.get("preview-size-values"));
        Log.e(TAG, "preview-format-values:" + parameters.get("preview-format-values"));
        parameters.setPreviewSize(1280, 720);
        //设置图像格式
        parameters.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
        mOrientationEventListener = new OrientationEventListener(RecognizeCameraActivity.this) {
            @Override
            public void onOrientationChanged(int orientation) {
                OrientationChanged(orientation);
                mFrameWidth = mFrame.getWidth();
                mFrameHeight = mFrame.getHeight();
            }
        };
        mOrientationEventListener.enable();
    }

    private synchronized void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera = null;
        }
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int displayDegree;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayDegree = (info.orientation + degrees) % 360;
            displayDegree = (360 - displayDegree) % 360;  // compensate the mirror
        } else {
            displayDegree = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(displayDegree);
    }

    RecognizeResult getRecognizeResult(int trackId) {
        if (mRecognizeResults == null) {
            return null;
        }
        for (int i = 0; i < mRecognizeResults.length; i++) {
            if (mRecognizeResults[i].trackId == trackId) {
                return mRecognizeResults[i];
            }
        }
        return null;
    }

    private View mAddView(float top, float left, float bottom, float right, boolean result, String text) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(right - left), Math.round(bottom - top));
        FaceFrameView view = new FaceFrameView(RecognizeCameraActivity.this, Math.round(top), Math.round(bottom), Math.round(left), Math.round(right), result, text);
        view.setLayoutParams(lp);
        return view;
    }

    public void OrientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        // Round device orientation to a multiple of 90
        orientation = (orientation + 45) / 90 * 90;
        // Reverse device orientation for front-facing cameras
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mRotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            mRotation = (info.orientation + orientation) % 360;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationEventListener.disable();
    }
}
