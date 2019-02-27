package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/29.
 */

class FaceRegisterJNI {
    public static native long createInstance();

    public static native void deleteInstance(long context);

    public static native int createGroup(long context, Group group);

    public static native int deleteGroup(long context, String groupId);

    public static native boolean isGroupExist(long context, String groupName);

    public static native int updateGroup(long context, String groupId, Group group);

    public static native Group getGroup(long context, String groupId);

    public static native Group[] getAllGroups(long context);

    public static native int addPerson(long context, String groupId, Person person);

    public static native int deletePerson(long context, String personId);

    public static native int deleteAllPersons(long context, String groupId);

    public static native int updatePerson(long context, String personId, Person person);

    public static native Person getPerson(long context, String personId);

    public static native Person[] getAllPersons(long context, String groupId);

    public static native int getPersonNum(long context, String groupId);

    public static native boolean isPersonExist(long context, String groupId, String personName);

    public static native int addFeature(long context, String personId, Feature feature);

    public static native int deleteFeature(long context, String featureId);

    public static native int deleteAllFeatures(long context, String personId);

    public static native int updateFeature(long context, String featureId, Feature feature);

    public static native int getFeatureNum(long context, String personId);

    public static native String extractFeature(long context, Image image, Face face, int modelType);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}
