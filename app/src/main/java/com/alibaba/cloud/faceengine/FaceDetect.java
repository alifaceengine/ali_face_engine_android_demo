package com.alibaba.cloud.faceengine;

public class FaceDetect {
    private long mContext;

    public static FaceDetect createInstance(int mode) {
        long context = FaceDetectJNI.createInstance(mode);
        if (context == 0) {
            return null;
        }

        return new FaceDetect(context);
    }

    public static void deleteInstance(FaceDetect ins) {
        if (ins != null) {
            FaceDetectJNI.deleteInstance(ins.getContext());
        }
    }

    protected FaceDetect(long context) {
        mContext = context;
    }

    protected long getContext() {
        return mContext;
    }

    public int setPictureParameter(DetectParameter parameter) {
        return FaceDetectJNI.setPictureParameter(mContext, parameter);
    }

    public DetectParameter getPictureParameter() {
        return FaceDetectJNI.getPictureParameter(mContext);
    }

    public Face[] detectPicture(Image image) {
        return FaceDetectJNI.detectPicture(mContext, image);
    }

    public int setVideoParameter(DetectParameter parameter) {
        return FaceDetectJNI.setVideoParameter(mContext, parameter);
    }

    public DetectParameter getVideoParameter() {
        return FaceDetectJNI.getVideoParameter(mContext);
    }

    public Face[] detectVideo(Image image) {
        return FaceDetectJNI.detectVideo(mContext, image);
    }
}
