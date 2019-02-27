package com.alibaba.cloud.alifaceenginedemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.Face;
import com.alibaba.cloud.faceengine.FaceDetect;
import com.alibaba.cloud.faceengine.FaceRecognize;
import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Feature;
import com.alibaba.cloud.faceengine.Group;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.Person;
import com.alibaba.cloud.faceengine.ImageFormat;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by h on 2018/8/31.
 */

public class DefaultService extends Service {
    private FaceRegister faceRegister;
    private FaceRecognize faceRecognize;
    private String groupName;
    private Face[] faces;
    private Bitmap bitmap;
    private FaceDetect faceDetect;
    private List<HashMap<String, String>> datas;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        faceRegister = FaceRegister.createInstance();
        faceRecognize = FaceRecognize.createInstance(Mode.TERMINAL);
        faceDetect = faceDetect.createInstance(Mode.TERMINAL);
        groupName = "DefaultGroupNotDelete";
        initData();
        for (int i = 0; i < datas.size(); i++) {
            createDefault(datas.get(i), "000" + i);
        }
        super.onCreate();
    }

    private void initData() {
        datas = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> data1 = new HashMap<String, String>();
        data1.put("name", "jay");
        data1.put("fileName", "jay.jpg");
        HashMap<String, String> data2 = new HashMap<String, String>();
        data2.put("name", "liudehua");
        data2.put("fileName", "liudehua.jpg");
        HashMap<String, String> data3 = new HashMap<String, String>();
        data3.put("name", "nini");
        data3.put("fileName", "nini.jpg");
        HashMap<String, String> data4 = new HashMap<String, String>();
        data4.put("name", "vae");
        data4.put("fileName", "vae.jpg");
        HashMap<String, String> data5 = new HashMap<String, String>();
        data5.put("name", "yangmi");
        data5.put("fileName", "yangmi.jpg");
        HashMap<String, String> data6 = new HashMap<String, String>();
        data6.put("name", "zhaoliying");
        data6.put("fileName", "zhaoliying.jpg");
        datas.add(data1);
        datas.add(data2);
        datas.add(data3);
        datas.add(data4);
        datas.add(data5);
        datas.add(data6);
    }

    private void createDefault(HashMap<String, String> data, String id) {
        if (faceRegister.isGroupExist(groupName)) {
            bitmap = Utils.getImageFromAssetsFile(DefaultService.this, data.get("fileName"));

            Person person = new Person();
            person.name = data.get("name");
            faceRegister.addPerson(groupName, person);
            Image image = new Image();
            image.data = Utils.bitmap2RGB(bitmap);
            image.format = ImageFormat.RGB888;
            image.rotation = ImageRotation.ANGLE_0;
            image.height = bitmap.getHeight();
            image.width = bitmap.getWidth();
            faces = faceDetect.detectPicture(image);
            String Feature = faceRegister.extractFeature(image, faces[0], ModelType.MODEL_SMALL);

            Feature feature = new Feature();
            feature.name = id;
            feature.feature = Feature;

            int result = faceRegister.addFeature(data.get("name"), feature);

        } else {
            bitmap = Utils.getImageFromAssetsFile(DefaultService.this, data.get("fileName"));

            Group group = new Group();
            group.name = groupName;
            group.modelType = ModelType.MODEL_SMALL;

            int jieguo = faceRegister.createGroup(group);

            Person person = new Person();
            person.name = data.get("name");

            faceRegister.addPerson(groupName, person);
            Image image = new Image();
            image.data = Utils.bitmap2RGB(bitmap);
            image.format = ImageFormat.RGB888;
            image.rotation = ImageRotation.ANGLE_0;
            image.height = bitmap.getHeight();
            image.width = bitmap.getWidth();
            faces = faceDetect.detectPicture(image);
            String Feature = faceRegister.extractFeature(image, faces[0], ModelType.MODEL_SMALL);

            Feature feature = new Feature();
            feature.name = id;
            feature.feature = Feature;

            int result = faceRegister.addFeature(data.get("name"), feature);

        }
    }

}
