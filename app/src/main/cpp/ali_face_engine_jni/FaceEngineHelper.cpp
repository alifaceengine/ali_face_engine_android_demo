#include <string>
#include <stdlib.h>
#include <list>

#include "FaceEngineHelper.h"

#define TAG "AliFaceEngineJNI_FaceEngineHelper"

JMode_t JMode;
JImage_t JImage;
JImageFormat_t JImageFormat;
JImageRotation_t JImageRotation;
JFace_t JFace;
JRect_t JRect;
JPose_t JPose;
JAttribute_t JAttribute;
JQuality_t JQuality;
JLiveness_t JLiveness;
JFacePoint_t JFacePoint;
JModelType_t JModelType;
JGroupInfo_t JGroupInfo;
JPerson_t JPerson;
JFeature_t JFeature;
JDetectParameter_t JDetectParameter;
JRecognizeResult_t JRecognizeResult;
JVerifyResult_t JVerifyResult;
JFaceVerify_VerifyVideoListener_t JFaceVerify_VerifyVideoListener;
JFaceRecognize_RecognizeVideoListener_t JFaceRecognize_RecognizeVideoListener;

JavaVM *gVM = 0;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI(TAG, "JNI_OnLoad");
    gVM = vm;
    JNIEnv *env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    JMode.reflect(env);
    JImageFormat.reflect(env);
    JImageRotation.reflect(env);
    JImage.reflect(env);
    JFace.reflect(env);
    JRect.reflect(env);
    JPose.reflect(env);
    JFacePoint.reflect(env);
    JAttribute.reflect(env);
    JQuality.reflect(env);
    JLiveness.reflect(env);
    JModelType.reflect(env);
    JGroupInfo.reflect(env);
    JPerson.reflect(env);
    JFeature.reflect(env);
    JDetectParameter.reflect(env);
    JRecognizeResult.reflect(env);
    JVerifyResult.reflect(env);
    JFaceRecognize_RecognizeVideoListener.reflect(env);
    JFaceVerify_VerifyVideoListener.reflect(env);
    LOGI(TAG, "JNI_OnLoad OK");
    return JNI_VERSION_1_4;
}

jobjectArray CFaceArrayToJFaceArray(JNIEnv *env, Face *cfaceArray, int cfaceNum) {
    if (cfaceNum <= 0) {
        return 0;
    }

    jobjectArray jfaceArray = (jobjectArray) env->NewObjectArray(cfaceNum, JFace.jClass, NULL);
    if (jfaceArray == NULL) {
        LOGE(TAG, "CFaceArrayToJFaceArray NewObjectArrayv errro");
        return 0;
    }

    for (int i = 0; i < cfaceNum; i++) {
        jobject jface = JFace.toJObject(env, cfaceArray[i]);
        if (!jface) {
            env->DeleteLocalRef(jfaceArray);
            return 0;
        }
        env->SetObjectArrayElement(jfaceArray, i, jface);
        env->DeleteLocalRef(jface);
    }

    return jfaceArray;
}

jobjectArray CFaceListToJFaceArray(JNIEnv *env, list<Face> &cfaceList) {
    if (cfaceList.size() == 0) {
        return 0;
    }

    jobjectArray jfaceArray = (jobjectArray) env->NewObjectArray(cfaceList.size(), JFace.jClass,
                                                                 NULL);
    CFaceListToJFaceArray(env, cfaceList, jfaceArray);
    return jfaceArray;
}

void CFaceListToJFaceArray(JNIEnv *env, list<Face> &cfaceList, jobjectArray &jfaceList) {
    if (cfaceList.size() == 0) {
        return;
    }

    if (jfaceList == NULL) {
        LOGE(TAG, "CFaceArrayToJFaceArray NewObjectArrayv errro");
        return;
    }

    int i = 0;
    for (list<Face>::iterator it = cfaceList.begin(); it != cfaceList.end(); ++it) {
        jobject jface = JFace.toJObject(env, *it);
        if (!jface) {
            env->DeleteLocalRef(jfaceList);
            return;
        }
        env->SetObjectArrayElement(jfaceList, i, jface);
        env->DeleteLocalRef(jface);
        i++;
    }
}

int JFaceArrayToCFaceArray(JNIEnv *env, jobjectArray jfaceArray, Face *cfaceArray) {
    if (!jfaceArray) {
        return -1;
    }

    int arrayNum = env->GetArrayLength(jfaceArray);
    for (int i = 0; i < arrayNum; i++) {
        JFace.toCObject(env, env->GetObjectArrayElement(jfaceArray, i), cfaceArray[i]);
    }
    return 0;
}

int JFaceArrayToCFaceList(JNIEnv *env, jobjectArray jfaceArray, list<Face> &cfaceList) {
    if (!jfaceArray) {
        return -1;
    }

    int arrayNum = env->GetArrayLength(jfaceArray);
    for (int i = 0; i < arrayNum; i++) {
        Face face;
        JFace.toCObject(env, env->GetObjectArrayElement(jfaceArray, i), face);
        cfaceList.push_back(face);
    }
    return 0;
}

int JFacePointArrayToCFacePointArray(JNIEnv *env, jobjectArray jfacePointArray,
                                     FacePoint *cfacePointArray) {
    if (!jfacePointArray) {
        return -1;
    }

    int arrayNum = env->GetArrayLength(jfacePointArray);
    for (int i = 0; i < arrayNum; i++) {
        JFacePoint.toCObject(env, env->GetObjectArrayElement(jfacePointArray, i),
                             cfacePointArray[i]);
    }
    return 0;
}

jobjectArray
CGroupInfoArrayToJGroupInfoArray(JNIEnv *env, list<Group> groupList) {
    if (groupList.size() == 0) {
        return 0;
    }

    jobjectArray jGroupInfoArray = (jobjectArray) env->NewObjectArray(groupList.size(),
                                                                      JGroupInfo.jClass, NULL);
    if (jGroupInfoArray == NULL) {
        LOGE(TAG, "NewObjectArray jGroupInfoArray error");
        return 0;
    }

    int i = 0;
    for (list<Group>::iterator it = groupList.begin(); it != groupList.end(); ++it) {
        jobject jGroupInfo = JGroupInfo.toJObject(env, *it);
        if (!jGroupInfo) {
            env->DeleteLocalRef(jGroupInfoArray);
            return 0;
        }
        env->SetObjectArrayElement(jGroupInfoArray, i, jGroupInfo);
        env->DeleteLocalRef(jGroupInfo);
        i++;
    }

    return jGroupInfoArray;
}

jobjectArray CPersonArrayToJPersonArray(JNIEnv *env, list<Person> personList) {
    if (personList.size() == 0) {
        return 0;
    }

    jobjectArray jarray = (jobjectArray) env->NewObjectArray(personList.size(), JPerson.jClass,
                                                             NULL);
    if (jarray == NULL) {
        LOGE(TAG, "CPersonArrayToJPersonArray NewObjectArray error");
        return 0;
    }

    int i = 0;
    for (list<Person>::iterator it = personList.begin(); it != personList.end(); ++it) {
        jobject object = JPerson.toJObject(env, *it);
        if (!object) {
            env->DeleteLocalRef(jarray);
            return 0;
        }
        env->SetObjectArrayElement(jarray, i, object);
        env->DeleteLocalRef(object);
        i++;
    }

    return jarray;
}

jobject CFeatureToJFeature(JNIEnv *env, Feature *cfeature) {
    if (!cfeature) {
        return 0;
    }

    jobject jfeature = 0;
    jfeature = env->NewObject(JFeature.jClass, JFeature.m_constructor.jMethod);

    jstring jid = env->NewStringUTF(cfeature->id.c_str());
    env->SetObjectField(jfeature, JFeature.f_id.jField, jid);
    env->DeleteLocalRef(jid);

    jstring jfeature_str = env->NewStringUTF(cfeature->feature.c_str());
    env->SetObjectField(jfeature, JFeature.f_feature.jField, jfeature_str);
    env->DeleteLocalRef(jfeature_str);

    return jfeature;
}

jobjectArray CFeatureArrayToJFeatureArray(JNIEnv *env, list<Feature> &cfeatureArray) {
    if (cfeatureArray.size() == 0) {
        return 0;
    }

    jobjectArray jarray = (jobjectArray) env->NewObjectArray(cfeatureArray.size(), JFeature.jClass,
                                                             NULL);
    if (jarray == NULL) {
        LOGE(TAG, "CFeatureArrayToJFeatureArray NewObjectArray error");
        return 0;
    }

    int i = 0;
    for (list<Feature>::iterator it = cfeatureArray.begin(); it != cfeatureArray.end(); ++it) {
        jobject object = CFeatureToJFeature(env, &(*it));
        if (!object) {
            env->DeleteLocalRef(jarray);
            return 0;
        }
        env->SetObjectArrayElement(jarray, i, object);
        env->DeleteLocalRef(object);
        i++;
    }

    return jarray;
}

int JDetectParameterToCDetectParameter(JNIEnv *env, jobject jdetectParameter,
                                       DetectParameter *cdetectParameter) {
    if (!jdetectParameter || !cdetectParameter) {
        return -1;
    }

    cdetectParameter->checkQuality = env->GetIntField(jdetectParameter,
                                                      JDetectParameter.f_checkQuality.jField);
    cdetectParameter->checkLiveness = env->GetIntField(jdetectParameter,
                                                       JDetectParameter.f_checkLiveness.jField);
    cdetectParameter->checkAge = env->GetIntField(jdetectParameter,
                                                  JDetectParameter.f_checkAge.jField);
    cdetectParameter->checkGender = env->GetIntField(jdetectParameter,
                                                     JDetectParameter.f_checkGender.jField);
    cdetectParameter->checkExpression = env->GetIntField(jdetectParameter,
                                                         JDetectParameter.f_checkExpression.jField);
    cdetectParameter->checkGlass = env->GetIntField(jdetectParameter,
                                                    JDetectParameter.f_checkGlass.jField);
    jobject jroi = env->GetObjectField(jdetectParameter, JDetectParameter.f_roi.jField);
    JRect.toCObject(env, jroi, cdetectParameter->roi);

    return 0;
}

jobject CDetectParameterToJDetectParameter(JNIEnv *env, DetectParameter *cDetectParameter) {
    if (!cDetectParameter) {
        return 0;
    }

    jobject jDetectParameter = 0;
    jDetectParameter = env->NewObject(JDetectParameter.jClass,
                                      JDetectParameter.m_constructor.jMethod);
    env->SetIntField(jDetectParameter, JDetectParameter.f_checkQuality.jField,
                     cDetectParameter->checkQuality);
    env->SetIntField(jDetectParameter, JDetectParameter.f_checkLiveness.jField,
                     cDetectParameter->checkLiveness);
    env->SetIntField(jDetectParameter, JDetectParameter.f_checkAge.jField,
                     cDetectParameter->checkAge);
    env->SetIntField(jDetectParameter, JDetectParameter.f_checkGender.jField,
                     cDetectParameter->checkGender);
    env->SetIntField(jDetectParameter, JDetectParameter.f_checkExpression.jField,
                     cDetectParameter->checkExpression);
    env->SetIntField(jDetectParameter, JDetectParameter.f_checkGlass.jField,
                     cDetectParameter->checkGlass);

    jobject roi = env->NewObject(JRect.jClass, JRect.m_constructor.jMethod);
    env->SetIntField(roi, JRect.f_left.jField, cDetectParameter->roi.left);
    env->SetIntField(roi, JRect.f_right.jField, cDetectParameter->roi.right);
    env->SetIntField(roi, JRect.f_top.jField, cDetectParameter->roi.top);
    env->SetIntField(roi, JRect.f_bottom.jField, cDetectParameter->roi.bottom);
    env->SetObjectField(jDetectParameter, JDetectParameter.f_roi.jField, roi);
    env->DeleteLocalRef(roi);

    return jDetectParameter;
}

jobject CRecognizeResultToJRecognizeResult(JNIEnv *env, RecognizeResult *cRecognizeResult) {
    if (!cRecognizeResult) {
        return 0;
    }

    jobject jRecognizeResult = 0;
    jRecognizeResult = env->NewObject(JRecognizeResult.jClass,
                                      JRecognizeResult.m_constructor.jMethod);
    env->SetIntField(jRecognizeResult, JRecognizeResult.f_trackId.jField,
                     cRecognizeResult->trackId);
    jstring jpersonId = env->NewStringUTF(cRecognizeResult->personId.c_str());
    env->SetObjectField(jRecognizeResult, JRecognizeResult.f_personId.jField, jpersonId);
    jstring jpersonName = env->NewStringUTF(cRecognizeResult->personName.c_str());
    env->SetObjectField(jRecognizeResult, JRecognizeResult.f_personName.jField, jpersonName);
    env->SetFloatField(jRecognizeResult, JRecognizeResult.f_similarity.jField,
                       cRecognizeResult->similarity);

    env->DeleteLocalRef(jpersonId);
    env->DeleteLocalRef(jpersonName);
    return jRecognizeResult;
}

jobjectArray
CRecognizeResultArrayToJRecognizeResultArray(JNIEnv *env,
                                             list<RecognizeResult> cRecognizeResultArray) {
    if (cRecognizeResultArray.size() == 0) {
        return 0;
    }

    jobjectArray jarray = (jobjectArray) env->NewObjectArray(cRecognizeResultArray.size(),
                                                             JRecognizeResult.jClass, NULL);
    if (jarray == NULL) {
        LOGE(TAG, "CRecognizeResultArrayToJRecognizeResultArray NewObjectArray error");
        return 0;
    }

    int i = 0;
    for (list<RecognizeResult>::iterator it = cRecognizeResultArray.begin();
         it != cRecognizeResultArray.end(); ++it) {
        jobject object = CRecognizeResultToJRecognizeResult(env, &(*it));
        if (!object) {
            env->DeleteLocalRef(jarray);
            return 0;
        }
        env->SetObjectArrayElement(jarray, i, object);
        env->DeleteLocalRef(object);
        i++;
    }

    return jarray;
}

jobject CVerifyResultToJVerifyResult(JNIEnv *env, VerifyResult *cVerifyResult) {
    if (!cVerifyResult) {
        return 0;
    }

    jobject jVerifyResult = 0;
    jVerifyResult = env->NewObject(JVerifyResult.jClass, JVerifyResult.m_constructor.jMethod);
    env->SetIntField(jVerifyResult, JVerifyResult.f_trackId.jField, cVerifyResult->trackId);
    env->SetFloatField(jVerifyResult, JVerifyResult.f_similarity.jField, cVerifyResult->similarity);

    return jVerifyResult;
}

jobjectArray
CVerifyResultArrayToJVerifyResultArray(JNIEnv *env, list<VerifyResult> &resultList) {
    if (resultList.size() == 0) {
        return 0;
    }

    jobjectArray jarray = (jobjectArray) env->NewObjectArray(resultList.size(),
                                                             JVerifyResult.jClass, NULL);
    if (jarray == NULL) {
        LOGE(TAG, "CVerifyResultArrayToJVerifyResultArray NewObjectArray error");
        return 0;
    }

    int i = 0;
    for (list<VerifyResult>::iterator it = resultList.begin(); it != resultList.end(); ++it) {
        jobject object = CVerifyResultToJVerifyResult(env, &(*it));
        if (!object) {
            env->DeleteLocalRef(jarray);
            return 0;
        }
        env->SetObjectArrayElement(jarray, i, object);
        env->DeleteLocalRef(object);
        i++;
    }

    return jarray;
}

GET_JNIENV_STATUS getJNIEnv(JNIEnv **env) {
    GET_JNIENV_STATUS GetStatus = GET_FAIL;
    int status = gVM->GetEnv((void **) env, JNI_VERSION_1_4);

    if (status < 0) {
#ifdef __ANDROID__
        status = gVM->AttachCurrentThread(env, NULL);
#else
        status = gVM->AttachCurrentThread((void **) env, NULL);
#endif
        if (status < 0) {
            LOGE(TAG, "callback_handler: failed to attach current thread");
            return GetStatus;
        }
        GetStatus = GET_SUCCES_ATTCH;
    } else {
        GetStatus = GET_SUCCES_NOATTACH;
    }
    return GetStatus;
}

int detachCurrentThread() {
    jint result;
    result = gVM->DetachCurrentThread();
    if (result != JNI_OK) {
        LOGE(TAG, "ERROR: thread detach failed\n");
    }

    return result;
}
