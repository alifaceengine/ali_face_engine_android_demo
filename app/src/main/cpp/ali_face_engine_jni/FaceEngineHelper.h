#pragma once

#include <list>
#include <jni.h>

#include "FaceEngine.h"

#define TAG "AliFaceEngineJNI_FaceEngineHelper"

#ifdef __ANDROID__

#include <android/log.h>

#define LOGD(TAG, fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args);
#define LOGI(TAG, fmt, args...) __android_log_print(ANDROID_LOG_INFO, TAG, fmt, ##args);
#define LOGE(TAG, fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args);
#elif defined(_WIN32)
#define LOGD(TAG, fmt, args)
#define LOGI(TAG, fmt, args)
#define LOGE(TAG, fmt, args)
#else
#define LOGD(TAG, fmt, args...)
#define LOGI(TAG, fmt, args...)
#define LOGE(TAG, fmt, args...)
#endif

using namespace ali_face_engine;

static const bool IsStatic = true;
static const bool IsNonStatic = false;

extern JavaVM *gVM;

typedef enum GET_JNIENV_STATUS_t {
    GET_FAIL = 0,
    GET_SUCCES_NOATTACH,
    GET_SUCCES_ATTCH,
} GET_JNIENV_STATUS;

GET_JNIENV_STATUS getJNIEnv(JNIEnv **env);

int detachCurrentThread();

int JFacePointArrayToCFacePointArray(JNIEnv *env, jobjectArray jfacePointArray,
                                     FacePoint *cfacePointArray);

jobjectArray CFeatureArrayToJFeatureArray(JNIEnv *env, list<Feature> &cfeatureArray);

class JBaseClass_t;

struct JField_t {
    const char *name;
    const char *type;
    bool isStatic;
    jfieldID jField;
    JBaseClass_t *jOwnerClass;

    //static value
    union {
        jobject objectValue;
        jint intValue;
        jfloat floatValue;
        jboolean booleanValue;
    };
};

struct JMethod_t {
    const char *name;
    const char *type;
    bool isStatic;
    jmethodID jMethod;
    JBaseClass_t *jOwnerClass;
};

class JBaseClass_t {
public:
    char *jClassName;
    jclass jClass;

public:
    JBaseClass_t() :
            jClassName(0),
            jClass(0) {

    }

    void reflect(JNIEnv *env) {
        jclass clazz;
        findClassOrDie(env, jClassName, &clazz);
        jClass = (jclass) env->NewGlobalRef(clazz);

        for (std::list<JField_t *>::iterator it = mJFieldList.begin();
             it != mJFieldList.end(); ++it) {
            (*it)->jOwnerClass = this;
            findFiledOrDie(env, *it);
        }
        mJFieldList.clear();

        for (std::list<JMethod_t *>::iterator it = mJMethodList.begin();
             it != mJMethodList.end(); ++it) {
            (*it)->jOwnerClass = this;
            findMethodOrDie(env, *it);
        }
        mJMethodList.clear();
    }

protected:
    std::list<JField_t *> mJFieldList;
    std::list<JMethod_t *> mJMethodList;

private:
    static inline void findClassOrDie(JNIEnv *env, char *jClassName, jclass *jClass) {
        jclass clazz = env->FindClass(jClassName);
        if (clazz == NULL) {
            LOGE(TAG, "FindClass %s error", jClassName);
            abort();
        }
        *jClass = clazz;
    }

    static inline void findFiledOrDie(JNIEnv *env, JField_t *field) {
        if (field->isStatic) {
            field->jField = env->GetStaticFieldID(field->jOwnerClass->jClass, field->name,
                                                  field->type);

            if (strcmp("I", field->type) == 0) {
                field->intValue = env->GetStaticIntField(field->jOwnerClass->jClass, field->jField);
            } else if (strcmp("F", field->type) == 0) {
                field->floatValue = env->GetStaticFloatField(field->jOwnerClass->jClass,
                                                             field->jField);
            } else {
                field->objectValue = env->GetStaticObjectField(field->jOwnerClass->jClass,
                                                               field->jField);
            }

        } else {
            field->jField = env->GetFieldID(field->jOwnerClass->jClass, field->name, field->type);
        }

        if (!field->jField) {
            LOGE(TAG, "findFiled %s error", field->name);
            abort();
        }
    }

    static inline void findMethodOrDie(JNIEnv *env, JMethod_t *method) {
        if (method->isStatic) {
            method->jMethod = env->GetStaticMethodID(method->jOwnerClass->jClass, method->name,
                                                     method->type);
        } else {
            method->jMethod = env->GetMethodID(method->jOwnerClass->jClass, method->name,
                                               method->type);
        }
        if (method->jMethod == NULL) {
            LOGE(TAG, "findFiled %s error", method->name);
            abort();
        }
    }
};

template<class T>
class JClass_t : public JBaseClass_t {
public:
    virtual jobject toJObject(JNIEnv *env, T &cObject) {
        return 0;
    }

    jobject toJObject(JNIEnv *env, T *cObject) {
        if (!cObject) {
            return 0;
        }
        return toJObject(env, *cObject);
    }

    virtual T *toCObject(JNIEnv *env, jobject jObject) {
        return 0;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, T &cObject) {
        return -1;
    }
};

template<class T>
class JClassWithByteArray_t : public JBaseClass_t {
public:
    virtual jobject toJObject(JNIEnv *env, T &cObject) {
        return 0;
    }

    jobject toJObject(JNIEnv *env, T *cObject) {
        if (!cObject) {
            return 0;
        }
        return toJObject(env, *cObject);
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, T &cObject, jobject &arr1) {
        return -1;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, T &cObject, jobject &arr1, jobject &arr2) {
        return -1;
    }

    virtual int releaseJObjectArrs(JNIEnv *env, T &cObject, jobject arr1) {
        return -1;
    }

    virtual int releaseJObjectArrs(JNIEnv *env, T &cObject, jobject arr1, jobject arr2) {
        return -1;
    }
};

class JMode_t : public JClass_t<enum Mode> {
public:
    JField_t f_TERMINAL = {"TERMINAL", "I", IsStatic};
    JField_t f_CLOUD = {"CLOUD", "I", IsStatic};

public:
    JMode_t() {
        jClassName = "com/alibaba/cloud/faceengine/Mode";
        mJFieldList.push_back(&f_TERMINAL);
        mJFieldList.push_back(&f_CLOUD);
    }
};

class JImageFormat_t : public JClass_t<enum ImageFormat> {
public:
    JField_t f_ImageFormat_UNKNOWN = {"ImageFormat_UNKNOWN", "I", IsStatic};
    JField_t f_RGB888 = {"RGB888", "I", IsStatic};
    JField_t f_BGR888 = {"BGR888", "I", IsStatic};
    JField_t f_NV21 = {"NV21", "I", IsStatic};
    JField_t f_JPEG = {"JPEG", "I", IsStatic};
    JField_t f_PNG = {"PNG", "I", IsStatic};
    JField_t f_BMP = {"BMP", "I", IsStatic};

public:
    JImageFormat_t() {
        jClassName = "com/alibaba/cloud/faceengine/ImageFormat";
        mJFieldList.push_back(&f_ImageFormat_UNKNOWN);
        mJFieldList.push_back(&f_RGB888);
        mJFieldList.push_back(&f_BGR888);
        mJFieldList.push_back(&f_NV21);
        mJFieldList.push_back(&f_JPEG);
        mJFieldList.push_back(&f_PNG);
        mJFieldList.push_back(&f_BMP);
    }
};

class JImageRotation_t : public JClass_t<enum ImageRotation> {
public:
    JField_t f_ANGLE_0 = {"ANGLE_0", "I", IsStatic};
    JField_t f_ANGLE_90 = {"ANGLE_90", "I", IsStatic};
    JField_t f_ANGLE_180 = {"ANGLE_180", "I", IsStatic};
    JField_t f_ANGLE_270 = {"ANGLE_270", "I", IsStatic};

public:
    JImageRotation_t() {
        jClassName = "com/alibaba/cloud/faceengine/ImageRotation";
        mJFieldList.push_back(&f_ANGLE_0);
        mJFieldList.push_back(&f_ANGLE_90);
        mJFieldList.push_back(&f_ANGLE_180);
        mJFieldList.push_back(&f_ANGLE_270);
    }
};

class JImage_t : public JClassWithByteArray_t<Image> {
public:
    JField_t f_data = {"data", "[B", IsNonStatic};
    JField_t f_format = {"format", "I", IsNonStatic};
    JField_t f_rotation = {"rotation", "I", IsNonStatic};
    JField_t f_width = {"width", "I", IsNonStatic};
    JField_t f_height = {"height", "I", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JImage_t() {
        jClassName = "com/alibaba/cloud/faceengine/Image";
        mJFieldList.push_back(&f_data);
        mJFieldList.push_back(&f_format);
        mJFieldList.push_back(&f_rotation);
        mJFieldList.push_back(&f_width);
        mJFieldList.push_back(&f_height);
        mJMethodList.push_back(&m_constructor);
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Image &cObject, jobject &arr1) {
        if (!jObject) {
            return -1;
        }

        cObject.data = 0;
        arr1 = (jobject) env->GetObjectField(jObject, f_data.jField);
        if (arr1) {
            cObject.data = (unsigned char *) (env->GetByteArrayElements(
                    (jbyteArray) arr1, 0));
            cObject.dataLen = env->GetArrayLength((jbyteArray) arr1);
        } else {
            cObject.dataLen = 0;
        }

        cObject.format = (ImageFormat) env->GetIntField(jObject, f_format.jField);
        cObject.rotation = (ImageRotation) env->GetIntField(jObject, f_rotation.jField);
        cObject.width = env->GetIntField(jObject, f_width.jField);
        cObject.height = env->GetIntField(jObject, f_height.jField);
        return 0;
    }

    virtual int releaseJObjectArrs(JNIEnv *env, Image &cObject, jobject arr1) {
        if (arr1) {
            env->ReleaseByteArrayElements((jbyteArray) arr1, (jbyte *) cObject.data, 0);
        }
        return 0;
    }
};

class JRect_t : public JClass_t<Rect> {
public:
    JField_t f_left = {"left", "I", IsNonStatic};
    JField_t f_top = {"top", "I", IsNonStatic};
    JField_t f_right = {"right", "I", IsNonStatic};
    JField_t f_bottom = {"bottom", "I", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JRect_t() {
        jClassName = "com/alibaba/cloud/faceengine/Rect";
        mJFieldList.push_back(&f_left);
        mJFieldList.push_back(&f_top);
        mJFieldList.push_back(&f_right);
        mJFieldList.push_back(&f_bottom);
        mJMethodList.push_back(&m_constructor);
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Rect &cObject) {
        if (!jObject) {
            return -1;
        }

        cObject.left = env->GetIntField(jObject, f_left.jField);
        cObject.top = env->GetIntField(jObject, f_top.jField);
        cObject.right = env->GetIntField(jObject, f_right.jField);
        cObject.bottom = env->GetIntField(jObject, f_bottom.jField);
        return 0;
    }
};

class JPose_t : public JClass_t<Pose> {
public:
    JField_t f_pitch = {"pitch", "F", IsNonStatic};
    JField_t f_yaw = {"yaw", "F", IsNonStatic};
    JField_t f_roll = {"roll", "F", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JPose_t() {
        jClassName = "com/alibaba/cloud/faceengine/Pose";
        mJFieldList.push_back(&f_pitch);
        mJFieldList.push_back(&f_yaw);
        mJFieldList.push_back(&f_roll);
        mJMethodList.push_back(&m_constructor);
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Pose &cObject) {
        if (!jObject) {
            return -1;
        }

        cObject.pitch = env->GetFloatField(jObject, f_pitch.jField);
        cObject.yaw = env->GetFloatField(jObject, f_yaw.jField);
        cObject.roll = env->GetFloatField(jObject, f_roll.jField);
        return 0;
    }
};

class JFacePoint_t : public JClass_t<FacePoint> {
public:
    JField_t f_x = {"x", "F", IsNonStatic};
    JField_t f_y = {"y", "F", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JFacePoint_t() {
        jClassName = "com/alibaba/cloud/faceengine/FacePoint";
        mJFieldList.push_back(&f_x);
        mJFieldList.push_back(&f_y);
        mJMethodList.push_back(&m_constructor);
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, FacePoint &cObject) {
        if (!jObject) {
            return -1;
        }

        cObject.x = env->GetFloatField(jObject, f_x.jField);
        cObject.y = env->GetFloatField(jObject, f_y.jField);
        return 0;
    }
};

extern JRect_t JRect;
extern JPose_t JPose;
extern JFacePoint_t JFacePoint;

class JQuality_t : public JClass_t<Quality> {
public:
    JField_t f_score = {"score", "I", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JQuality_t() {
        jClassName = "com/alibaba/cloud/faceengine/Quality";
        mJFieldList.push_back(&f_score);
        mJMethodList.push_back(&m_constructor);
    }

    virtual jobject toJObject(JNIEnv *env, Quality &cObject) {
        jobject jObject = env->NewObject(jClass, m_constructor.jMethod);
        env->SetIntField(jObject, f_score.jField, cObject.score);
        return jObject;
    }

    virtual Quality *toCObject(JNIEnv *env, jobject jObject) {
        if (!jObject) {
            return 0;
        }

        return 0;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Quality &cObject) {
        if (!jObject) {
            return -1;
        }

        cObject.score = env->GetIntField(jObject, f_score.jField);
        return 0;
    }
};

class JLiveness_t : public JClass_t<Liveness> {
public:
    JField_t f_score = {"score", "I", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JLiveness_t() {
        jClassName = "com/alibaba/cloud/faceengine/Liveness";
        mJFieldList.push_back(&f_score);
        mJMethodList.push_back(&m_constructor);
    }

    virtual jobject toJObject(JNIEnv *env, Liveness &cObject) {
        jobject jObject = env->NewObject(jClass, m_constructor.jMethod);
        env->SetIntField(jObject, f_score.jField, cObject.score);
        return jObject;
    }

    virtual Liveness *toCObject(JNIEnv *env, jobject jObject) {
        if (!jObject) {
            return 0;
        }

        return 0;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Liveness &cObject) {
        if (!jObject) {
            return -1;
        }

        cObject.score = env->GetIntField(jObject, f_score.jField);
        return 0;
    }
};

extern JQuality_t JQuality;
extern JLiveness_t JLiveness;

class JAttribute_t : public JClass_t<Attribute> {
public:
    JField_t f_quality = {"quality", "Lcom/alibaba/cloud/faceengine/Quality;", IsNonStatic};
    JField_t f_liveness = {"liveness", "Lcom/alibaba/cloud/faceengine/Liveness;", IsNonStatic};
    JField_t f_age = {"age", "I", IsNonStatic};
    JField_t f_gender = {"gender", "I", IsNonStatic};
    JField_t f_expression = {"expression", "I", IsNonStatic};
    JField_t f_glass = {"glass", "I", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JAttribute_t() {
        jClassName = "com/alibaba/cloud/faceengine/Attribute";
        mJFieldList.push_back(&f_quality);
        mJFieldList.push_back(&f_liveness);
        mJFieldList.push_back(&f_age);
        mJFieldList.push_back(&f_gender);
        mJFieldList.push_back(&f_expression);
        mJFieldList.push_back(&f_glass);
        mJMethodList.push_back(&m_constructor);
    }

    virtual jobject toJObject(JNIEnv *env, Attribute &cObject) {
        jobject jObj = env->NewObject(jClass, m_constructor.jMethod);

        jobject jquality = JQuality.toJObject(env, cObject.quality);
        env->SetObjectField(jObj, f_quality.jField, jquality);

        jobject jliveness = JLiveness.toJObject(env, cObject.liveness);
        env->SetObjectField(jObj, f_liveness.jField, jliveness);

        env->SetIntField(jObj, f_age.jField, cObject.age);
        env->SetIntField(jObj, f_gender.jField, cObject.gender);
        env->SetIntField(jObj, f_expression.jField, cObject.expression);
        env->SetIntField(jObj, f_glass.jField, cObject.glass);
        return jObj;
    }

    virtual Attribute *toCObject(JNIEnv *env, jobject jObject) {
        if (!jObject) {
            return 0;
        }

        return 0;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Attribute &cObject) {
        if (!jObject) {
            return -1;
        }

        jobject jquality = env->GetObjectField(jObject, f_quality.jField);
        JQuality.toCObject(env, jquality, cObject.quality);
        jobject jliveness = env->GetObjectField(jObject, f_liveness.jField);
        JLiveness.toCObject(env, jliveness, cObject.liveness);

        cObject.age = env->GetIntField(jObject, f_age.jField);
        cObject.gender = (enum Gender) env->GetIntField(jObject, f_gender.jField);
        cObject.expression = (enum Expression) env->GetIntField(jObject, f_expression.jField);
        cObject.glass = (enum Glass) env->GetIntField(jObject, f_glass.jField);
        return 0;
    }
};

extern JAttribute_t JAttribute;

class JFace_t : public JClass_t<Face> {
public:
    JField_t f_trackId = {"trackId", "I", IsNonStatic};
    JField_t f_rect = {"rect", "Lcom/alibaba/cloud/faceengine/Rect;", IsNonStatic};
    JField_t f_pose = {"pose", "Lcom/alibaba/cloud/faceengine/Pose;", IsNonStatic};
    JField_t f_facePoints = {"facePoints", "[Lcom/alibaba/cloud/faceengine/FacePoint;",
                             IsNonStatic};
    JField_t f_attribute = {"attribute", "Lcom/alibaba/cloud/faceengine/Attribute;", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JFace_t() {
        jClassName = "com/alibaba/cloud/faceengine/Face";
        mJFieldList.push_back(&f_trackId);
        mJFieldList.push_back(&f_rect);
        mJFieldList.push_back(&f_pose);
        mJFieldList.push_back(&f_facePoints);
        mJFieldList.push_back(&f_attribute);
        mJMethodList.push_back(&m_constructor);
    }

    virtual void toJObject(JNIEnv *env, Face &cObject, jobject jObject) {
        if (!jObject) {
            return;
        }

        jobject jface = jObject;
        env->SetIntField(jface, f_trackId.jField, cObject.trackId);

        jobject rect = env->NewObject(JRect.jClass, JRect.m_constructor.jMethod);
        env->SetIntField(rect, JRect.f_left.jField, cObject.rect.left);
        env->SetIntField(rect, JRect.f_right.jField, cObject.rect.right);
        env->SetIntField(rect, JRect.f_top.jField, cObject.rect.top);
        env->SetIntField(rect, JRect.f_bottom.jField, cObject.rect.bottom);
        env->SetObjectField(jface, f_rect.jField, rect);
        env->DeleteLocalRef(rect);

        jobject pose = env->NewObject(JPose.jClass, JPose.m_constructor.jMethod);
        env->SetFloatField(pose, JPose.f_pitch.jField, cObject.pose.pitch);
        env->SetFloatField(pose, JPose.f_roll.jField, cObject.pose.roll);
        env->SetFloatField(pose, JPose.f_yaw.jField, cObject.pose.yaw);
        env->SetObjectField(jface, f_pose.jField, pose);
        env->DeleteLocalRef(pose);

        jobjectArray jfacePointsArray = (jobjectArray) env->NewObjectArray(FACE_POINT_NUM,
                                                                           JFacePoint.jClass,
                                                                           NULL);
        for (int i = 0; i < FACE_POINT_NUM; i++) {
            jobject jfacePoint = env->NewObject(JFacePoint.jClass,
                                                JFacePoint.m_constructor.jMethod);
            env->SetFloatField(jfacePoint, JFacePoint.f_x.jField, cObject.facePoints[i].x);
            env->SetFloatField(jfacePoint, JFacePoint.f_y.jField, cObject.facePoints[i].y);
            if (!jfacePoint) {
                env->DeleteLocalRef(jfacePointsArray);
            }
            env->SetObjectArrayElement(jfacePointsArray, i, jfacePoint);
            env->DeleteLocalRef(jfacePoint);
        }
        env->SetObjectField(jface, f_facePoints.jField, jfacePointsArray);
        env->DeleteLocalRef(jfacePointsArray);

        env->SetObjectField(jface, f_attribute.jField,
                            JAttribute.toJObject(env, cObject.attribute));
    }

    virtual jobject toJObject(JNIEnv *env, Face &cObject) {
        jobject jface = env->NewObject(jClass, m_constructor.jMethod);
        toJObject(env, cObject, jface);
        return jface;
    }

    virtual Face *toCObject(JNIEnv *env, jobject jObject) {
        if (!jObject) {
            return 0;
        }

        Face *cObject = newFace();
        int error = toCObject(env, jObject, *cObject);
        if (error < 0) {
            deleteFace(cObject);
            return 0;
        }
        return cObject;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Face &cObject) {
        if (!jObject) {
            return -1;
        }

        cObject.trackId = env->GetIntField(jObject, f_trackId.jField);
        jobject jrect = env->GetObjectField(jObject, f_rect.jField);
        JRect.toCObject(env, jrect, cObject.rect);
        jobject jpose = env->GetObjectField(jObject, f_pose.jField);
        JPose.toCObject(env, jpose, cObject.pose);
        jobject jfacePoints = env->GetObjectField(jObject, f_facePoints.jField);
        JFacePointArrayToCFacePointArray(env, (jobjectArray) jfacePoints, cObject.facePoints);
        return 0;
    }
};

class JModelType_t : public JClass_t<enum ModelType> {
public:
    JField_t f_MODEL_SMALL = {"MODEL_SMALL", "I", IsStatic};
    JField_t f_MODEL_BIG = {"MODEL_BIG", "I", IsStatic};

public:
    JModelType_t() {
        jClassName = "com/alibaba/cloud/faceengine/ModelType";
        mJFieldList.push_back(&f_MODEL_SMALL);
        mJFieldList.push_back(&f_MODEL_BIG);
    }
};

class JGroupInfo_t : public JClass_t<Group> {
public:
    JField_t f_id = {"id", "Ljava/lang/String;", IsNonStatic};
    JField_t f_name = {"name", "Ljava/lang/String;", IsNonStatic};
    JField_t f_modelType = {"modelType", "I", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JGroupInfo_t() {
        jClassName = "com/alibaba/cloud/faceengine/Group";
        mJFieldList.push_back(&f_id);
        mJFieldList.push_back(&f_name);
        mJFieldList.push_back(&f_modelType);
        mJMethodList.push_back(&m_constructor);
    }

    virtual jobject toJObject(JNIEnv *env, Group &cObject) {
        jobject jGroupInfo = 0;
        jGroupInfo = env->NewObject(jClass, m_constructor.jMethod);
        jstring jid = env->NewStringUTF(cObject.id.c_str());
        jstring jname = env->NewStringUTF(cObject.name.c_str());

        env->SetObjectField(jGroupInfo, f_id.jField, jid);
        env->SetObjectField(jGroupInfo, f_name.jField, jname);
        env->SetIntField(jGroupInfo, f_modelType.jField, cObject.modelType);

        env->DeleteLocalRef(jid);
        env->DeleteLocalRef(jname);
        return jGroupInfo;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Group &cObject) {
        if (!jObject) {
            return -1;
        }

        jstring jid = (jstring) env->GetObjectField(jObject, f_id.jField);
        const char *cid = 0;
        if (jid) {
            cid = env->GetStringUTFChars(jid, 0);
        }

        jstring jname = (jstring) env->GetObjectField(jObject, f_name.jField);
        const char *cname = 0;
        if (jname) {
            cname = env->GetStringUTFChars(jname, 0);
        }

        if (cid) {
            cObject.id = cid;
        }
        if (cname) {
            cObject.name = cname;
        }
        cObject.modelType = (ModelType) env->GetIntField(jObject, f_modelType.jField);

        if (jid) {
            env->ReleaseStringUTFChars(jid, cid);
        }
        if (jname) {
            env->ReleaseStringUTFChars(jname, cname);
        }
        return 0;
    }
};

class JFeature_t : public JClass_t<Feature> {
public:
    JField_t f_id = {"id", "Ljava/lang/String;", IsNonStatic};
    JField_t f_name = {"name", "Ljava/lang/String;", IsNonStatic};
    JField_t f_feature = {"feature", "Ljava/lang/String;", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};

public:
    JFeature_t() {
        jClassName = "com/alibaba/cloud/faceengine/Feature";
        mJFieldList.push_back(&f_id);
        mJFieldList.push_back(&f_name);
        mJFieldList.push_back(&f_feature);
        mJMethodList.push_back(&m_constructor);
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Feature &cObject) {
        if (!jObject) {
            return -1;
        }

        jstring jid = (jstring) env->GetObjectField(jObject, f_id.jField);
        const char *cid = 0;
        if (jid) {
            cid = env->GetStringUTFChars(jid, 0);
        }
        jstring jname = (jstring) env->GetObjectField(jObject, f_name.jField);
        const char *cname = 0;
        if (jname) {
            cname = env->GetStringUTFChars(jname, 0);
        }
        jstring jfeature = (jstring) env->GetObjectField(jObject, f_feature.jField);
        const char *cfeature = 0;
        if (jfeature) {
            cfeature = env->GetStringUTFChars(jfeature, 0);
        }

        if (cid) {
            cObject.id = cid;
        }
        if (cname) {
            cObject.name = cname;
        }
        if (cfeature) {
            cObject.feature = cfeature;
        }

        if (jid) {
            env->ReleaseStringUTFChars(jid, cid);
        }
        if (jname) {
            env->ReleaseStringUTFChars(jname, cname);
        }
        if (jfeature) {
            env->ReleaseStringUTFChars(jfeature, cfeature);
        }
        return 0;
    }
};

class JPerson_t : public JClass_t<Person> {
public:
    JField_t f_id = {"id", "Ljava/lang/String;", IsNonStatic};
    JField_t f_name = {"name", "Ljava/lang/String;", IsNonStatic};
    JField_t f_features = {"features", "[Lcom/alibaba/cloud/faceengine/Feature;", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};
public:
    JPerson_t() {
        jClassName = "com/alibaba/cloud/faceengine/Person";
        mJFieldList.push_back(&f_id);
        mJFieldList.push_back(&f_name);
        mJFieldList.push_back(&f_features);
        mJMethodList.push_back(&m_constructor);
    }

    virtual jobject toJObject(JNIEnv *env, Person *cObject) {
        if (!cObject) {
            return 0;
        }
        return toJObject(env, *cObject);
    }

    virtual jobject toJObject(JNIEnv *env, Person &cObject) {
        jobject jperson = 0;
        jperson = env->NewObject(jClass, m_constructor.jMethod);

        jstring jid = env->NewStringUTF(cObject.id.c_str());
        env->SetObjectField(jperson, f_id.jField, jid);
        env->DeleteLocalRef(jid);

        jstring jname = env->NewStringUTF(cObject.name.c_str());
        env->SetObjectField(jperson, f_name.jField, jname);
        env->DeleteLocalRef(jname);

        jobjectArray jfeatures = CFeatureArrayToJFeatureArray(env, cObject.features);
        env->SetObjectField(jperson, f_features.jField, jfeatures);
        env->DeleteLocalRef(jfeatures);
        return jperson;
    }

    virtual int toCObject(JNIEnv *env, jobject jObject, Person &cObject) {
        if (!jObject) {
            return -1;
        }

        jstring jid = (jstring) env->GetObjectField(jObject, f_id.jField);
        const char *cid = 0;
        if (jid) {
            cid = env->GetStringUTFChars(jid, 0);
        }

        jstring jname = (jstring) env->GetObjectField(jObject, f_name.jField);
        const char *cname = 0;
        if (jname) {
            cname = env->GetStringUTFChars(jname, 0);
        }

        if (cid) {
            cObject.id = cid;
        }
        if (cname) {
            cObject.name = cname;
        }

        if (jid) {
            env->ReleaseStringUTFChars(jid, cid);
        }
        if (jname) {
            env->ReleaseStringUTFChars(jname, cname);
        }
        return 0;
    }
};

class JDetectParameter_t : public JClass_t<DetectParameter> {
public:
    JField_t f_checkQuality = {"checkQuality", "I", IsNonStatic};
    JField_t f_checkLiveness = {"checkLiveness", "I", IsNonStatic};
    JField_t f_checkAge = {"checkAge", "I", IsNonStatic};
    JField_t f_checkGender = {"checkGender", "I", IsNonStatic};
    JField_t f_checkExpression = {"checkExpression", "I", IsNonStatic};
    JField_t f_checkGlass = {"checkGlass", "I", IsNonStatic};
    JField_t f_roi = {"roi", "Lcom/alibaba/cloud/faceengine/Rect;", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};
public:
    JDetectParameter_t() {
        jClassName = "com/alibaba/cloud/faceengine/DetectParameter";
        mJFieldList.push_back(&f_checkQuality);
        mJFieldList.push_back(&f_checkLiveness);
        mJFieldList.push_back(&f_checkAge);
        mJFieldList.push_back(&f_checkGender);
        mJFieldList.push_back(&f_checkExpression);
        mJFieldList.push_back(&f_checkGlass);
        mJFieldList.push_back(&f_roi);
        mJMethodList.push_back(&m_constructor);
    }
};

class JRecognizeResult_t : public JClass_t<RecognizeResult> {
public:
    JField_t f_trackId = {"trackId", "I", IsNonStatic};
    JField_t f_personName = {"personName", "Ljava/lang/String;", IsNonStatic};
    JField_t f_personId = {"personId", "Ljava/lang/String;", IsNonStatic};
    JField_t f_similarity = {"similarity", "F", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};
public:
    JRecognizeResult_t() {
        jClassName = "com/alibaba/cloud/faceengine/RecognizeResult";
        mJFieldList.push_back(&f_trackId);
        mJFieldList.push_back(&f_personName);
        mJFieldList.push_back(&f_personId);
        mJFieldList.push_back(&f_similarity);
        mJMethodList.push_back(&m_constructor);
    }
};

class JVerifyResult_t : public JClass_t<VerifyResult> {
public:
    JField_t f_trackId = {"trackId", "I", IsNonStatic};
    JField_t f_similarity = {"similarity", "F", IsNonStatic};
    JMethod_t m_constructor = {"<init>", "()V", IsNonStatic};
public:
    JVerifyResult_t() {
        jClassName = "com/alibaba/cloud/faceengine/VerifyResult";
        mJFieldList.push_back(&f_trackId);
        mJFieldList.push_back(&f_similarity);
        mJMethodList.push_back(&m_constructor);
    }
};

class JFaceRecognize_RecognizeVideoListener_t
        : public JClass_t<FaceRecognize::RecognizeVideoListener> {
public:
    JMethod_t m_onRecognized = {"onRecognized",
                                "(Lcom/alibaba/cloud/faceengine/Image;[Lcom/alibaba/cloud/faceengine/RecognizeResult;)V",
                                IsNonStatic};
public:
    JFaceRecognize_RecognizeVideoListener_t() {
        jClassName = "com/alibaba/cloud/faceengine/FaceRecognize$RecognizeVideoListener";
        mJMethodList.push_back(&m_onRecognized);
    }
};

class JFaceVerify_VerifyVideoListener_t : public JClass_t<FaceVerify::VerifyVideoListener> {
public:
    JMethod_t m_onVerified = {"onVerified",
                              "(Lcom/alibaba/cloud/faceengine/Image;[Lcom/alibaba/cloud/faceengine/VerifyResult;)V",
                              IsNonStatic};
public:
    JFaceVerify_VerifyVideoListener_t() {
        jClassName = "com/alibaba/cloud/faceengine/FaceVerify$VerifyVideoListener";

        mJMethodList.push_back(&m_onVerified);
    }
};

extern JRecognizeResult_t JRecognizeResult;
extern JVerifyResult_t JVerifyResult;
extern JFaceVerify_VerifyVideoListener_t JFaceVerify_VerifyVideoListener;
extern JFaceRecognize_RecognizeVideoListener_t JFaceRecognize_RecognizeVideoListener;

int JImageToCImage(JNIEnv *env, jobject jimage, Image *cimage);

jobjectArray CFaceArrayToJFaceArray(JNIEnv *env, Face *cfaceArray, int cfaceNum);

jobjectArray CFaceListToJFaceArray(JNIEnv *env, list<Face> &cfaceList);

void CFaceListToJFaceArray(JNIEnv *env, list<Face> &cfaceList, jobjectArray &jfaceList);

int JFaceToCFace(JNIEnv *env, jobject jface, Face *cface);

int JFaceArrayToCFaceArray(JNIEnv *env, jobjectArray jfaceArray, Face *cfaceArray);

int JFaceArrayToCFaceList(JNIEnv *env, jobjectArray jfaceArray, list<Face> &cfaceList);

jobjectArray
CGroupInfoArrayToJGroupInfoArray(JNIEnv *env, list<Group> groupList);

jobject CPersonToJPerson(JNIEnv *env, Person *cperson);

jobjectArray CPersonArrayToJPersonArray(JNIEnv *env, list<Person> personList);

jobject CFeatureToJFeature(JNIEnv *env, Feature *cfeature);

jobjectArray CFeatureArrayToJFeatureArray(JNIEnv *env, list<Feature> &cfeatureArray);

int JDetectParameterToCDetectParameter(JNIEnv *env, jobject jdetectParameter,
                                       DetectParameter *cdetectParameter);

jobject CDetectParameterToJDetectParameter(JNIEnv *env, DetectParameter *cDetectParameter);

jobject CRecognizeResultToJRecognizeResult(JNIEnv *env, RecognizeResult *cRecognizeResult);

jobjectArray
CRecognizeResultArrayToJRecognizeResultArray(JNIEnv *env,
                                             list<RecognizeResult> recognizeResultList);

jobject CVerifyResultToJVerifyResult(JNIEnv *env, VerifyResult *cVerifyResult);

jobjectArray
CVerifyResultArrayToJVerifyResultArray(JNIEnv *env, list<VerifyResult> &resultList);

extern JMode_t JMode;
extern JImage_t JImage;
extern JImageFormat_t JImageFormat;
extern JImageRotation_t JImageRotation;
extern JFace_t JFace;
extern JModelType_t JModelType;
extern JGroupInfo_t JGroupInfo;
extern JPerson_t JPerson;
extern JFeature_t JFeature;
extern JDetectParameter_t JDetectParameter;
extern JRecognizeResult_t JRecognizeResult;
extern JVerifyResult_t JVerifyResult;
extern JFaceVerify_VerifyVideoListener_t JFaceVerify_VerifyVideoListener;
extern JFaceRecognize_RecognizeVideoListener_t JFaceRecognize_RecognizeVideoListener;