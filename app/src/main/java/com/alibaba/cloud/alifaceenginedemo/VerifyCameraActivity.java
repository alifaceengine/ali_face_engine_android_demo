package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.cloud.faceengine.Codec;
import com.alibaba.cloud.faceengine.Error;
import com.alibaba.cloud.faceengine.Face;
import com.alibaba.cloud.faceengine.FaceAttributeAnalyze;
import com.alibaba.cloud.faceengine.FaceDetect;
import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.FaceVerify;
import com.alibaba.cloud.faceengine.FeatureExtract;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;
import com.alibaba.cloud.faceengine.VerifyResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VerifyCameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "AFE_VerifyCamera";
    private static final int ALBUM_OK = 0;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mPreviewRotation = 0;
    private static int PREVIEW_WIDTH = 1280;
    private static int PREVIEW_HEIGHT = 720;

    private OrientationEventListener mOrientationEventListener;
    private boolean mSupportOrientation = false;

    private FaceDetect mFaceDetect;
    private FaceVerify mFaceVerify;
    private FeatureExtract mFeatureExtract;
    private int mFaceAttributeFlag = 0;
    private FaceAttributeAnalyze mFaceAttributeAnalyze;
    private FaceVerify.VerifyVideoListener mVerifyVideoListener;
    private VerifyResult[] mVerifyResults;

    private FaceFrameView[] mFaceViews;
    private ImageView mRegisteredPhotoView;
    private TextView mChannelTitleView;
    private FrameLayout mFrame;
    private ImageButton mBackBtn, mSwitchCameraBtn;

    private long mTotalCost = 0;
    private long mDetectCost = 0;
    private long mVerifyCost = 0;
    private long mAttributeCost = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_verifycamera);

        mSurfaceView = (SurfaceView) findViewById(R.id.activity_verifycamera_sv);
        mRegisteredPhotoView = (ImageView) findViewById(R.id.activity_verifycamera_iv);
        mChannelTitleView = (TextView) findViewById(R.id.currency_tv_title);
        mFrame = (FrameLayout) findViewById(R.id.activity_verifycamera_frame);
        mBackBtn = (ImageButton) findViewById(R.id.currency_btn_back);
        mSwitchCameraBtn = (ImageButton) findViewById(R.id.activity_verifycamera_ib);
        mChannelTitleView.setText(R.string.verify_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFixedSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mVerifyVideoListener = new FaceVerify.VerifyVideoListener() {
            @Override
            public void onVerified(Image image, VerifyResult[] results) {
                Log.d(TAG, "onVerified");
                mVerifyResults = results;
            }
        };

        mRegisteredPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentIv = new Intent(Intent.ACTION_PICK, null);
                intentIv.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intentIv, ALBUM_OK);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyCameraActivity.this.finish();
            }
        });

        mSwitchCameraBtn.setOnClickListener(new View.OnClickListener() {
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

        mOrientationEventListener = new OrientationEventListener(VerifyCameraActivity.this) {
            @Override
            public void onOrientationChanged(int orientation) {
                mSupportOrientation = true;
                orientationChanged(orientation);
            }
        };

        mFaceDetect = FaceDetect.createInstance(Mode.TERMINAL);
        mFaceVerify = FaceVerify.createInstance(Mode.TERMINAL);
        mFeatureExtract = FeatureExtract.createInstance(ModelType.MODEL_3K, Mode.TERMINAL);
        mFaceAttributeAnalyze = FaceAttributeAnalyze.createInstance(Mode.TERMINAL);
        mFaceAttributeFlag = FaceAttributeAnalyze.QUALITY
                | FaceAttributeAnalyze.LIVENESS;
        mFaceAttributeAnalyze.setFlag(mFaceAttributeFlag);
        mFaceVerify.setVerifyVideoListener(mVerifyVideoListener);

        if (hasFrontFacingCamera()) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else if (hasBackFacingCamera()) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mOrientationEventListener.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mOrientationEventListener.disable();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        FaceDetect.deleteInstance(mFaceDetect);
        FaceVerify.deleteInstance(mFaceVerify);
        FeatureExtract.deleteInstance(mFeatureExtract);
        FaceAttributeAnalyze.deleteInstance(mFaceAttributeAnalyze);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        long onPreviewFrameTime = System.currentTimeMillis();
        Log.d(TAG, "onPreviewFrame, mPreviewRotation: " + mPreviewRotation + " data.length:" + data.length);

        if (mFaceViews != null) {
            for (int j = 0; j < mFaceViews.length; j++) {
                mFrame.removeView(mFaceViews[j]);
            }
        }

        Image image = new Image();
        image.data = data;
        image.format = com.alibaba.cloud.faceengine.ImageFormat.NV21;
        image.width = PREVIEW_WIDTH;
        image.height = PREVIEW_HEIGHT;
        image.rotation = ImageRotation.ANGLE_0;

        long beginCost = System.currentTimeMillis();
        if (mPreviewRotation == 270) {
            Codec.nv21Rotate270InPlace(image.data, image.width, image.height);
            int width = image.width;
            image.width = image.height;
            image.height = width;
        } else if (mPreviewRotation == 180) {
            Codec.nv21Rotate180InPlace(image.data, image.width, image.height);
        } else if (mPreviewRotation == 90) {
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

        if (faces != null && faces.length > 0) {
            Log.d(TAG, "verifyVideo begin");

            List<Face> faceList = new ArrayList<Face>();
            for (int i = 0; i < faces.length; i++) {
                if ((mFaceAttributeFlag & FaceAttributeAnalyze.LIVENESS) == 0
                        || faces[i].attribute.liveness.score >= 70) {
                    faceList.add(faces[i]);
                }
            }

            if (faceList.size() > 0) {
                beginCost = System.currentTimeMillis();
                Face[] faces1 = new Face[faceList.size()];
                mFaceVerify.verifyVideo(image, faceList.toArray(faces1));

                mVerifyCost = System.currentTimeMillis() - beginCost;
                Log.d(TAG, "verifyVideo cost : " + mVerifyCost);

                if (mVerifyResults != null) {
                    for (int i = 0; i < mVerifyResults.length; i++) {
                        Log.d(TAG, "mVerifyResults[" + i + "] = " + mVerifyResults[i]);
                    }
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
    }

    private View createFaceFrameView(float top, float left, float bottom, float right, boolean result, String text) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(right - left), Math.round(bottom - top));
        FaceFrameView view = new FaceFrameView(VerifyCameraActivity.this, Math.round(top), Math.round(bottom), Math.round(left), Math.round(right), result, text);
        view.setLayoutParams(lp);
        return view;
    }

    private void drawFaces(Image image, Face face, int i) {
        int mFrameWidth = mFrame.getWidth();
        int mFrameHeight = mFrame.getHeight();

        int left = face.rect.left;
        int top = face.rect.top;
        int right = face.rect.right;
        int bottom = face.rect.bottom;
        boolean mresult = false;
        String mText = this.getString(R.string.please_select_photo);

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

        if ((mFaceAttributeFlag & FaceAttributeAnalyze.LIVENESS) > 0) {
            mText += ",liveness: " + face.attribute.liveness.score;
        }
        if ((mFaceAttributeFlag & FaceAttributeAnalyze.AGE) > 0) {
            mText += ",age: " + face.attribute.age;
        }
        if ((mFaceAttributeFlag & FaceAttributeAnalyze.GENDER) > 0) {
            mText += ",gender: " + face.attribute.gender;
        }
        if ((mFaceAttributeFlag & FaceAttributeAnalyze.EXPRESSION) > 0) {
            mText += ",expression: " + face.attribute.expression;
        }
        if ((mFaceAttributeFlag & FaceAttributeAnalyze.QUALITY) > 0) {
            mText += ",quality: " + face.attribute.quality.score;
        }
        if ((mFaceAttributeFlag & FaceAttributeAnalyze.GLASS) > 0) {
            mText += ",glass: " + face.attribute.glass;
        }
        mText += ", ";
        mText += ",Detect Cost: " + mDetectCost;
        mText += ",Verify Cost: " + mVerifyCost;
        mText += ",Attribute Cost: " + mAttributeCost;
        mText += ",Total Cost: " + mTotalCost;


        float mratewidth = 0;
        float mrateheight = 0;
        if (mFrameWidth > mFrameHeight) {
            mratewidth = ((float) mFrameWidth) / PREVIEW_WIDTH;
            mrateheight = ((float) mFrameHeight) / PREVIEW_HEIGHT;
        } else {
            mratewidth = ((float) mFrameWidth) / PREVIEW_HEIGHT;
            mrateheight = ((float) mFrameHeight) / PREVIEW_WIDTH;
        }
        mFaceViews[i] = (FaceFrameView) createFaceFrameView(top * mrateheight, left * mratewidth, bottom * mrateheight, right * mratewidth, mresult, mText);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mFaceViews[i].getLayoutParams());

        if (mPreviewRotation == 90) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(left * mratewidth), Math.round((image.height - bottom) * mrateheight), Math.round((image.width - right) * mratewidth), Math.round((image.height - top) * mrateheight));
                mFaceViews[i].setRotation(180);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(left * mratewidth), Math.round(top * mrateheight), Math.round((image.width - right) * mratewidth), Math.round(bottom * mrateheight));
            }
        } else if (mPreviewRotation == 0) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (mSupportOrientation) {
                    params.setMargins(Math.round(mFrameWidth - bottom * mratewidth), Math.round(mFrameHeight - right * mrateheight), Math.round(mFrameWidth - top * mratewidth), Math.round(mFrameHeight - left * mrateheight));
                    mFaceViews[i].setRotation(90);
                } else {
                    params.setMargins(Math.round((image.width - right) * mratewidth), Math.round(top * mrateheight), Math.round(left * mratewidth), Math.round(bottom * mrateheight));
                }
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (mSupportOrientation) {
                    params.setMargins(Math.round(mFrameWidth - bottom * mratewidth), Math.round(left * mrateheight), Math.round(mFrameWidth - top * mratewidth), Math.round(right * mrateheight));
                    mFaceViews[i].setRotation(90);
                } else {
                    params.setMargins(Math.round(left * mratewidth), Math.round(top * mrateheight), Math.round((image.width - right) * mratewidth), Math.round(bottom * mrateheight));
                }
            }
        } else if (mPreviewRotation == 180) {
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

    private void openCamera(SurfaceHolder holder, int cameraId) {
        Log.d(TAG, "openCamera");
        releaseCamera();
        mCamera = Camera.open(cameraId);
        Camera.Parameters parameters = mCamera.getParameters();
        //设置摄像机方向
        setCameraDisplayOrientation(this, cameraId, mCamera);
        //预览图像尺寸
        parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
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
    }

    private synchronized void releaseCamera() {
        Log.d(TAG, "releaseCamera");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ALBUM_OK == requestCode) {
            if (data != null) {
                Uri uri = data.getData();
                registerPicture(uri);
            }
        }
    }

    private void registerPicture(Uri uri) {
        String path = Utils.getFilePathByUri(VerifyCameraActivity.this, uri);
        Log.d(TAG, "registerPicture path:" + path);
        ContentResolver cr = this.getContentResolver();

        byte[] data = Utils.loadFile(path);
        float degree = Utils.readPictureDegree(path);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            Log.d(TAG, "registerPicture fail: bitmap is null");
            return;
        }

        if (degree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }

        Image image;
        image = new Image();
        image.data = Utils.bitmap2RGB(bitmap);
        image.format = com.alibaba.cloud.faceengine.ImageFormat.RGB888;
        image.rotation = ImageRotation.ANGLE_0;
        image.height = bitmap.getHeight();
        image.width = bitmap.getWidth();
        Face[] faces = mFaceDetect.detectPicture(image);

        if (faces == null) {
            Log.d(TAG, "registerFace fail: detectPicture faces:null");
        } else {

            Log.d(TAG, "detectPicture :" + faces.length);


            boolean registerFeature = false;
            int status = Error.FAILED;
            if (registerFeature) {
                String feature;
                if (mFeatureExtract != null) {
                    feature = mFeatureExtract.extractFeature(image, faces[0]);
                    status = mFaceVerify.registerFeature(feature);
                } else {
                    Log.e(TAG, "registerFace fail, mFeatureExtract is null");
                }
            } else {
                status = mFaceVerify.registerFace(image, faces[0]);
                Log.d(TAG, "registerFace status: " + status);
            }

            if (status == Error.OK) {
                mRegisteredPhotoView.setImageBitmap(bitmap);
            } else {
                mRegisteredPhotoView.setImageBitmap(null);
            }
        }
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

    public void orientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;
        }

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        // Round device orientation to a multiple of 90
        orientation = (orientation + 45) / 90 * 90;
        // Reverse device orientation for front-facing cameras
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
            Log.v(TAG, "mPreviewRotation changed to : " + mPreviewRotation);
        } else {  // back-facing mCamera
            rotation = (info.orientation + orientation) % 360;
        }

        if (mPreviewRotation != rotation) {
            mPreviewRotation = rotation;
            Log.v(TAG, "mPreviewRotation changed to : " + mPreviewRotation);
        }
    }

    public static int getSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }
}
