package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.Error;
import com.alibaba.cloud.faceengine.Face;
import com.alibaba.cloud.faceengine.FaceDetect;
import com.alibaba.cloud.faceengine.FaceEngine;
import com.alibaba.cloud.faceengine.FaceRecognize;
import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Feature;
import com.alibaba.cloud.faceengine.Group;
import com.alibaba.cloud.faceengine.Image;
import com.alibaba.cloud.faceengine.ImageFormat;
import com.alibaba.cloud.faceengine.ImageRotation;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;
import com.alibaba.cloud.faceengine.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class RegisterCameraActivity extends Activity {
    private static final String TAG = "AFE_" + RegisterCameraActivity.class.getSimpleName();

    private EditText edName, edFeatureId;
    private Spinner spinner;
    private ImageView ivPhoto;
    private Button btn;
    private FaceRegister mFaceRegister;
    private List<Group> mAllGroups;
    private List<String> mAllGroupNames;
    private TextView title;
    private static final int ALBUM_OK = 0;
    private Bitmap bitmap;
    private int status;
    private FaceDetect faceDetect;
    private Face[] faces;
    public static String RegisteredCameraTAG = "AFE_registered";
    private String filePath;
    private ImageButton btnBack;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeredphoto);
        initData();
        init();
        initView();
    }

    private void initView() {
        title.setText(R.string.register_camera);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterCameraActivity.this, R.layout.support_simple_spinner_dropdown_item, mAllGroupNames);
        spinner.setAdapter(adapter);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentIv = new Intent(RegisterCameraActivity.this, RecognizeCameraActivity.class);
                intentIv.putExtra("TAG", RegisteredCameraTAG);
                startActivityForResult(intentIv, ALBUM_OK);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllGroups == null || mAllGroups.size() == 0) {
                    Toast.makeText(RegisterCameraActivity.this, RegisterCameraActivity.this.getString(R.string.please_create_group_first), Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = edName.getText().toString();
                String Feature_ID = edFeatureId.getText().toString();
                if (name.equals("") || name == null || Feature_ID.equals("") || Feature_ID == null || bitmap == null) {
                    if (bitmap == null) {
                        Toast.makeText(RegisterCameraActivity.this, RegisterCameraActivity.this.getString(R.string.please_select_photo), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterCameraActivity.this, RegisterCameraActivity.this.getString(R.string.please_fill), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "register begin");
                    Person person = new Person();
                    person.name = name;
                    status = mFaceRegister.addPerson(mAllGroups.get(spinner.getSelectedItemPosition()).id, person);
                    if (status == Error.OK || status == Error.ERROR_EXISTED || status == Error.ERROR_CLOUD_EXISTED_ERROR) {
                        Image image = new Image();
                        image.data = Utils.bitmap2RGB(bitmap);
                        image.format = ImageFormat.RGB888;
                        image.rotation = ImageRotation.ANGLE_0;
                        image.height = bitmap.getHeight();
                        image.width = bitmap.getWidth();
                        faces = faceDetect.detectPicture(image);
                        String Feature = mFaceRegister.extractFeature(image, faces[0], mAllGroups.get(spinner.getSelectedItemPosition()).modelType);
                        Feature feature = new Feature();
                        feature.name = edName.getText().toString();
                        feature.feature = Feature;
                        int result = mFaceRegister.addFeature(person.id, feature);
                        Log.d(TAG, "register end");
                        if (result == 0) {
                            Toast.makeText(RegisterCameraActivity.this, RegisterCameraActivity.this.getString(R.string.add_success), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterCameraActivity.this, RegisterCameraActivity.this.getString(R.string.add_failure) + result, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(RegisterCameraActivity.this, RegisterCameraActivity.this.getString(R.string.add_failure) + status, Toast.LENGTH_LONG).show();
                    }
                    if (file != null) {
                        Utils.deleteFile(file);
                    }
                    RegisterCameraActivity.this.finish();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file != null) {
                    Utils.deleteFile(file);
                }
                RegisterCameraActivity.this.finish();
            }
        });
    }

    private void initData() {
        mFaceRegister = FaceRegister.createInstance();
        faceDetect = FaceDetect.createInstance(Mode.TERMINAL);
        mAllGroups = new ArrayList<Group>();
        Group[] allGroups = mFaceRegister.getAllGroups();
        mAllGroupNames = new ArrayList<String>();
        if (mAllGroups != null) {
            for (int i = 0; i < allGroups.length; i++) {
                if (allGroups[i].modelType == ModelType.MODEL_100K) {
                    if (FaceEngine.supportCloud()) {
                        mAllGroupNames.add(allGroups[i].name + " (100K)");
                        mAllGroups.add(allGroups[i]);
                    }
                } else {
                    mAllGroupNames.add(allGroups[i].name + " (3K)");
                    mAllGroups.add(allGroups[i]);
                }
            }
        }
    }

    private void init() {
        edFeatureId = (EditText) findViewById(R.id.feature_name);
        edName = (EditText) findViewById(R.id.person_name);
        spinner = (Spinner) findViewById(R.id.activity_registeredphoto_sp);
        ivPhoto = (ImageView) findViewById(R.id.activity_registeredphoto_photo);
        btn = (Button) findViewById(R.id.activity_registeredphoto_confirm);
        title = (TextView) findViewById(R.id.currency_tv_title);
        btnBack = (ImageButton) findViewById(R.id.currency_btn_back);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 0) {
                filePath = data.getStringExtra("data");
                file = new File(filePath);
                bitmap = getLoacalBitmap(filePath);
                ivPhoto.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
