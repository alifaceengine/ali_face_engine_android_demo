#pragma once

/**
 * @file FaceRecognize.h
 * @brief [EN] face recognize, support picture and video
 * @brief [中文] 人脸识别，支持照片和视频
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
     * @brief [EN] face recognize class
     * @brief [中文] 人脸识别类
     */
    class DLL_API FaceRecognize {

    public:
        /**
         * @brief [EN] recognize or verify video listener
         * @brief [中文] 视频识别和比对的监听者
         * @note [EN] RecognizeVideoListener is pure virtual class, you must inherit and implement it if to use
         * @note [中文] RecognizeVideoListener是纯虚类，如果使用必须继承并实现它。
         */
        class RecognizeVideoListener {
        public:
            virtual ~RecognizeVideoListener() {

            }

            /**
             * @brief [EN] get recognizeVideo result
             * @brief [中文] 获得recognizeVideo的识别结果
             * @param [out] image [EN] the video image recognized
             * @param [out] image [中文] 识别到结果时的视频帧
             * @param [out] results [EN] recognize result array
             * @param [out] results [中文] 识别结果数组
             * @param [out] resultNum [EN] results size
             * @param [out] resultNum [中文] results长度
             * @return void
             * @see recognizeVideo
             */
            virtual void onRecognized(Image &image, list<RecognizeResult> resultList) = 0;

            /**
             * @brief [EN] get verifyVideo result
             * @brief [中文] 获得verifyVideo的比对结果
             * @param [out] image [EN] the video image verified
             * @param [out] image [中文] 比对到结果时的视频帧
             * @param [out] results [EN] verify result array
             * @param [out] results [中文] 比对结果数组
             * @param [out] resultNum [EN] results size
             * @param [out] resultNum [中文] results长度
             * @return void
             * @see verifyVideo
             */
            virtual void onVerified(Image &image, list<VerifyResult> resultList) = 0;
        };

    public:
        /**
         * @brief [EN] create FaceRecognize instance
         * @brief [中文] 创建FaceRecognize实例
         * @param [in] mode
         * @note [EN] default is TERMINAL
         * @note [中文] 默认是TERMINAL
         * @attention [EN] mode can't be changed after instance created
         * @attention [中文] mode在实例创建后不能被修改
         * @warning
         * @return [EN] 0:Fail other:FaceRecognize instance
         * @return [中文] 0:失败 其他:FaceRecognize
         * @see deleteInstance
         * @see Mode
        */
        static FaceRecognize *createInstance(enum Mode mode = TERMINAL);

        /**
         * @brief [EN] delete FaceRecognize instance
         * @brief [中文] 销毁FaceRecognize实例
         * @param [in] ins [EN] FaceRecognize instance
         * @param [in] ins [中文] FaceRecognize实例
         * @return void
         * @see createInstance
        */
        static void deleteInstance(FaceRecognize *&ins);

    public:

        /**
         * @brief [EN] set group to recognize
         * @brief [中文] 设置要识别的组
         * @param [in] groupId [EN] group id
         * @param [in] groupId [中文] 组的id
         * @return Error
         * @see Error
        */
        virtual int setGroupId(const string &groupId) = 0;

        /**
         * @brief [EN] reload database to cache
         * @brief [中文] 重新加载数据库到内存
         * @return Error
         * @see Error
        */
        virtual int reloadDB() = 0;

        /**
         * @brief [EN] set RecognizeVideoListener
         * @brief [中文] 设置视频识别的监听
         * @param [in] listener [EN] listener
         * @param [in] listener [中文] 监听者
         * @return void
         * @see RecognizeVideoListener
        */
        virtual void setRecognizeVideoListener(RecognizeVideoListener *listener) = 0;

        /**
         * @brief [EN] get RecognizeVideoListener
         * @brief [中文] 获得视频识别监听者
         * @return RecognizeVideoListener
         * @see RecognizeVideoListener
        */
        virtual RecognizeVideoListener *getRecognizeVideoListener() = 0;

        /**
         * @brief [EN] recognize persons of video from a group
         * @brief [中文] 识别视频中的人
         * @param [in] image [EN] video image to recognize
         * @param [in] image [中文] 要识别的视频
         * @param [in,out] faceList [EN] faces in video to recognize
         * @param [in,out] faceList [中文] 要识别视频中的哪些人脸
         * @attention [EN] faceList can be input or output parameter.
         * when faceList is null, it will be output parameter, recognizeVideo will detect faces first, and then recognize the faces.
         * And output faceList will be setted the detected faces.
         * when faceList is not null, it will be input parameter, recognizeVideo will recognize the input faces.
         * @attention [中文] faceList既可以是输入也可以是输出。
         * 当faceList为空时，它就是输出参数，recognizeVideo会先检测视频中的人脸，然后在识别这些人脸。
         * faceList会被设置为检测到人脸。
         * 当faceList不为空时，它是输入参数，recognizeVideo直接识别这些人脸。
         * @attention [EN] RecognizeResult will get from RecognizeVideoListener.onRecognized.
         * @attention [中文] 识别结果会通过RecognizeVideoListener.onRecognized获得。
         * @return Error
         * @see Error
         * @see RecognizeVideoListener
        */
        virtual int recognizeVideo(Image &image, list<Face> &faceList) = 0;

        /**
         * @brief [EN] recognize persons of picture from a group
         * @brief [中文] 识别照片中的人
         * @param [in] image [EN] picture image to recognize
         * @param [in] image [中文] 要识别的照片
         * @param [in,out] faceList [EN] faces in picture to recognize
         * @param [in,out] faceList [中文] 要识别照片中的哪些人脸
         * @param [out] recognizeResults [EN] recognize result array
         * @param [out] recognizeResults [中文] 识别结果数组
         * @param [out] resultNum [EN] recognizeResults size
         * @param [out] resultNum [中文] 识别结果数组长度
         * @attention [EN] faces and faceNum can be input or output parameter.
         * when faceList is null, it will be output parameter, recognizePicture will detect faces first, and then recognize the faces.
         * And output faceList will be setted the detected faces.
         * when faceList is not null, it will be input parameter, recognizePicture will recognize the input faces.
         * @attention [中文] faceList既可以是输入也可以是输出。
         * 当faceList为null时，它就是输出参数，recognizePicture会先检测照片中的人脸，然后在识别这些人脸。
         * faceList会被设置为检测到人脸。
         * 当faceList不为null时，它是输入参数，recognizePicture直接识别这些人脸。
         * @return Error
         * @see Error
        */
        virtual int
        recognizePicture(Image &image, list<Face> &faceList, list<RecognizeResult> &recognizeResultList) = 0;

        /**
         * @brief [EN] verify persons of video with a person of a group
         * @brief [中文] 将视频中的人和组中的一个人进行比对
         * @param [in] personId [EN] person id to verify
         * @param [in] personId [中文] 要比对的人id
         * @param [in] image [EN] video image to verify
         * @param [in] image [中文] 要比对的视频
         * @param [in,out] faceList [EN] faces in video to verify
         * @param [in,out] faceList [中文] 要比对视频中的哪些人脸
         * @attention [EN] faces and faceNum can be input or output parameter.
         * when faceList is null, it will be output parameter, verifyVideo will detect faces first, and then verify the faces.
         * And output faceList will be setted the detected faces.
         * when faceList is not null, it will be input parameter, verifyVideo will verify the input faces.
         * @attention [中文] faces 和 faceNum既可以是输入也可以是输出。
         * 当faceList为null时，它就是输出参数，verifyVideo会先检测视频中的人脸，然后在比对这些人脸。
         * faceList会被设置为检测到人脸。
         * 当faceList不为null时，它是输入参数，verifyVideo直接比对这些人脸。
         * @attention [EN] VerifyResult will get from RecognizeVideoListener.onVerified.
         * @attention [中文] 比对结果会通过RecognizeVideoListener.onVerified获得。
         * @return Error
         * @see Error
         * @see RecognizeVideoListener
        */
        virtual int verifyVideo(const string &personId, Image &image, list<Face> &faceList) = 0;

        /**
         * @brief [EN] verify persons of a picture with a person of a group
         * @brief [中文] 将照片中的人和组中的一个人进行比对
         * @param [in] personId [EN] person id to verify
         * @param [in] personId [中文] 要比对的人id
         * @param [in] image [EN] picture image to verify
         * @param [in] image [中文] 要比对的照片
         * @param [in,out] faceList [EN] faces in picture to verify
         * @param [in,out] faceList [中文] 要比对照片中的哪些人脸
         * @param [out] verifyResults [EN] verify result array
         * @param [out] verifyResults [中文] 比对结果数组
         * @param [out] resultNum [EN] verifyResults size
         * @param [out] resultNum [中文] 比对结果数组长度
         * @attention [EN] faces and faceNum can be input or output parameter.
         * when faceList is null, it will be output parameter, verifyPicture will detect faces first, and then verify the faces.
         * And output faceList will be setted the detected faces.
         * when faceList is not null, it will be input parameter, verifyPicture will verify the input faces.
         * @attention [中文] faces 和 faceNum既可以是输入也可以是输出。
         * 当faceList为null时，它就是输出参数，verifyPicture会先检测照片中的人脸，然后再比对这些人脸。
         * faceList会被设置为检测到人脸。
         * 当faceList不为null时，它是输入参数，verifyPicture直接比对这些人脸。
         * @return Error
         * @see Error
        */
        virtual int verifyPicture(const string &personId, Image &image, list<Face> &faceList,
                                  list<VerifyResult> &verifyResultList) = 0;

    protected:
        FaceRecognize();

        virtual ~FaceRecognize();
    };
}