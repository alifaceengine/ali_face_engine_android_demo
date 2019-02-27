#pragma once

#include <string>
#include <string.h>
#include <list>

/**
 * @file type.h
 * @brief [EN] type define.
 * @brief [中文] 类型定义。
 * @author [EN] hujunyuan
 * @author [中文] 胡俊远
*/

using namespace std;

#ifdef WIN32
#define DLL_API __declspec(dllexport)
#else
#define DLL_API
#endif

namespace ali_face_engine {
    /**
     * @brief [EN] execute status
     * @brief [中文] 执行的结果
    */
    enum Error {
        OK = 0,///< [EN] success [中文] 成功
        FAILED = -1,///< [EN] other fail [中文] 其他失败原因
        ERROR_EXPIRE = -2,///< [EN] sdk authorize key is expired [中文] sdk授权码已经过期
        ERROR_AUTH_FAIL = -3,///< [EN] authorize key is wrong [中文] sdk授权码错误
        ERROR_INVALID_ARGUMENT = -4,///< [EN] argument invalid [中文] 参数错误
        ERROR_DB_EXEC = -5,///< [EN] database execute error [中文] 数据库操作错误
        ERROR_EXISTED = -6,///< [EN] existed error [中文] 已经存在的错误
        ERROR_NOT_EXIST = -7,///< [EN] not exist error [中文] 不存在的错误
        ERROR_NETWORK_FAIL = -8,///< [EN] network error [中文] 网络错误
        ERROR_NETWORK_RECV_JSON_WRONG = -9,///< [EN] json received from cloud error [中文] 从服务端获得的json错误
        ERROR_NO_FACE = -10,///< [EN] no face error [中文] 没有人脸的错误
        ERROR_FORMAT_NOT_SUPPORT = -11,///< [EN] image format not support [中文] 不支持的图像格式
        ERROR_NO_ID = -12,///< [EN] no id [中文] 没有id

        ERROR_CLOUD_OK = OK,///< [EN] cloud success [中文] 服务端执行成功
        ERROR_CLOUD_ACCOUT_WRONG = -20,///< [EN] cloud account wrong [中文] 服务端账户用户名和密码错误
        ERROR_CLOUD_REQUEST_DATA_ERROR = -21,///< [EN] cloud request data is wrong [中文] 请求的数据格式错误
        ERROR_CLOUD_DB_EXEC_ERROR = -22,///< [EN] cloud database execute error [中文] 服务端数据库操作失败
        ERROR_CLOUD_EXISTED_ERROR = -23,///< [EN] cloud existed error [中文] 服务端已经存在的错误
        ERROR_CLOUD_NOT_EXIST_ERROR = -24,///< [EN] cloud not exist error [中文] 服务端不存在的错误
        ERROR_CLOUD_NO_AUTHORIZE = -25,///< [EN] cloud sdk authorize error [中文] 服务端sdk授权失败
        ERROR_CLOUD_ALGORITHOM_ERROR = -26,///< [EN] cloud sdk execute error [中文] 服务端sdk执行失败
        ERROR_CLOUD_NO_FACE = -27,///< [EN] cloud no face error [中文] 服务端没有人脸的错误
        ERROR_CLOUD_FAILED = -28,///< [EN] cloud other fail [中文] 服务端其他失败
        ERROR_CLOUD_NOT_SUPPORT = -29///< [EN] cloud not support some function [中文] 服务端不支持某些功能
    };

    /**
     * @brief [EN] run mode
     * @brief [中文] 运行模式
    */
    enum Mode {
        TERMINAL = 1,///< [EN]run offline on terminal [中文]在端设备上离线运行
        CLOUD = 2///< [EN]run on terminal and cloud [中文]云端一体
    };

    /**
     * @brief [EN] feature model type
     * @brief [中文] 特征模型类型
    */
    enum ModelType {
        MODEL_SMALL = 1,///< [EN]face small feature type [中文]人脸特征小模型类型
        MODEL_BIG = 2///< [EN]face big feature type [中文]人脸特征大模型类型
    };

    /**
     * @brief [EN] image format
     * @brief [中文] 图像格式
    */
    enum ImageFormat {
        ImageFormat_UNKNOWN = -1,///< [EN]unknown image format, maybe JPEG,PNG,BMP [中文]未知的图像格式，可能是JPEG,PNG,BMP
        RGB888 = 0,///< rgb888
        BGR888 = 1,///< bgr888
        NV21 = 10,///< nv21, yuv420sp, YYYYYYYY VUVU
        NV12 = 11,///< nv12, yuv420sp, YYYYYYYY UVUV
        YV12 = 12,///<yv12, yuv420p, YYYYYYYY VV UU
        I420 = 13,///<I420, yuv420p, YYYYYYYY UU VV
        JPEG = 20,///< jpeg
        PNG = 21,///< png
        BMP = 22///< bmp
    };

    /**
     * @brief [EN] image rotation
     * @brief [中文] 图像转向
    */
    enum ImageRotation {
        ANGLE_0 = 0,///< angle 0
        ANGLE_90 = 90,///< angle 90
        ANGLE_180 = 180,///< angle 180
        ANGLE_270 = 270///< angle 270
    };

    /**
     * @brief [EN] image
     * @brief [中文] 图像
    */
    class DLL_API Image {
    public:
        /**
         * @attention [EN] data must be set
         * @attention [中文] 必选
         */
        unsigned char *data;///< [EN] image buffer addr [中文] 图像数据地址
        /**
         * @attention [EN] format must be set
         * @attention [中文] 必选
         */
        enum ImageFormat format;///< [EN] image format [中文] 图像格式

        /**
         * @attention [EN] rotation must be set when format is RGB888|BGR888|NV21|YV12|I420
         * @attention [中文] 当format是RGB888|BGR888|NV21|YV12|I420时，rotation必须设置
         */
        enum ImageRotation rotation;///< [EN] image rotation [中文] 图像格式

        /**
         * @attention [EN] width must be set when format is RGB888|BGR888|NV21|YV12|I420
         * @attention [中文] 当format是RGB888|BGR888|NV21|YV12|I420时，width必须设置
         * */
        int width;///< [EN] image width [中文] 图像宽度

        /**
         * @attention [EN] stride can be set when format is RGB888|BGR888|NV21|YV12|I420, if stride is 0, means stride equals width
         * @attention [中文] 当format是RGB888|BGR888|NV21|YV12|I420时，stride可以设置，当stride设为0时，那么stride和width等长
         * */
        int stride;///< [EN] image stride [中文] 图像对齐宽度

        /**
         * @attention [EN] height must be set when format is RGB888|BGR888|NV21|YV12|I420
         * @attention [中文] 当format是RGB888|BGR888|NV21|YV12|I420时，height必须设置
         * */
        int height;///< [EN] image height [中文] 图像高度

        /**
         * @attention [EN] dataLen must be set when format is ImageFormat_UNKNOWN|JPEG|PNG|BMP
         * @attention [中文] 当format是ImageFormat_UNKNOWN|JPEG|PNG|BMP时，dataLen必须设置
         * */
        int dataLen;

        Image();
    };

    /**
     * @brief [EN] face rect
     * @brief [中文] 人脸框
    */
    class DLL_API Rect {
    public:
        int left;
        int top;
        int right;
        int bottom;

        Rect();
    };

    /**
     * @brief [EN] face pose
     * @brief [中文] 人脸姿态角度
    */
    class DLL_API Pose {
    public:
        /**
         * @brief [EN] it is positive when look up, it is negative when look down
         * @brief [中文] 人脸在图像中抬头为正，低头为负
         * */
        float pitch;
        /**
         * @brief [EN] it is positive when look left, it is negative when look right
         * @brief [中文] 人人脸在图像中左转为正，右转为负
         * */
        float yaw;
        /**
         * @brief [EN] it is positive when roll anticlockwise, it is negative when roll clockwise
         * @brief [中文] 人脸在图像中逆时针转为正，顺时针转为负
         * */
        float roll;

        Pose();
    };

    /**
     * @brief [EN] face key point
     * @brief [中文] 人脸关键点
    */
    class DLL_API FacePoint {
    public:
        float x;
        float y;

        FacePoint();
    };

    /**
     * @brief [EN] face quality
     * @brief [中文] 人脸质量
    */
    class DLL_API Quality {
    public:
        int score;///< [EN] 0-100, quality is more high when score is more big [中文] 取值0-100，数值越大，人脸质量越高

        Quality();
    };

    /**
     * @brief [EN] face liveness
     * @brief [中文] 人脸活体
    */
    class DLL_API Liveness {
    public:
        int score;///< [EN] 0-100, liveness is more high when score is more big [中文] 取值0-100，数值越大，人脸活体越高

        Liveness();
    };

    /**
     * @brief [EN] face gender
     * @brief [中文] 性别
    */
    enum Gender {
        GENGER_UNKNOWN = -1,///< [EN] unknown gender [中文] 未知的性别
        GENGER_MALE = 0,
        GENGER_FEMALE
    };

    enum Expression {
        EXPRESSION_UNKOWN = -1,
        EXPRESSION_DISLIKE = 0,
        EXPRESSION_LIKE,
        EXPRESSION_NETRUAL,
        EXPRESSION_SURPRISE
    };

    enum Glass {
        GLASS_UNKOWN = -1,
        GLASS_NO = 0,
        GLASS_YES
    };

    /**
     * @brief [EN] face detect parameter
     * @brief [中文] 人脸检测参数
     * @see setPictureParameter
     * @see setVideoParameter
    */
    class DLL_API DetectParameter {
    public:
        /**
         * @brief [EN] enable quality check when set to 1, disable quality check when set to 0
         * @brief [中文] 当设为1时开启质量判断，当设为0时关闭质量判断
         * */
        bool checkQuality;
        /**
         * @brief [EN] enable liveness check when set to 1, disable liveness check when set to 0
         * @brief [中文] 当设为1时开启活体判断，当设为0时关闭活体判断
         * */
        bool checkLiveness;
        /**
        * @brief [EN] enable age check when set to 1, disable age check when set to 0
        * @brief [中文] 当设为1时开启年龄判断，当设为0时关闭年龄判断
        * */
        bool checkAge;
        /**
         * @brief [EN] enable gender check when set to 1, disable gender check when set to 0
         * @brief [中文] 当设为1时开启性别判断，当设为0时关闭性别判断
         * */
        bool checkGender;
        /**
         * @brief [EN] enable expression check when set to 1, disable expression check when set to 0
         * @brief [中文] 当设为1时开启表情判断，当设为0时关闭表情判断
         * */
        bool checkExpression;
        /**
         * @brief [EN] enable glass check when set to 1, disable glass check when set to 0
         * @brief [中文] 当设为1时开启是否戴眼镜判断，当设为0时关闭是否戴眼镜判断
         * */
        bool checkGlass;
        /**
         * @brief [EN] region of interest
         * @brief [中文] 检测当区域
         * @note [EN] if image is 640x480, when you want detect round 200x200, you can set eft：220，top：120，right：420，bottom：320.
         * @note [中文] 如image是640x480，但是只需要中间200x200的区域进行检测，则可设置roi为left：220，top：120，right：420，bottom：320。
         * */
        Rect roi;

        DetectParameter();
    };

    const int GROUP_NAME_MAX_SIZE = 100;
    const int PERSON_NAME_MAX_SIZE = 50;
    const int FEATURE_ID_MAX_SIZE = 50;

    /**
     * @brief [EN] face recognize result
     * @brief [中文] 人脸识别结果
    */
    class DLL_API RecognizeResult {
    public:
        int trackId;///< [EN] face trackId [中文] 对应的人脸trackId
        string personId;///< [EN] person id [中文] 人编号
        string personName;///< [EN] person name [中文] 人姓名
        float similarity;///< [EN] similarity [中文] 相似度

        RecognizeResult();
    };

    /**
     * @brief [EN] face verify result
     * @brief [中文] 人脸比对结果
    */
    class DLL_API VerifyResult {
    public:
        int trackId;///< [EN] face trackId [中文] 对应的人脸trackId
        float similarity;///< [EN] similarity [中文] 相似度

        VerifyResult();
    };

    /**
     * @brief [EN] face attribute
     * @brief [中文] 人脸属性
    */
    class DLL_API Attribute {
    public:
        Quality quality;///< [EN] face quality [中文] 人脸质量
        Liveness liveness;///< [EN] face liveness [中文] 人脸活体
        int age;
        enum Gender gender;
        enum Expression expression;
        enum Glass glass;

        Attribute();
    };

    const int FACE_POINT_NUM = 51;

    /**
     * @brief [EN] face info
     * @brief [中文] 人脸信息
    */
    class DLL_API Face {
    public:
        int trackId;///< [EN] face trackId [中文] 人脸trackId
        Rect rect;///< [EN] face rect [中文] 人脸框
        Pose pose;///< [EN] face pose [中文] 人脸姿态
        FacePoint facePoints[FACE_POINT_NUM];///< [EN] face point [中文] 人脸关键点
        Attribute attribute;///< [EN] face attribute [中文] 人脸属性

        Face();
    };

    /**
     * @brief [EN] face feature
     * @brief [中文] 人脸特征
    */
    class DLL_API Feature {
    public:
        string id;///< [EN] feature id [中文] 人脸特征编号
        string name;///< [EN] feature name [中文] 人脸特征名称
        string feature;///< [EN] feature string [中文] 人脸特征字符串

        Feature();
    };

    /**
     * @brief [EN] person info
     * @brief [中文] 人的信息
    */
    class DLL_API Person {
    public:
        string id;
        string name;
        list<Feature> features;///< [EN] person feature array [中文] 人的特征数组

        Person();
    };

    /**
     * @brief [EN] group info
     * @brief [中文] 组信息
    */
    class DLL_API Group {
    public:
        string id;
        string name;///< [EN] group name [中文] 组的名字
        enum ModelType modelType;///< [EN] feature model type [中文] 特征模型类型

        Group();
    };
}