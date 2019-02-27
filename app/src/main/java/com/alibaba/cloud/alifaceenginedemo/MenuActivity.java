package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MenuActivity extends Activity implements View.OnClickListener {
    public static String TAG = "AFE_MenuActivity";

    private TextView tv_title;
    private Button btnCreateGroup, btnFacelibrary, btnRegisterPhoto, btnRegisterCamera,
            btnRecognitionPhoto, btnRecognitionCamera, btnContrastPhoto, btnContrastCamera;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.currency_tv_title);
        tv_title.setText(this.getString(R.string.menu_title));
        btnCreateGroup = (Button) findViewById(R.id.activity_menu_btn_creategroup);
        btnFacelibrary = (Button) findViewById(R.id.activity_menu_btn_facelibrary);
        btnRegisterPhoto = (Button) findViewById(R.id.activity_menu_btn_registerphoto);
        btnRegisterCamera = (Button) findViewById(R.id.activity_menu_btn_registercamera);
        btnRecognitionCamera = (Button) findViewById(R.id.activity_menu_btn_recognition_camera);
        btnRecognitionPhoto = (Button) findViewById(R.id.activity_menu_btn_recognition_photo);
        btnContrastCamera = (Button) findViewById(R.id.activity_menu_btn_contrast_camera);
        btnContrastPhoto = (Button) findViewById(R.id.activity_menu_btn_contrast_photo);
        btnBack = (ImageButton) findViewById(R.id.currency_btn_back);
        btnCreateGroup.setOnClickListener(this);
        btnFacelibrary.setOnClickListener(this);
        btnRegisterPhoto.setOnClickListener(this);
        btnRegisterCamera.setOnClickListener(this);
        btnRecognitionCamera.setOnClickListener(this);
        btnRecognitionPhoto.setOnClickListener(this);
        btnContrastCamera.setOnClickListener(this);
        btnContrastPhoto.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_menu_btn_creategroup:
                CreateGroupDialog dialog = new CreateGroupDialog(MenuActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case R.id.activity_menu_btn_facelibrary:
                Intent intentfacelibrary = new Intent(MenuActivity.this, AllGroupsActivity.class);
                startActivity(intentfacelibrary);
                break;
            case R.id.activity_menu_btn_registerphoto:
                Intent intentRegisterphoto = new Intent(MenuActivity.this, RegisterPhotoActivity.class);
                startActivity(intentRegisterphoto);
                break;
            case R.id.activity_menu_btn_registercamera:
                Intent intentRegistercamera = new Intent(MenuActivity.this, RegisterCameraActivity.class);
                startActivity(intentRegistercamera);
                break;
            case R.id.activity_menu_btn_recognition_photo:
                Intent intentrecognizephoto = new Intent(MenuActivity.this, RecognizePhotoActivity.class);
                startActivity(intentrecognizephoto);
                break;
            case R.id.activity_menu_btn_recognition_camera:
                Intent intentRecognitionCamera = new Intent(MenuActivity.this, RecognizeCameraActivity.class);
                intentRecognitionCamera.putExtra("TAG", "0");
                startActivity(intentRecognitionCamera);
                break;
            case R.id.activity_menu_btn_contrast_photo:
                Intent intentcontractphoto = new Intent(MenuActivity.this, VerifyPhotoActivity.class);
                startActivity(intentcontractphoto);
                break;
            case R.id.activity_menu_btn_contrast_camera:
                Intent intentContract = new Intent(MenuActivity.this, VerifyCameraActivity.class);
                startActivity(intentContract);
                break;
            case R.id.currency_btn_back:
                MenuActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
