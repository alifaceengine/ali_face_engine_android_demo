package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.FaceEngine;

import java.io.File;

public class WelcomeActivity extends Activity implements View.OnClickListener {
    public static boolean AuthSuccess = false;

    private Button btnChun, btnCloud;
    private Button mBtnSetMode;
    private Button mBtnAuth;
    public static String TAG = "AFE_WelcomeActivity";
    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);
        init();
        int mPermission = ContextCompat.checkSelfPermission(getApplication(), android.Manifest.permission.CAMERA);
        int myPermission = ContextCompat.checkSelfPermission(getApplication(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (mPermission == PackageManager.PERMISSION_GRANTED
                && myPermission == PackageManager.PERMISSION_GRANTED) {
            Initialization();
        } else {
            //若没有授权，会弹出一个对话框
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA}, 1);
        }
    }

    private void Initialization() {
        filePath = Utils.filePath;
        Log.d(TAG, "FaceEngine.getVersion:" + FaceEngine.getVersion());

        if (!SPUtils.hasAuthKey(this)) {
            SPUtils.setAuthKey(this, "eyJ2ZW5kb3JJZCI6ImNlc2hpX3ZlbmRvciIsInJvbGUiOjIsImNvZGUiOiJBNEU1QzZCNkMxQkY4RkZENjgwRTY2NkIzMkIxNjI2RSIsImV4cGlyZSI6IjIwMTkwODMxIiwidHlwZSI6MX0=");
        }

        int status = FaceEngine.authorize(SPUtils.getAuthKey(this));
        if (status != 0) {
            toastAuthFail();
        } else {
            AuthSuccess = true;
        }

        FaceEngine.enableDebug(true);

        if (isFile(filePath)) {
            FaceEngine.setPersistencePath(filePath);
        }

        boolean useCloud = (Boolean) SPUtils.get(this, SPUtils.KEY_USE_CLOUD, SPUtils.DEFAULT_VALUE_USE_CLOUD);
        if (useCloud) {
            FaceEngine.setCloudAddr(SPUtils.get(this, SPUtils.KEY_CLOUD_IP, "101.132.89.177").toString(),
                    Integer.parseInt(SPUtils.get(this, SPUtils.KEY_CLOUD_PORT, "15004").toString()));
            FaceEngine.setCloudLoginAccount(SPUtils.get(this, SPUtils.KEY_CLOUD_USERNAME, "user_register").toString(),
                    SPUtils.get(this, SPUtils.KEY_CLOUD_USERPSW, "666666").toString());
        } else {
            FaceEngine.setCloudAddr("", 0);
            FaceEngine.setCloudLoginAccount("", "");
        }
    }

    private boolean isFile(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void init() {
        btnChun = (Button) findViewById(R.id.activity_wel_btn1);
        btnCloud = (Button) findViewById(R.id.activity_wel_btn2);
        mBtnSetMode = (Button) findViewById(R.id.activity_wel_btn3);
        mBtnAuth = (Button) findViewById(R.id.btn_auth);
        btnChun.setOnClickListener(this);
        btnCloud.setOnClickListener(this);
        mBtnSetMode.setOnClickListener(this);
        mBtnAuth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_wel_btn1:
                if (!AuthSuccess) {
                    toastAuthFail();
                    return;
                }

                Intent intent = new Intent(WelcomeActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_wel_btn2: {
                //弹窗显示
                SettingDialog dialog = new SettingDialog(WelcomeActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            break;
            case R.id.activity_wel_btn3: {
                //弹窗显示
                SetRunModeDialog dialog = new SetRunModeDialog(WelcomeActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            break;
            case R.id.btn_auth: {
                AuthDialog dialog = new AuthDialog(WelcomeActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            default:
                break;
        }
    }

    private void toastAuthFail() {
        Toast.makeText(this,
                this.getString(R.string.dialog_message_auth_fail),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    System.exit(0);
                }
            }
            Initialization();
        }

    }
}
