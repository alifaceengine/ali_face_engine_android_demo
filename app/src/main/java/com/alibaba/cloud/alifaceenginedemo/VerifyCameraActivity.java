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
import com.alibaba.cloud.faceengine.FaceVerify;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.VerifyResult;

import java.io.FileNotFoundException;
import java.io.IOException;

public class VerifyCameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "AFE_VerifyCamera";
    private static final int ALBUM_OK = 0;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mPreviewRotation = 90;
    private int mPreviewWidth = 1280;
    private int mPreviewHeight = 720;

    private OrientationEventListener mOrientationEventListener;

    private FaceDetect mFaceDetect;
    private FaceVerify mFaceVerify;
    private FaceAttributeAnalyze mFaceAttributeAnalyze;
    private FaceVerify.VerifyVideoListener mVerifyVideoListener;
    private VerifyResult[] mVerifyResults;

    private FaceFrameView[] mFaceFrameViews;
    private ImageView mRegisteredPhotoView;
    private TextView mChannelTitleView;
    private FrameLayout mRootViews;
    private ImageButton mBackBtn, mSwitchCameraBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_verifycamera);

        mSurfaceView = (SurfaceView) findViewById(R.id.activity_verifycamera_sv);
        mRegisteredPhotoView = (ImageView) findViewById(R.id.activity_verifycamera_iv);
        mChannelTitleView = (TextView) findViewById(R.id.currency_tv_title);
        mRootViews = (FrameLayout) findViewById(R.id.activity_verifycamera_frame);
        mBackBtn = (ImageButton) findViewById(R.id.currency_btn_back);
        mSwitchCameraBtn = (ImageButton) findViewById(R.id.activity_verifycamera_ib);
        mChannelTitleView.setText(R.string.verify_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFixedSize(mPreviewWidth, mPreviewHeight);
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
                if (mFaceFrameViews != null) {
                    for (int j = 0; j < mFaceFrameViews.length; j++) {
                        mRootViews.removeView(mFaceFrameViews[j]);
                    }
                }
            }
        });

        mOrientationEventListener = new OrientationEventListener(VerifyCameraActivity.this) {
            @Override
            public void onOrientationChanged(int orientation) {
                orientationChanged(orientation);
            }
        };

        mFaceDetect = FaceDetect.createInstance(Mode.TERMINAL);
        mFaceVerify = FaceVerify.createInstance(Mode.TERMINAL);
        mFaceAttributeAnalyze = FaceAttributeAnalyze.createInstance(Mode.TERMINAL);
        //mFaceAttributeAnalyze.setFlag(FaceAttributeAnalyze.QUALITY);
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
        FaceAttributeAnalyze.deleteInstance(mFaceAttributeAnalyze);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Image image = new Image();
        image.data = data;
        image.format = com.alibaba.cloud.faceengine.ImageFormat.NV21;
        image.width = mPreviewWidth;
        image.height = mPreviewHeight;
        image.rotation = ImageRotation.ANGLE_0;

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

        Face[] faces = mFaceDetect.detectVideo(image);
        if (faces != null) {
            Log.d(TAG, "mFaceAttributeAnalyze.analyze begin");
            mFaceAttributeAnalyze.analyze(image, faces);
            Log.d(TAG, "mFaceAttributeAnalyze.analyze end");
            for (int i = 0; i < faces.length; i++) {
                Log.d(TAG, "faces[" + i + "]=" + faces[i]);
            }
        }

        if (faces != null && faces.length > 0) {
            Log.d(TAG, "verifyVideo begin");
            mFaceVerify.verifyVideo(image, faces);
        }
        drawAllFaces(image, faces);
    }

    private void drawAllFaces(Image image, Face[] faces) {
        if (mFaceFrameViews != null) {
            for (int i = 0; i < mFaceFrameViews.length; i++) {
                if (mFaceFrameViews[i] != null) {
                    mRootViews.removeView(mFaceFrameViews[i]);
                }
            }
            mFaceFrameViews = null;
        }

        if (faces == null) {
            return;
        }

        mFaceFrameViews = new FaceFrameView[faces.length];
        for (int i = 0; i < faces.length; i++) {
            mFaceFrameViews[i] = new FaceFrameView(VerifyCameraActivity.this);
            drawFaceRect(image, faces[i], mFaceFrameViews[i]);
        }
    }

    private void drawFaceRect(Image image, Face face, FaceFrameView faceFrameView) {
        int pWidth = mRootViews.getWidth();
        int pHeight = mRootViews.getHeight();
        float mrateWidth = ((float) pWidth) / mPreviewHeight;
        float mrateHeight = ((float) pHeight) / mPreviewWidth;

        int mleft = face.rect.left;
        int mtop = face.rect.top;
        int mright = face.rect.right;
        int mbottom = face.rect.bottom;

        boolean verifySuccess = false;
        String resultText = this.getString(R.string.please_select_photo);
        VerifyResult result = getVerifyResult(face.trackId);
        if (result != null) {
            if (result.similarity >= 70.0) {
                verifySuccess = true;
                resultText = result.similarity + "";
            } else {
                verifySuccess = false;
                resultText = result.similarity + "";
            }
        }
        faceFrameView.setResult(resultText, verifySuccess);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round((mright * mrateWidth) - (mleft * mrateWidth)), Math.round((mbottom * mrateHeight) - (mtop * mrateHeight)));
        faceFrameView.setLayoutParams(lp);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(faceFrameView.getLayoutParams());

        if (mPreviewRotation == 90) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(mleft * mrateWidth), Math.round((image.height - mbottom) * mrateHeight), Math.round((image.width - mright) * mrateWidth), Math.round((image.height - mtop) * mrateHeight));
                faceFrameView.setRotation(180);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(mleft * mrateWidth), Math.round(mtop * mrateHeight), Math.round((image.width - mright) * mrateWidth), Math.round(mbottom * mrateHeight));
            }
        } else if (mPreviewRotation == 0) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(pWidth - mbottom * mrateWidth), Math.round(pHeight - mright * mrateHeight), Math.round(pWidth - mtop * mrateWidth), Math.round(pHeight - mleft * mrateHeight));
                faceFrameView.setRotation(90);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(pWidth - mbottom * mrateWidth), Math.round(mleft * mrateHeight), Math.round(pWidth - mtop * mrateWidth), Math.round(mright * mrateHeight));
                faceFrameView.setRotation(90);
            }
        } else if (mPreviewRotation == 180) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round(mtop * mrateWidth), Math.round(mleft * mrateHeight), Math.round(mbottom * mrateWidth), Math.round(mright * mrateHeight));
                faceFrameView.setRotation(270);
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round(mtop * mrateWidth), Math.round(pHeight - mright * mrateHeight), Math.round(mbottom * mrateWidth), Math.round(pHeight - mleft * mrateHeight));
                faceFrameView.setRotation(270);
            }
        } else {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                params.setMargins(Math.round((image.width - mright) * mrateWidth), Math.round(mtop * mrateHeight), Math.round(mleft * mrateWidth), Math.round(mbottom * mrateHeight));
            } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setMargins(Math.round((image.width - mright) * mrateWidth), Math.round((image.height - mbottom) * mrateHeight), Math.round(mleft * mrateWidth), Math.round((image.height - mtop) * mrateHeight));
                faceFrameView.setRotation(180);
            }
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(params);
        mRootViews.addView(faceFrameView, layoutParams);
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
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
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
            int status = mFaceVerify.registerFace(image, faces[0]);
            Log.d(TAG, "registerFace status: " + status);

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
