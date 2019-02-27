#pragma once

/**
 * @file FaceDetect.h
 * @brief [EN] face detect, support picture and video
 * @brief [中文] 人脸检测，支持照片和视频
 * @author [EN] hujunyuan
 * @author [中文] 胡俊远
*/

#include <list>

#include "type.h"

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {
    /**
     * @brief [EN] face detect class
     * @brief [中文] 人脸检测类
     */
    class DLL_API FaceDetect {
    public:
        /**
         * @brief [EN] detect video type
         * @brief [中文] 视频检测类型
         */
        enum DetectVideoType {
            ACCURATE = 1,///< [EN] more accurate, it must be use when FaceRecognize and FaceVerify [中文] 更精确，人脸识别和比对必须使用该类型
            QUICK = 2///< [EN] more quick, it can be use when only use face key points [中文] 检测速度更快，当只是使用人脸特征点的场景可以使用该类型
        };

    public:
        /**
         * @brief [EN] create FaceDetect instance
         * @brief [中文] 创建FaceDetect实例
         * @param [in] mode
         * @note [EN] default is TERMINAL
         * @note [中文] 默认是TERMINAL
         * @attention [EN] mode can't be changed after instance created
         * @attention [中文] mode在实例创建后不能被修改
         * @warning
         * @return [EN] 0:Fail other:FaceDetect instance
         * @return [中文] 0:失败 其他:FaceDetect实例
         * @see deleteInstance
         * @see Mode
        */
        static FaceDetect *createInstance(enum Mode mode = TERMINAL);

        /**
         * @brief [EN] delete FaceDetect instance
         * @brief [中文] 销毁FaceDetect实例
         * @param [in] ins [EN] FaceDetect instance
         * @param [in] ins [中文] FaceDetect实例
         * @return void
         * @see createInstance
        */
        static void deleteInstance(FaceDetect *&ins);

    public:

        /**
         * @brief [EN] set picture detect parameter
         * @brief [中文] 设置照片检测参数
         * @param [in] parameter [EN] detect parameter
         * @param [in] parameter [中文] 检测参数
         * @return Error
         * @attention [EN] parameter should get from getPictureParameter, because it will inherit current parameter
         * @attention [中文] parameter最好通过getPictureParameter获得，这样它就可以在现有parameter的基础上设置
         * @see setVideoParameter
         * @see DetectParameter
         * @see Error
        */
        virtual int setPictureParameter(DetectParameter &parameter) = 0;

        /**
         * @brief [EN] get picture detect parameter
         * @brief [中文] 获得照片检测参数
         * @return [EN]picture detect parameter
         * @return [中文] 照片检测参数
         * @see setPictureParameter
         * @see getVideoParameter
         * @see DetectParameter
        */
        virtual DetectParameter &getPictureParameter() = 0;

        /**
         * @brief [EN] detect faces from picture
         * @brief [中文] 在照片中检测人脸
         * @param [in] image [EN] picture
         * @param [in] image [中文] 照片
         * @param [out] faceList [EN] faces detected from picture
         * @param [out] faceList [中文] 检测到的人脸
         * @return Error
         * @attention [EN] return OK if no face
         * @attention [中文] 如果没有人脸，返回值也会是OK
         * @see detectVideo
         * @see Error
        */
        virtual int detectPicture(Image &image, list<Face> &faceList) = 0;

        /**
         * @brief [EN] set video detect parameter
         * @brief [中文] 设置视频检测参数
         * @param [in] parameter [EN] detect parameter
         * @param [in] parameter [中文] 检测参数
         * @return Error
         * @attention [EN] parameter should get from getVideoParameter, because it will inherit current parameter
         * @attention [中文] parameter最好通过getVideoParameter获得，这样它就可以在现有parameter的基础上设置
         * @see setPictureParameter
         * @see DetectParameter
         * @see Error
        */
        virtual int setVideoParameter(DetectParameter parameter) = 0;

        /**
         * @brief [EN] get video detect parameter
         * @brief [中文] 获得视频检测参数
         * @return [EN] video detect parameter
         * @return [中文] 视频检测参数
         * @see setVideoParameter
         * @see getPictureParameter
         * @see DetectParameter
        */
        virtual DetectParameter getVideoParameter() = 0;

        /**
         * @brief [EN] set video detect type, please see DetectVideoType
         * @brief [中文] 设置视频检测类型，具体参考DetectVideoType
         * @param [in] type [EN] detect type
         * @param [in] type [中文] 检测类型
         * @return Error
         * @see DetectVideoType
         * @see Error
        */
        virtual int setDetectVideoType(enum DetectVideoType type) = 0;

        /**
         * @brief [EN] detect faces from video
         * @brief [中文] 在视频中检测人脸
         * @param [in] image [EN] video
         * @param [in] image [中文] 视频
         * @param [out] faceList [EN] faces detected from video
         * @param [out] faceList [中文] 检测到的人脸
         * @return Error
         * @attention [EN] return OK if no face
         * @attention [中文] 如果没有人脸，返回值也会是OK
         * @see detectPicture
         * @see setDetectVideoType
         * @see Error
        */
        virtual int detectVideo(Image &image, list<Face> &faceList) = 0;

    protected:
        FaceDetect();

        virtual ~FaceDetect();
    };
}
