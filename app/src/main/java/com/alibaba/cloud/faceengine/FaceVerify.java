package com.alibaba.cloud.faceengine;

public class FaceVerify {
    public interface VerifyVideoListener {
        void onVerified(Image image, VerifyResult results[]);
    }

    public static FaceVerify createInstance(int mode) {
        long context = FaceVerifyJNI.createInstance(mode);
        if (context == 0) {
            return null;
        }

        FaceVerify ins = new FaceVerify(context);
        ins.mContext = context;
        return ins;
    }

    public static void deleteInstance(FaceVerify ins) {
        if (ins != null) {
            FaceVerifyJNI.deleteInstance(ins.mContext);
        }
    }

    private long mContext;
    private VerifyVideoListener mVerifyVideoListener;

    private FaceVerify(long context) {
        this.mContext = context;
    }

    public void setVerifyVideoListener(VerifyVideoListener listener) {
        mVerifyVideoListener = listener;
        FaceVerifyJNI.setVerifyVideoListener(mContext, listener);
    }

    public VerifyVideoListener getVerifyVideoListener() {
        return mVerifyVideoListener;
    }

    public int verifyVideo(Image image, Face faces[]) {
        return FaceVerifyJNI.verifyVideo(mContext, image, faces);
    }

    public int registerFace(Image image, Face face) {
        return FaceVerifyJNI.registerFace(mContext, image, face);
    }

    public VerifyResult[] verifyPicture(Image image1, Face image1Face, Image image2, Face image2Faces[]) {
        return FaceVerifyJNI.verifyPicture(mContext, image1, image1Face, image2, image2Faces);
    }

}
