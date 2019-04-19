package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/29.
 */

public class FaceRegister {
    public static FaceRegister createInstance() {
        long context = FaceRegisterJNI.createInstance();
        if (context == 0) {
            return null;
        }

        FaceRegister ins = new FaceRegister();
        ins.mContext = context;
        return ins;
    }

    public static void deleteInstance(FaceRegister ins) {
        if (ins != null) {
            FaceRegisterJNI.deleteInstance(ins.mContext);
        }
    }

    protected long mContext;

    public int createGroup(Group group) {
        return FaceRegisterJNI.createGroup(mContext, group);
    }

    public int deleteGroup(String groupId) {
        return FaceRegisterJNI.deleteGroup(mContext, groupId);
    }

    public boolean isGroupExist(String groupName) {
        return FaceRegisterJNI.isGroupExist(mContext, groupName);
    }

    public int updateGroup(String groupId, Group group) {
        return FaceRegisterJNI.updateGroup(mContext, groupId, group);
    }

    public Group getGroup(String groupId) {
        return FaceRegisterJNI.getGroup(mContext, groupId);
    }

    public Group getGroupByName(String groupName) {
        return FaceRegisterJNI.getGroupByName(mContext, groupName);
    }

    public Group[] getAllGroups() {
        return FaceRegisterJNI.getAllGroups(mContext);
    }

    public int addPerson(String groupId, Person person) {
        return FaceRegisterJNI.addPerson(mContext, groupId, person);
    }

    public int deletePerson(String personId) {
        return FaceRegisterJNI.deletePerson(mContext, personId);
    }

    public int deleteAllPersons(String groupId) {
        return FaceRegisterJNI.deleteAllPersons(mContext, groupId);
    }

    public int updatePerson(String personId, Person person) {
        return FaceRegisterJNI.updatePerson(mContext, personId, person);
    }

    public Person getPerson(String personId) {
        return FaceRegisterJNI.getPerson(mContext, personId);
    }

    public Person getPersonByName(String groupId, String personName) {
        return FaceRegisterJNI.getPersonByName(mContext, groupId, personName);
    }

    public Person[] getAllPersons(String groupId) {
        return FaceRegisterJNI.getAllPersons(mContext, groupId);
    }

    public int getPersonNum(String groupId) {
        return FaceRegisterJNI.getPersonNum(mContext, groupId);
    }

    public boolean isPersonExist(String groupId, String personName) {
        return FaceRegisterJNI.isPersonExist(mContext, groupId, personName);
    }

    public int addFeature(String personId, Feature feature) {
        return FaceRegisterJNI.addFeature(mContext, personId, feature);
    }

    public int deleteFeature(String featureId) {
        return FaceRegisterJNI.deleteFeature(mContext, featureId);
    }

    public int deleteAllFeatures(String personId) {
        return FaceRegisterJNI.deleteAllFeatures(mContext, personId);
    }

    public int updateFeature(String featureId, Feature feature) {
        return FaceRegisterJNI.updateFeature(mContext, featureId, feature);
    }

    public int getFeatureNum(String personId) {
        return FaceRegisterJNI.getFeatureNum(mContext, personId);
    }

    public String extractFeature(Image image, Face face, int modelType) {
        return FaceRegisterJNI.extractFeature(mContext, image, face, modelType);
    }

}
