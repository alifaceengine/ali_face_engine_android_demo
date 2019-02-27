#include <jni.h>
#include <string>
#include <stdlib.h>
#include <list>

#include "FaceEngineHelper.h"
#include "Tools.h"
#include "Codec.h"

#define TAG "AliFaceEngineJNI"

using namespace ali_face_engine;

typedef void (*RecognizeVideoListener_onRecognized)(void *context, Image *image,
                                                    list<RecognizeResult> &resultList);

typedef void (*VerifyVideoListener_onVerified)(void *context, Image *image,
                                               list<VerifyResult> &resultList);

class RecognizeVideoListenerImp : public FaceRecognize::RecognizeVideoListener {
public:
    RecognizeVideoListenerImp() : mContext(0), mListener(0) {

    }

    virtual ~RecognizeVideoListenerImp() {

    }

    virtual void onRecognized(Image &image, list<RecognizeResult> resultList) {
        if (mListener) {
            mListener(mContext, &image, resultList);
        }
    }

    virtual void onVerified(Image &image, list<VerifyResult> resultList) {

    }

    void setContextAndListener(void *context, RecognizeVideoListener_onRecognized listener) {
        mContext = context;
        mListener = listener;
    }

    RecognizeVideoListener_onRecognized getListener() {
        return mListener;
    }

    void *getContext() {
        return mContext;
    }

private:
    void *mContext;
    RecognizeVideoListener_onRecognized mListener;
};

class VerifyVideoListenerImp : public FaceVerify::VerifyVideoListener {
public:
    VerifyVideoListenerImp() : mContext(0), mListener(0) {

    }

    virtual ~VerifyVideoListenerImp() {

    }

    virtual void onVerified(Image &image, list<VerifyResult> resultList) {
        if (mListener) {
            mListener(mContext, &image, resultList);
        }
    }

    void setContextAndListener(void *context, VerifyVideoListener_onVerified listener) {
        mContext = context;
        mListener = listener;
    }

    VerifyVideoListener_onVerified getListener() {
        return mListener;
    }

    void *getContext() {
        return mContext;
    }

private:
    void *mContext;
    VerifyVideoListener_onVerified mListener;
};

extern "C"
JNIEXPORT jstring JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_getVersion(
        JNIEnv *env, jobject obj) {
    char *version = ali_face_engine::getVersion();
    return env->NewStringUTF(version);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_authorize(
        JNIEnv *env, jobject obj,
        jstring key) {
    const char *pkey = 0;
    if (key) {
        pkey = env->GetStringUTFChars(key, 0);
    }

    int status = ali_face_engine::authorize((char *) pkey);

    if (key) {
        env->ReleaseStringUTFChars(key, pkey);
    }
    return status;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_setCloudAddr(
        JNIEnv *env, jobject obj,
        jstring ip, jint port) {
    const char *pip = 0;
    if (ip) {
        pip = env->GetStringUTFChars(ip, 0);
    }
    ali_face_engine::setCloudAddr(pip, port);
    if (ip) {
        env->ReleaseStringUTFChars(ip, pip);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_setCloudLoginAccount(
        JNIEnv *env, jobject obj,
        jstring cId, jstring cSecret) {
    const char *pcId = 0;
    if (cId) {
        pcId = env->GetStringUTFChars(cId, 0);
    }
    const char *pcSecret = 0;
    if (cSecret) {
        pcSecret = env->GetStringUTFChars(cSecret, 0);
    }
    ali_face_engine::setCloudLoginAccount(pcId, pcSecret);
    if (cId) {
        env->ReleaseStringUTFChars(cId, pcId);
    }
    if (cSecret) {
        env->ReleaseStringUTFChars(cSecret, pcSecret);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_enableDebug(
        JNIEnv *env, jobject obj,
        jint enable) {
    ali_face_engine::enableDebug(enable);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_setPersistencePath(
        JNIEnv *env, jobject obj,
        jstring path) {
    const char *ppath = 0;
    if (path) {
        ppath = env->GetStringUTFChars(path, 0);
    }
    ali_face_engine::setPersistencePath(ppath);
    if (path) {
        env->ReleaseStringUTFChars(path, ppath);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_AliFaceEngineJNI_setThreadNum(
        JNIEnv *env, jobject obj,
        jint num) {
    ali_face_engine::setThreadNum(num);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_createInstance(
        JNIEnv *env, jobject obj,
        jint mode) {
    return (long) FaceDetect::createInstance((enum Mode) mode);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_deleteInstance(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceDetect::deleteInstance((FaceDetect *&) context);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_setPictureParameter(
        JNIEnv *env, jobject obj,
        jlong context, jobject parameter) {
    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return FAILED;
    }

    DetectParameter cparameter;
    int error = JDetectParameterToCDetectParameter(env, parameter, &cparameter);
    if (error < 0) {
        return error;
    }
    int status = ins->setPictureParameter(cparameter);
    return status;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_getPictureParameter(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return 0;
    }

    DetectParameter cparameter = ins->getPictureParameter();
    jobject jparameter = CDetectParameterToJDetectParameter(env, &cparameter);
    return jparameter;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_detectPicture(
        JNIEnv *env, jobject obj,
        jlong context, jobject image) {

    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return 0;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    list<Face> faceList;
    int status = ins->detectPicture(cimage, faceList);
    JImage.releaseJObjectArrs(env, cimage, jimageData);

    jobjectArray jfaces = CFaceListToJFaceArray(env, faceList);

    return jfaces;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_setVideoParameter(
        JNIEnv *env, jobject obj,
        jlong context, jobject parameter) {
    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return FAILED;
    }

    DetectParameter cparameter;
    int error = JDetectParameterToCDetectParameter(env, parameter, &cparameter);
    if (error < 0) {
        return error;
    }
    int status = ins->setVideoParameter(cparameter);
    return status;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_getVideoParameter(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return 0;
    }

    DetectParameter cparameter = ins->getVideoParameter();
    jobject jparameter = CDetectParameterToJDetectParameter(env, &cparameter);
    return jparameter;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_setDetectVideoType(
        JNIEnv *env, jobject obj,
        jlong context, jint type) {
    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return FAILED;
    }
    return ins->setDetectVideoType((FaceDetect::DetectVideoType) type);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_alibaba_cloud_faceengine_FaceDetectJNI_detectVideo(
        JNIEnv *env, jobject obj,
        jlong context, jobject image) {
    FaceDetect *ins = (FaceDetect *) context;
    if (!ins) {
        return 0;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    list<Face> faceList;
    int status = ins->detectVideo(cimage, faceList);
    JImage.releaseJObjectArrs(env, cimage, jimageData);

    jobjectArray jfaces = CFaceListToJFaceArray(env, faceList);

    return jfaces;
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_cloud_faceengine_FaceAttributeAnalyzeJNI_createInstance(
        JNIEnv *env, jobject obj,
        jint mode) {
    return (long) FaceAttributeAnalyze::createInstance((enum Mode) mode);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceAttributeAnalyzeJNI_deleteInstance(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceAttributeAnalyze::deleteInstance((FaceAttributeAnalyze *&) context);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceAttributeAnalyzeJNI_setFlag(
        JNIEnv *env, jobject obj,
        jlong context, jint flag) {
    FaceAttributeAnalyze *ins = (FaceAttributeAnalyze *) context;
    if (!ins) {
        return;
    }
    ins->setFlag(flag);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceAttributeAnalyzeJNI_getFlag(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceAttributeAnalyze *ins = (FaceAttributeAnalyze *) context;
    if (!ins) {
        return 0;
    }
    return ins->getFlag();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceAttributeAnalyzeJNI_analyze(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobject face) {
    FaceAttributeAnalyze *ins = (FaceAttributeAnalyze *) context;
    if (!ins) {
        return FAILED;
    }

    if (!face) {
        return FAILED;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    Face cface;
    JFace.toCObject(env, face, cface);
    int status = ins->analyze(cimage, cface);
    JFace.toJObject(env, cface, face);

    JImage.releaseJObjectArrs(env, cimage, jimageData);
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceAttributeAnalyzeJNI_analyze2(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobjectArray faces) {
    FaceAttributeAnalyze *ins = (FaceAttributeAnalyze *) context;
    if (!ins) {
        return FAILED;
    }

    if (!faces) {
        return FAILED;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    list<Face> cfaceList;
    JFaceArrayToCFaceList(env, faces, cfaceList);
    int status = ins->analyze(cimage, cfaceList);
    CFaceListToJFaceArray(env, cfaceList, faces);

    JImage.releaseJObjectArrs(env, cimage, jimageData);
    return status;
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_createInstance(
        JNIEnv *env, jobject obj) {
    return (long) FaceRegister::createInstance();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_deleteInstance(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceRegister *ins = (FaceRegister *) context;
    if (ins) {
        FaceRegister::deleteInstance((FaceRegister *&) ins);
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_createGroup(
        JNIEnv *env, jobject obj,
        jlong context, jobject group) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    if (!group) {
        return FAILED;
    }

    Group cgroup;
    JGroupInfo.toCObject(env, group, cgroup);
    int status = ins->createGroup(cgroup);

    if (cgroup.id.size() > 0) {
        jstring jid = env->NewStringUTF(cgroup.id.c_str());
        env->SetObjectField(group, JGroupInfo.f_id.jField, jid);
    }

    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_deleteGroup(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    int status = ins->deleteGroup(c_groupId);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }

    return status;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_isGroupExist(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupName) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return JNI_FALSE;
    }

    const char *c_groupName = 0;
    if (groupName) {
        c_groupName = env->GetStringUTFChars(groupName, 0);
    }

    bool exist = ins->isGroupExist(c_groupName);

    if (groupName) {
        env->ReleaseStringUTFChars(groupName, c_groupName);
    }

    return exist ? JNI_TRUE : JNI_FALSE;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_updateGroup(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId, jobject group) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    string str_groupId;
    if (c_groupId) {
        str_groupId = c_groupId;
    }

    Group cgroup;
    JGroupInfo.toCObject(env, group, cgroup);
    int status = ins->updateGroup(str_groupId, cgroup);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }

    return status;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_getGroup(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    Group cgroup;
    int status = ins->getGroup(c_groupId, cgroup);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }

    if (status == OK) {
        jobject jgroupInfo = JGroupInfo.toJObject(env, cgroup);
        return jgroupInfo;
    } else {
        return 0;
    }
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_getAllGroups(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    list<Group> groupList;
    ins->getAllGroups(groupList);
    jobjectArray jgroupInfos = CGroupInfoArrayToJGroupInfoArray(env, groupList);
    return jgroupInfos;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_addPerson(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId, jobject person) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins || !person) {
        return FAILED;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    Person cperson;
    JPerson.toCObject(env, person, cperson);
    int status = ins->addPerson(c_groupId, cperson);

    if (status == OK || status == ERROR_EXISTED || status == ERROR_CLOUD_EXISTED_ERROR) {
        jstring jid = env->NewStringUTF(cperson.id.c_str());
        env->SetObjectField(person, JPerson.f_id.jField, jid);
        env->DeleteLocalRef(jid);
    }

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }

    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_deletePerson(
        JNIEnv *env, jobject obj,
        jlong context, jstring personId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_personId = 0;
    if (personId) {
        c_personId = env->GetStringUTFChars(personId, 0);
    }

    int status = ins->deletePerson(c_personId);

    if (personId) {
        env->ReleaseStringUTFChars(personId, c_personId);
    }

    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_deleteAllPersons(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    int status = ins->deleteAllPersons(c_groupId);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_updatePerson(
        JNIEnv *env, jobject obj,
        jlong context, jstring personId, jobject person) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_personId = 0;
    if (personId) {
        c_personId = env->GetStringUTFChars(personId, 0);
    }

    Person cperson;
    JPerson.toCObject(env, person, cperson);
    int status = ins->updatePerson(c_personId, cperson);

    if (c_personId) {
        env->ReleaseStringUTFChars(personId, c_personId);
    }

    return status;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_getPerson(
        JNIEnv *env, jobject obj,
        jlong context, jstring personId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    const char *c_personId = 0;
    if (personId) {
        c_personId = env->GetStringUTFChars(personId, 0);
    }

    Person person;
    int status = ins->getPerson(c_personId, person);

    if (personId) {
        env->ReleaseStringUTFChars(personId, c_personId);
    }

    if (status == OK) {
        jobject jperson = JPerson.toJObject(env, &person);
        return jperson;
    } else {
        return 0;
    }
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_getAllPersons(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId) {

    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    list<Person> personList;
    ins->getAllPersons(c_groupId, personList);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }

    jobjectArray jpersonArray = CPersonArrayToJPersonArray(env, personList);
    return jpersonArray;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_getPersonNum(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    int personNum = 0;
    int status = ins->getPersonNum(c_groupId, personNum);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }

    return personNum;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_isPersonExist(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId, jstring personName) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return JNI_FALSE;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }
    const char *c_personName = 0;
    if (personName) {
        c_personName = env->GetStringUTFChars(personName, 0);
    }

    bool exist = ins->isPersonExist(c_groupId, c_personName);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }
    if (personName) {
        env->ReleaseStringUTFChars(personName, c_personName);
    }

    return exist ? JNI_TRUE : JNI_FALSE;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_addFeature(
        JNIEnv *env, jobject obj,
        jlong context, jstring personId, jobject feature) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_personId = 0;
    if (personId) {
        c_personId = env->GetStringUTFChars(personId, 0);
    }

    Feature cfeature;
    JFeature.toCObject(env, feature, cfeature);
    int status = ins->addFeature(c_personId, cfeature);
    if (cfeature.id.size() > 0) {
        jstring jid = env->NewStringUTF(cfeature.id.c_str());
        env->SetObjectField(feature, JFeature.f_id.jField, jid);
    }

    if (personId) {
        env->ReleaseStringUTFChars(personId, c_personId);
    }
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_deleteFeature(
        JNIEnv *env, jobject obj,
        jlong context, jstring featureId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_featureId = 0;
    if (featureId) {
        c_featureId = env->GetStringUTFChars(featureId, 0);
    }

    int status = ins->deleteFeature(c_featureId);

    if (featureId) {
        env->ReleaseStringUTFChars(featureId, c_featureId);
    }
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_deleteAllFeatures(
        JNIEnv *env, jobject obj,
        jlong context, jstring personId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_personId = 0;
    if (personId) {
        c_personId = env->GetStringUTFChars(personId, 0);
    }

    int status = ins->deleteAllFeatures(c_personId);

    if (personId) {
        env->ReleaseStringUTFChars(personId, c_personId);
    }
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_updateFeature(
        JNIEnv *env, jobject obj,
        jlong context, jstring featureId, jobject feature) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return FAILED;
    }

    const char *c_featureId = 0;
    if (featureId) {
        c_featureId = env->GetStringUTFChars(featureId, 0);
    }

    Feature cfeature;
    JFeature.toCObject(env, feature, cfeature);
    int status = ins->updateFeature(c_featureId, cfeature);

    if (featureId) {
        env->ReleaseStringUTFChars(featureId, c_featureId);
    }

    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_getFeatureNum(
        JNIEnv *env, jobject obj,
        jlong context, jstring personId) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    const char *c_personId = 0;
    if (personId) {
        c_personId = env->GetStringUTFChars(personId, 0);
    }

    int num = 0;
    int status = ins->getFeatureNum(c_personId, num);

    if (personId) {
        env->ReleaseStringUTFChars(personId, c_personId);
    }
    return num;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_alibaba_cloud_faceengine_FaceRegisterJNI_extractFeature(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobject face, jint modelType) {
    FaceRegister *ins = (FaceRegister *) context;
    if (!ins) {
        return 0;
    }

    Face *cface = JFace.toCObject(env, face);
    if (!cface) {
        return 0;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    string feature;
    feature = "a";
    feature = "";
    int status = ins->extractFeature(cimage, *cface, (enum ModelType) modelType, feature);
    JImage.releaseJObjectArrs(env, cimage, jimageData);
    if (cface) {
        deleteFace(cface);
    }

    if (feature.size() > 0) {
        jstring jfeature = env->NewStringUTF(feature.c_str());
        return jfeature;
    } else {
        return 0;
    }
}

static void
RecognizeVideoListener_onRecognizedImp(void *listenerContext, Image *image,
                                       list<RecognizeResult> &resultList) {
    JNIEnv *env = NULL;
    GET_JNIENV_STATUS status = getJNIEnv(&env);
    if (status == GET_FAIL) {
        return;
    }

    jobject listener = (jobject) listenerContext;
    jobjectArray jresults = CRecognizeResultArrayToJRecognizeResultArray(env, resultList);

    env->CallVoidMethod(listener, JFaceRecognize_RecognizeVideoListener.m_onRecognized.jMethod, 0,
                        jresults);

    if (status == GET_SUCCES_ATTCH) {
        detachCurrentThread();
    }
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_createInstance(
        JNIEnv *env, jobject obj,
        jint mode) {
    return (long) FaceRecognize::createInstance((enum Mode) mode);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_deleteInstance(
        JNIEnv *env, jobject obj,
        jlong context) {
    void *listenerContext = 0;
    FaceRecognize *ins = (FaceRecognize *) context;
    if (ins) {
        FaceRecognize::RecognizeVideoListener *clistener = ins->getRecognizeVideoListener();
        if (clistener) {
            listenerContext = ((RecognizeVideoListenerImp *) clistener)->getContext();
        }
    }

    if (ins) {
        FaceRecognize::deleteInstance((FaceRecognize *&) ins);
    }

    if (listenerContext) {
        jobject listenerContext_obj = (jobject) listenerContext;
        env->DeleteGlobalRef(listenerContext_obj);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_setRecognizeVideoListener(
        JNIEnv *env, jobject obj,
        jlong context, jobject listener) {
    FaceRecognize *ins = (FaceRecognize *) context;
    if (ins) {
        FaceRecognize::RecognizeVideoListener *clistener = ins->getRecognizeVideoListener();
        if (!clistener) {
            clistener = new RecognizeVideoListenerImp();
            jobject listener_new = env->NewGlobalRef(listener);
            ((RecognizeVideoListenerImp *) clistener)->setContextAndListener(listener_new,
                                                                             RecognizeVideoListener_onRecognizedImp);
            ins->setRecognizeVideoListener(clistener);
        }
    }
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_getRecognizeVideoListener(
        JNIEnv *env, jobject obj,
        jlong context, jobject listener) {
    FaceRecognize *ins = (FaceRecognize *) context;
    if (ins) {
        FaceRecognize::RecognizeVideoListener *clistener = ins->getRecognizeVideoListener();
        if (clistener) {
            return (jobject) ((RecognizeVideoListenerImp *) clistener)->getContext();
        } else {
            return 0;
        }
    } else {
        return 0;
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_setGroupId(
        JNIEnv *env, jobject obj,
        jlong context, jstring groupId) {
    FaceRecognize *ins = (FaceRecognize *) context;
    if (!ins) {
        return 0;
    }

    const char *c_groupId = 0;
    if (groupId) {
        c_groupId = env->GetStringUTFChars(groupId, 0);
    }

    int status = ins->setGroupId(c_groupId);

    if (groupId) {
        env->ReleaseStringUTFChars(groupId, c_groupId);
    }
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_reloadDB(
        JNIEnv *env, jobject obj,
        jlong context) {
    FaceRecognize *ins = (FaceRecognize *) context;
    if (!ins) {
        return 0;
    }

    int status = ins->reloadDB();
    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_recognizeVideo(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobjectArray faces) {
    FaceRecognize *ins = (FaceRecognize *) context;
    if (!ins) {
        return FAILED;
    }

    if (!faces) {
        return 0;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    list<Face> cfaceList;
    JFaceArrayToCFaceList(env, faces, cfaceList);
    int status = ins->recognizeVideo(cimage, cfaceList);

    JImage.releaseJObjectArrs(env, cimage, jimageData);
    return status;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_alibaba_cloud_faceengine_FaceRecognizeJNI_recognizePicture(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobjectArray faces) {
    FaceRecognize *ins = (FaceRecognize *) context;
    if (!ins) {
        return 0;
    }

    if (!faces) {
        return 0;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    list<Face> cfaceList;
    JFaceArrayToCFaceList(env, faces, cfaceList);

    list<RecognizeResult> recognizeResultList;
    int status = ins->recognizePicture(cimage, cfaceList, recognizeResultList);
    JImage.releaseJObjectArrs(env, cimage, jimageData);

    jobjectArray jrecognizeResults = CRecognizeResultArrayToJRecognizeResultArray(env,
                                                                                  recognizeResultList);
    return jrecognizeResults;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_createInstance(
        JNIEnv *env, jobject obj,
        jint mode) {
    return (long) FaceVerify::createInstance((enum Mode) mode);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_deleteInstance(
        JNIEnv *env, jobject obj,
        jlong context) {
    void *listenerContext = 0;
    FaceVerify *ins = (FaceVerify *) context;
    if (ins) {
        FaceVerify::VerifyVideoListener *clistener = ins->getVerifyVideoListener();
        if (clistener) {
            listenerContext = ((VerifyVideoListenerImp *) clistener)->getContext();
        }
    }

    if (ins) {
        FaceVerify::deleteInstance((FaceVerify *&) ins);
    }

    if (listenerContext) {
        jobject listenerContext_obj = (jobject) listenerContext;
        env->DeleteGlobalRef(listenerContext_obj);
    }

}

static void
VerifyVideoListener_onVerifiedImp(void *listenerContext, Image *image,
                                  list<VerifyResult> &resultList) {
    JNIEnv *env = NULL;
    GET_JNIENV_STATUS status = getJNIEnv(&env);
    if (status == GET_FAIL) {
        return;
    }

    jobject listener = (jobject) listenerContext;
    jobjectArray jresults = CVerifyResultArrayToJVerifyResultArray(env, resultList);

    env->CallVoidMethod(listener, JFaceVerify_VerifyVideoListener.m_onVerified.jMethod, 0,
                        jresults);
    if (status == GET_SUCCES_ATTCH) {
        detachCurrentThread();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_setVerifyVideoListener(
        JNIEnv *env, jobject obj,
        jlong context, jobject listener) {
    FaceVerify *ins = (FaceVerify *) context;
    if (ins) {
        FaceVerify::VerifyVideoListener *clistener = ins->getVerifyVideoListener();
        if (!clistener) {
            clistener = new VerifyVideoListenerImp();
            jobject listener_new = env->NewGlobalRef(listener);
            ((VerifyVideoListenerImp *) clistener)->setContextAndListener(listener_new,
                                                                          VerifyVideoListener_onVerifiedImp);
            ins->setVerifyVideoListener(clistener);
        }
    }
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_getVerifyVideoListener(
        JNIEnv *env, jobject obj,
        jlong context, jobject listener) {
    FaceVerify *ins = (FaceVerify *) context;
    if (ins) {
        FaceVerify::VerifyVideoListener *clistener = ins->getVerifyVideoListener();
        if (clistener) {
            return (jobject) ((VerifyVideoListenerImp *) clistener)->getContext();
        } else {
            return 0;
        }
    } else {
        return 0;
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_verifyVideo(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobjectArray faces) {
    FaceVerify *ins = (FaceVerify *) context;
    if (!ins) {
        return FAILED;
    }

    int error = FAILED;

    if (!faces) {
        return error;
    }

    list<Face> faceLists;
    int faceNum = env->GetArrayLength(faces);
    for (int i = 0; i < faceNum; i++) {
        Face face;
        JFace.toCObject(env, env->GetObjectArrayElement(faces, i), face);
        faceLists.push_back(face);
    }

    Image cimage;
    jobject jimageData = 0;
    error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return error;
    }

    int status = ins->verifyVideo(cimage, faceLists);
    JImage.releaseJObjectArrs(env, cimage, jimageData);

    return status;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_registerFace(
        JNIEnv *env, jobject obj,
        jlong context, jobject image, jobject face) {
    FaceVerify *ins = (FaceVerify *) context;
    if (!ins) {
        return FAILED;
    }

    if (!face) {
        return FAILED;
    }

    Face *cface = JFace.toCObject(env, face);
    if (!cface) {
        return FAILED;
    }

    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < FAILED) {
        return FAILED;
    }

    int status = ins->registerFace(cimage, *cface);
    JImage.releaseJObjectArrs(env, cimage, jimageData);

    if (cface) {
        deleteFace(cface);
    }

    return status;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_alibaba_cloud_faceengine_FaceVerifyJNI_verifyPicture(
        JNIEnv *env, jobject obj,
        jlong context, jobject image1, jobject image1Face, jobject image2,
        jobjectArray image2Faces) {
    FaceVerify *ins = (FaceVerify *) context;
    if (!ins) {
        return 0;
    }

    if (!image1Face) {
        return 0;
    }

    if (!image2Faces) {
        return 0;
    }

    Image cimage1;
    jobject jimageData1 = 0;
    int error = JImage.toCObject(env, image1, cimage1, jimageData1);
    if (error < 0) {
        return 0;
    }

    Image cimage2;
    jobject jimageData2 = 0;
    error = JImage.toCObject(env, image2, cimage2, jimageData2);
    if (error < 0) {
        JImage.releaseJObjectArrs(env, cimage1, jimageData1);
        return 0;
    }

    Face *cimage1Face = JFace.toCObject(env, image1Face);

    int image2FacesNum = env->GetArrayLength(image2Faces);
    Face *cimage2Faces = new Face[image2FacesNum];

    JFaceArrayToCFaceArray(env, image2Faces, cimage2Faces);

    list<Face> cimage2FaceList;
    for (int i = 0; i < image2FacesNum; i++) {
        cimage2FaceList.push_back(cimage2Faces[i]);
    }

    list<VerifyResult> cVerifyResults;
    int status = ins->verifyPicture(cimage1, *cimage1Face, cimage2, cimage2FaceList,
                                    cVerifyResults);

    JImage.releaseJObjectArrs(env, cimage1, jimageData1);
    JImage.releaseJObjectArrs(env, cimage2, jimageData2);

    jobjectArray jVerifyResults = CVerifyResultArrayToJVerifyResultArray(env, cVerifyResults);

    if (cimage1Face) {
        deleteFace(cimage1Face);
    }
    delete[] cimage2Faces;

    return jVerifyResults;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_cloud_faceengine_ToolsJNI_drawFaceRect(
        JNIEnv *env, jobject obj,
        jobject image, jobject face, jint color) {
    Image cimage;
    jobject jimageData = 0;
    int error = JImage.toCObject(env, image, cimage, jimageData);
    if (error < 0) {
        return 0;
    }

    Face *cface = JFace.toCObject(env, face);
    if (cface) {
        ali_face_engine::Tools::drawFaceRect(&cimage, cface, color);
        deleteFace(cface);
    }
    JImage.releaseJObjectArrs(env, cimage, jimageData);

    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_CodecJNI_nv21Rotate90InPlace(
        JNIEnv *env, jobject obj,
        jbyteArray src, jint width, jint height) {
    unsigned char *csrc = (unsigned char *) (env->GetByteArrayElements(src, 0));
    Codec::nv21Rotate90InPlace(csrc, width, height);
    env->ReleaseByteArrayElements((jbyteArray) src, (jbyte *) csrc, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_CodecJNI_nv21Rotate180InPlace(
        JNIEnv *env, jobject obj,
        jbyteArray src, jint width, jint height) {
    unsigned char *csrc = (unsigned char *) (env->GetByteArrayElements(src, 0));
    Codec::nv21Rotate180InPlace(csrc, width, height);
    env->ReleaseByteArrayElements((jbyteArray) src, (jbyte *) csrc, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_cloud_faceengine_CodecJNI_nv21Rotate270InPlace(
        JNIEnv *env, jobject obj,
        jbyteArray src, jint width, jint height) {
    unsigned char *csrc = (unsigned char *) (env->GetByteArrayElements(src, 0));
    Codec::nv21Rotate270InPlace(csrc, width, height);
    env->ReleaseByteArrayElements((jbyteArray) src, (jbyte *) csrc, 0);
}