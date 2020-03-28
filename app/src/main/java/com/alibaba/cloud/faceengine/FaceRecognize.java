package com.alibaba.cloud.faceengine;

public class FaceRecognize {
    public interface RecognizeVideoListener {
        void onRecognized(Image image, RecognizeResult[] results);
    }

    public static FaceRecognize createInstance(String groupName, int mode) {
        long context = FaceRecognizeJNI.createInstance(groupName, mode);
        if (context == 0) {
            return null;
        }

        FaceRecognize ins = new FaceRecognize();
        ins.mContext = context;
        return ins;
    }

    public static void deleteInstance(FaceRecognize ins) {
        if (ins != null) {
            FaceRecognizeJNI.deleteInstance(ins.mContext);
        }
    }

    protected long mContext;

    private RecognizeVideoListener mListener;

    public void setRecognizeVideoListener(RecognizeVideoListener listener) {
        mListener = listener;
        FaceRecognizeJNI.setRecognizeVideoListener(mContext, listener);
    }

    public RecognizeVideoListener getRecognizeVideoListener() {
        return mListener;
    }

    public int reloadDB() {
        return FaceRecognizeJNI.reloadDB(mContext);
    }

    public int recognizeVideo(Image image, Face[] faces) {
        return FaceRecognizeJNI.recognizeVideo(mContext, image, faces);
    }

    public RecognizeResult[] recognizePicture(Image image, Face[] faces) {
        if (image == null || image.data == null) {
            return null;
        }
        return FaceRecognizeJNI.recognizePicture(mContext, image, faces);
    }

    public RecognizeResult recognizeFeature(String feature) {
        return FaceRecognizeJNI.recognizeFeature(mContext, feature);
    }
}
