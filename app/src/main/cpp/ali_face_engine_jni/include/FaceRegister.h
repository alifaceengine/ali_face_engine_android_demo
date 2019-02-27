#pragma once

/**
 * @file FaceRegister.h
 * @brief [EN] register faces
 * @brief [中文] 注册人脸
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
    class DLL_API FaceRegister {

    public:
        /**
         * @brief [EN] create FaceRegister instance
         * @brief [中文] 创建FaceRegister实例
         * @warning
         * @return [EN] 0:Fail other:FaceRegister instance
         * @return [中文] 0:失败 其他:FaceRegister
         * @see deleteInstance
        */
        static FaceRegister *createInstance();

        /**
         * @brief [EN] delete FaceRegister instance
         * @brief [中文] 销毁FaceRegister实例
         * @param [in] ins [EN] FaceRegister instance
         * @param [in] ins [中文] FaceRegister实例
         * @return void
         * @see createInstance
        */
        static void deleteInstance(FaceRegister *&ins);

    public:

        /**
         * @brief [EN] create a group
         * @brief [中文] 创建一个组
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] modelType [EN] model type
         * @param [in] modelType [中文] 模型类型
         * @return Error
         * @attention [EN] group model type can't be change after a group created
         * @attention [中文] 组的模型类型不能在创建后改变
         * @see Error
        */
        virtual int createGroup(Group &group) = 0;

        /**
         * @brief [EN] delete a group
         * @brief [中文] 删除一个组
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @return Error
         * @see Error
        */
        virtual int deleteGroup(const string &groupId) = 0;

        /**
         * @brief [EN] check a group if exist
         * @brief [中文] 判断一个组是否存在
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [out] exist [EN] if exist
         * @param [out] exist [中文] 是否存在
         * @return Error
         * @see Error
        */
        virtual bool isGroupExist(const string &groupName) = 0;

        /**
         * @brief [EN] update group name for existing group
         * @brief [中文] 更改一个组的名字
         * @param [in] oldGroupName [EN] group name before update
         * @param [in] oldGroupName [中文] 修改前组的名字
         * @param [in] newGroupName [EN] group name after update
         * @param [in] newGroupName [中文] 修改后组的名字
         * @return Error
         * @see Error
        */
        virtual int updateGroup(const string &groupId, Group &group) = 0;

        /**
         * @brief [EN] get group info by group name
         * @brief [中文] 通过组名获得组的信息
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [out] groupInfo [EN] group info
         * @param [out] groupInfo [中文] 组信息
         * @return Error
         * @see Error
        */
        virtual int getGroup(const string &groupId, Group &group) = 0;

        /**
         * @brief [EN] get all group info
         * @brief [中文] 获得所有组的信息
         * @param [out] groupInfos [EN] group info array
         * @param [out] groupInfos [中文] 组信息数组
         * @param [out] size [EN] groupInfos size
         * @param [out] size [中文] groupInfos数组的长度
         * @return Error
         * @see Error
        */
        virtual int getAllGroups(list<Group> &groupList) = 0;

        /**
         * @brief [EN] add a person to a group
         * @brief [中文] 向一个组添加人
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @return Error
         * @attention [EN] a group can contain many persons
         * @attention [中文] 一个组可以包含多个人
         * @see Error
        */
        virtual int addPerson(const string &groupId, Person &person) = 0;

        /**
         * @brief [EN] delete a person from a group
         * @brief [中文] 删除一个组的某个人
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @return Error
         * @see Error
        */
        virtual int deletePerson(const string &personId) = 0;

        /**
         * @brief [EN] delete all persons of a group
         * @brief [中文] 删除一个组所有人
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @return Error
         * @see Error
        */
        virtual int deleteAllPersons(const string &groupId) = 0;

        /**
         * @brief [EN] update person name
         * @brief [中文] 修改一个人的名字
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] oldPersonName [EN] person name before update
         * @param [in] oldPersonName [中文] 修改前的人名
         * @param [in] newPersonName [EN] person name after update
         * @param [in] newPersonName [中文] 修改后的人名
         * @return Error
         * @see Error
        */
        virtual int updatePerson(const string &personId, Person &person) = 0;

        /**
         * @brief [EN] get person info by person name
         * @brief [中文] 通过人名获得人的信息
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人名
         * @param [out] person [EN] person info
         * @param [out] person [中文] 人信息
         * @return Error
         * @see Person
         * @see Error
        */
        virtual int getPerson(const string &personId, Person &person) = 0;

        /**
         * @brief [EN] get all person infos of a group
         * @brief [中文] 通过一个组所有人的信息
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [out] persons [EN] person info array
         * @param [out] persons [中文] 人的信息数组
         * @param [out] personNum [EN] persons size
         * @param [out] personNum [中文] persons数组的长度
         * @return Error
         * @see Person
         * @see Error
        */
        virtual int getAllPersons(const string &groupId, list<Person> &personList) = 0;

        /**
         * @brief [EN] get person number of a group
         * @brief [中文] 获得一个组的人的数量
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [out] num [EN] person number
         * @param [out] num [中文] 人的数量
         * @return Error
         * @see Error
        */
        virtual int getPersonNum(const string &groupId, int &num) = 0;

        /**
         * @brief [EN] check if a person exist
         * @brief [中文] 判断一个人是否存在
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @param [out] exist [EN] if exist
         * @param [out] exist [中文] 是否存在
         * @return Error
         * @see Error
        */
        virtual bool isPersonExist(const string &groupId, const string &personName) = 0;

        /**
         * @brief [EN] add a feature for a person
         * @brief [中文] 给一个人添加特征
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @param [in] featureId [EN] feature id
         * @param [in] featureId [中文] 特征编号
         * @param [in] feature [EN] feature string
         * @param [in] feature [中文] 特征字符串
         * @return Error
         * @attention [EN] a person can contain many features
         * @attention [中文] 一个人可以有多个特征
         * @see Error
        */
        virtual int addFeature(const string &personId, Feature &feature) = 0;

        /**
         * @brief [EN] delete a feature from a person
         * @brief [中文] 删除一个人的某个特征
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @param [in] featureId [EN] feature id
         * @param [in] featureId [中文] 特征编号
         * @return Error
         * @see Error
        */
        virtual int deleteFeature(const string &featureId) = 0;

        /**
         * @brief [EN] delete all features of a person
         * @brief [中文] 删除一个人所有特征
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @return Error
         * @see Error
        */
        virtual int deleteAllFeatures(const string &personId) = 0;

        /**
         * @brief [EN] update feature by feature id
         * @brief [中文] 修改一个feature
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人名
         * @param [in] featureId [EN] feature id
         * @param [in] featureId [中文] 要修改的特征编号
         * @param [in] feature [EN] the feature to update
         * @param [in] feature [中文] 要更新的特征
         * @return Error
         * @see Error
        */
        virtual int updateFeature(const string &featureId, Feature &feature) = 0;

        /**
         * @brief [EN] get feature number of a person
         * @brief [中文] 获得一个人的特征数量
         * @param [in] groupName [EN] group name
         * @param [in] groupName [中文] 组的名字
         * @param [in] personName [EN] person name
         * @param [in] personName [中文] 人的名字
         * @param [out] num [EN] feature number
         * @param [out] num [中文] 特征的数量
         * @return Error
         * @see Error
        */
        virtual int getFeatureNum(const string &personId, int &num) = 0;

        /**
         * @brief [EN] extract feature for a face
         * @brief [中文] 获得一个人脸的特征
         * @param [in] image [EN] image，it can be video and picture
         * @param [in] image [中文] 图像，可以是照片，也可以是视频
         * @param [in] face [EN] the face to extract feature
         * @param [in] face [中文] 要提取特征的人脸
         * @param [in] modelType [EN] feature model type
         * @param [in] modelType [中文] 特征模型的类型
         * @param [out] feature [EN] feature extracted
         * @param [out] feature [中文] 获得的特征
         * @return Error
         * @see Error
        */
        virtual int extractFeature(Image &image, Face &face, enum ModelType modelType, string &feature) = 0;

    protected:
        FaceRegister();

        virtual ~FaceRegister();
    };
}