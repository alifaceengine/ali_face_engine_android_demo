#pragma once

/**
 * @file FaceEngine.h
 * @brief [EN] some global functions.
 * @brief [中文] 一些全局的函数。
 * @author [EN] hujunyuan
 * @author [中文] 胡俊远
*/

#include "type.h"
#include "FaceDetect.h"
#include "FaceAttributeAnalyze.h"
#include "FaceRecognize.h"
#include "FaceVerify.h"
#include "FaceRegister.h"
#include "Tools.h"
#include "Codec.h"

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {

    /**
     * @brief [EN] get version of sdk
     * @brief [中文] 获得sdk的版本号
     * @return [EN] version string
     * @return [中文] 版本号
     */
    DLL_API char *getVersion();

    DLL_API const char *getVendorId();

    DLL_API bool isAuthorized();

    /**
     * @brief [EN] authorize sdk
     * @brief [中文] 授权sdk
     * @param [in] key [EN] authorize key
     * @param [in] key [中文] 授权码
     * @return Error
     * @attention [EN] if you have not key, please contact alibaba engineer first.
     * @attention [中文] 如果没有授权码，请先联系阿里工程师索取。
     * @attention [EN] you must authorize first before use sdk.
     * @attention [中文] 在使用sdk前，必须先授权。
     */
    DLL_API int authorize(char *key);

    /**
     * @brief [EN] set cloud ip addr
     * @brief [中文] 设置服务端的ip地址
     * @param [in] ip [EN] ip addr
     * @param [in] ip [中文] ip地址
     * @param [in] port [EN] port
     * @param [in] port [中文] 端口号
     * @return void
     * @attention [EN] when create FaceDetect|FaceRecognize|FaceVerify instance, you must set mode, if you want set CLOUD mode, you must setCloudAddr
     * @attention [中文] 当创建FaceDetect|FaceRecognize|FaceVerify实例时，你必须设置运行模式，如果想设置云端一体模式的话，你必须设置服务端ip地址。
     * @see Mode
     */
    DLL_API void setCloudAddr(const char *ip, const int port);

    /**
     * @brief [EN] set cloud login account
     * @brief [中文] 设置服务端的登陆用户名和密码
     * @param [in] cId [EN] account id
     * @param [in] cId [中文] 用户名
     * @param [in] cSecret [EN] account secret
     * @param [in] cSecret [中文] 用户密码
     * @return void
     * @attention [EN] if you want use CLOUD mode, you must setCloudLoginAccount
     * @attention [中文] 如果你想使用云端一体模式的话，你必须设置服务端登陆用户名和密码。
     * @see Mode
     */
    DLL_API void setCloudLoginAccount(const char *cId, const char *cSecret);

    /**
     * @brief [EN] enable or disable debug mode
     * @brief [中文] 开启或者关闭debug模式
     * @param [in] enable [EN] True：enable debug mode， False：disable debug mode
     * @param [in] enable [中文] True：开启debug模式， False：关闭debug模式
     * @return void
     * @attention [EN] debug mode is disable default.
     * when debug mode is disable, important log will save.
     * when debug mode is enable, all log will save.
     * @attention [中文]debug模式默认关闭。
     * 当关闭debug模式时，只有重要的log被保存。
     * 当开启debug模式时，所有的log被保存。
     * @see setPersistencePath
     */
    DLL_API void enableDebug(const bool enable);

    /**
     * @brief [EN] set persistence(save) path for log|recognize database
     * @brief [中文] 设置保存log、识别人脸库的地址
     * @param [in] path [EN] save path
     * @param [in] path [中文] 保存的地址
     * @return void
     * @attention [EN] if not set, save path is bin current path default.
     * @attention [中文] 如果没有设置，默认使用可执行程序的路径。
     * @see setPersistencePath
     */
    DLL_API void setPersistencePath(const char *path);

    DLL_API void setThreadNum(const int num);


    /**
     * @brief [EN] create a face instance
     * @brief [中文] 创建一个人脸实例
     * @return Face
     * @see deleteFace
     */
    DLL_API Face *newFace();

    /**
     * @brief [EN] create face instance array
     * @brief [中文] 创建人脸实例数组
     * @param [in] size [EN] array size
     * @param [in] size [中文] 数组长度
     * @return Face array
     * @see deleteFaces
     */
    DLL_API Face *newFaces(int size);

    DLL_API Face *getFace(Face *faces, int index);

    DLL_API void deleteFace(Face *&face);

    DLL_API void deleteFaces(Face *&faces);

    DLL_API FacePoint *getFacePoint(FacePoint *facePoints, int index);


    //group
    DLL_API void deleteGroup(Group *&group);

    DLL_API void deleteGroups(Group *&groups);

    DLL_API Group *getGroup(Group *groups, int index);


    //person
    DLL_API Person *newPerson();

    DLL_API void deletePerson(Person *&person);

    DLL_API void deletePersons(Person *&persons);

    DLL_API Person *getPerson(Person *persons, int index);


    //RecognizeResult
    DLL_API RecognizeResult *getRecognizeResult(RecognizeResult *recognizeResults, int index);

    DLL_API void deleteRecognizeResult(RecognizeResult *&recognizeResult);

    DLL_API void deleteRecognizeResults(RecognizeResult *&recognizeResults);


    //VerifyResult
    DLL_API VerifyResult *getVerifyResult(VerifyResult *verifyResults, int index);

    DLL_API void deleteVerifyResult(VerifyResult *&verifyResult);

    DLL_API void deleteVerifyResults(VerifyResult *&verifyResults);

}
