#pragma once

/**
 * @file FaceVerify.h
 * @brief [EN] face verify, support picture and video
 * @brief [中文] 人脸比对，支持照片和视频
 * @author [EN] hujunyuan
 * @author [中文] 胡俊远
*/

#include "type.h"

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {
    class DLL_API FaceVerify {

    public:
        /**
         * @brief [EN] verify video listener
         * @brief [中文] 视频比对的监听者
         * @note [EN] VerifyVideoListener is pure virtual class, you must inherit and implement it if to use
         * @note [中文] VerifyVideoListener是纯虚类，如果使用必须继承并实现它。
         */
        class VerifyVideoListener {
        public:
            virtual ~VerifyVideoListener() {

            }

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
         * @brief [EN] create FaceVerify instance
         * @brief [中文] 创建FaceVerify实例
         * @param [in] mode
         * @note [EN] default is TERMINAL
         * @note [中文] 默认是TERMINAL
         * @attention [EN] mode can't be changed after instance created
         * @attention [中文] mode在实例创建后不能被修改
         * @warning
         * @return [EN] 0:Fail other:FaceVerify instance
         * @return [中文] 0:失败 其他:FaceVerify
         * @see deleteInstance
         * @see Mode
        */
        static FaceVerify *createInstance(enum Mode mode = TERMINAL);

        /**
         * @brief [EN] delete FaceVerify instance
         * @brief [中文] 销毁FaceVerify实例
         * @param [in] ins [EN] FaceVerify instance
         * @param [in] ins [中文] FaceVerify实例
         * @return void
         * @see createInstance
        */
        static void deleteInstance(FaceVerify *&ins);

    public:
        /**
         * @brief [EN] set VerifyVideoListener
         * @brief [中文] 设置视频比对的监听
         * @param [in] listener [EN] listener
         * @param [in] listener [中文] 监听者
         * @return void
         * @see VerifyVideoListener
        */
        virtual void setVerifyVideoListener(VerifyVideoListener *listener) = 0;

        /**
         * @brief [EN] get VerifyVideoListener
         * @brief [中文] 获得视频比对监听者
         * @return VerifyVideoListener
         * @see VerifyVideoListener
        */
        virtual VerifyVideoListener *getVerifyVideoListener() = 0;

        /**
         * @brief [EN] verify faces of video with face registered by registerFace
         * @brief [中文] 将视频中的脸和registerFace注册的人脸比对
         * @param [in] image [EN] video image to verify
         * @param [in] image [中文] 要比对的视频
         * @param [in,out] faceList [EN] faces in video to verify
         * @param [in,out] faceList [中文] 要比对视频中的哪些人脸
         * @attention [EN] faceList can be input or output parameter.
         * when faceList size is 0, it will be output parameter, verifyVideo will detect faces first, and then verify the faces.
         * And output faceList will be setted the detected faces.
         * when faceList size is not 0, it will be input parameter, verifyVideo will verify the input faces.
         * @attention [中文] faceList既可以是输入也可以是输出。
         * 当faceList为空时，它就是输出参数，verifyVideo会先检测视频中的人脸，然后在比对这些人脸。
         * faceList会被设置为检测到人脸。
         * 当faceList不为空时，它是输入参数，verifyVideo直接比对这些人脸。
         * @attention [EN] VerifyResult will get from VerifyVideoListener.onVerified.
         * @attention [中文] 比对结果会通过VerifyVideoListener.onVerified获得。
         * @return Error
         * @see Error
         * @see VerifyVideoListener
         * @see registerFace
        */
        virtual int verifyVideo(Image &image, list<Face> &faceList) = 0;

        /**
         * @brief [EN] register face for verifyVideo
         * @brief [中文] 注册要视频比对的人脸
         * @param [in] image [EN] face所在的图像
         * @param [in] image [中文] image contains the face
         * @param [in] face [EN] the face to verify
         * @param [in] face [中文] 要比对的人脸
         * @return Error
         * @attention [EN] if you want verify video, you must register the face to verify.
         * @attention [中文] 如果你想比对视频，必须先注册一个要比对的人脸。
         * @see Error
         * @see verifyVideo
        */
        virtual int registerFace(Image &image, Face &face) = 0;

        /**
         * @brief [EN] verify face in image1 with faces in image2
         * @brief [中文] 将image1的一个人脸和image2的多个人脸比对
         * @param [in] image1 [EN] 要比对的图像
         * @param [in] image1 [中文] image to verify
         * @param [in,out] image1Face [EN] the face to veirify in image1
         * @param [in,out] image1Face [中文] image1中要比对的人脸
         * @param [in] image2 [EN] 被比对的图像
         * @param [in] image2 [中文] image to be verified
         * @param [in,out] image2Faces [EN] faces in image2 to verify
         * @param [in,out] image2Faces [中文] 要比对image2中的哪些人脸
         * @param [in,out] image2FacesNum [EN] image2Faces array size
         * @param [in,out] image2FacesNum [中文] image2Faces数组长度
         * @param [out] verifyResults [EN] verify result array
         * @param [out] verifyResults [中文] 比对结果数组
         * @param [out] resultNum [EN] verifyResults size
         * @param [out] resultNum [中文] 比对结果数组长度
         * @attention [EN] image1Face can be input or output parameter.
         * when image1Face is null, it will be output parameter, verifyPicture will detect the highest confidence face first.
         * And output image1Face will be setted the detected face.
         * when image1Face is not null, it will be input parameter.
         * @attention [中文] image1Face既可以是输入也可以是输出。
         * 当image1Face为null时，它就是输出参数，verifyPicture会先检测image1中的置信度最高的人脸。
         * image1Face会被设置为检测到人脸。
         * 当image1Face不为null时，它是输入参数。
         * @attention [EN] image2Faces and image2FacesNum can be input or output parameter.
         * when image2Faces is null, it will be output parameter, verifyPicture will detect faces first, and then verify the faces.
         * And output image2Faces will be setted the detected faces.
         * when faces is not null, it will be input parameter, verifyPicture will verify the input faces.
         * @attention [中文] image2Faces 和 image2FacesNum既可以是输入也可以是输出。
         * 当image2Faces为null时，它就是输出参数，verifyPicture会先检测照片中的人脸，然后再比对这些人脸。
         * image2Faces会被设置为检测到人脸。
         * 当image2Faces不为null时，它是输入参数，verifyPicture直接比对这些人脸。
         * @return Error
         * @see Error
        */
        virtual int
        verifyPicture(Image &image1, Face &image1Face, Image &image2, list<Face> &image2FaceList,
                      list<VerifyResult> &verifyResultList) = 0;

    protected:
        FaceVerify();

        virtual ~FaceVerify();
    };
}