package com.alibaba.cloud.alifaceenginedemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.FaceEngine;
import com.alibaba.cloud.faceengine.Mode;

public class SettingDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "AFE_" + SettingDialog.class.getSimpleName();
    private Context mContext;
    private RadioGroup mRadioGroup;
    private RadioButton mCtrlUseCloud, mCtrlNotUseCloud;
    private EditText mCtrlIP;
    private EditText mCtrlPort;
    private EditText mCtrlUserName;
    private EditText mCtrlUserPsw;
    private Button mBtnCancel, mBtnConfirm;

    public SettingDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.settings, null);
        setContentView(view);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.use_cloud);
        mCtrlUseCloud = (RadioButton) view.findViewById(R.id.use_cloud_use);
        mCtrlNotUseCloud = (RadioButton) view.findViewById(R.id.use_cloud_not_use);
        mCtrlIP = (EditText) view.findViewById(R.id.dialog_wel_et_ip);
        mCtrlPort = (EditText) view.findViewById(R.id.dialog_wel_et_pot);
        mCtrlUserName = (EditText) view.findViewById(R.id.dialog_wel_et_username);
        mCtrlUserPsw = (EditText) view.findViewById(R.id.dialog_wel_et_userpass);


        mRadioGroup.setOnCheckedChangeListener(this);

        boolean useCloud = (Boolean) SPUtils.get(mContext, SPUtils.KEY_USE_CLOUD, SPUtils.DEFAULT_VALUE_USE_CLOUD);
        if (useCloud) {
            mCtrlUseCloud.setChecked(true);
            mCtrlIP.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_IP, "101.132.89.177").toString());
            mCtrlPort.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_PORT, "15005").toString());
            mCtrlUserName.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_USERNAME, "user_register").toString());
            mCtrlUserPsw.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_USERPSW, "666666").toString());
        } else {
            mCtrlNotUseCloud.setChecked(true);
            mCtrlIP.setText("");
            mCtrlPort.setText("");
            mCtrlUserName.setText("");
            mCtrlUserPsw.setText("");
        }

        mBtnCancel = (Button) view.findViewById(R.id.dialog_wel_btn_cancel);
        mBtnConfirm = (Button) view.findViewById(R.id.dialog_wel_btn_confirm);
        mBtnCancel.setOnClickListener(new clickListener());
        mBtnConfirm.setOnClickListener(new clickListener());


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); //获取屏幕宽、高
        lp.width = (int) (d.widthPixels * 0.9); //设置宽度
        // lp.height = (int) (d.heightPixels * 0.5);//设置高度
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.use_cloud_use:
                mCtrlIP.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_IP, "101.132.89.177").toString());
                mCtrlPort.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_PORT, "15005").toString());
                mCtrlUserName.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_USERNAME, "user_register").toString());
                mCtrlUserPsw.setText(SPUtils.get(mContext, SPUtils.KEY_CLOUD_USERPSW, "666666").toString());
                break;
            case R.id.use_cloud_not_use:
                mCtrlIP.setText("");
                mCtrlPort.setText("");
                mCtrlUserName.setText("");
                mCtrlUserPsw.setText("");
                break;
            default:
                break;
        }
    }

    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_wel_btn_cancel:
                    dismiss();
                    break;
                case R.id.dialog_wel_btn_confirm:
                    if (mCtrlUseCloud.isChecked()) {
                        if (mCtrlIP.getText().toString().trim().length() == 0) {
                            Toast.makeText(mContext, mContext.getString(R.string.dialog_message_ip_empty), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mCtrlPort.getText().toString().trim().length() == 0) {
                            Toast.makeText(mContext, mContext.getString(R.string.dialog_message_port_empty), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mCtrlUserName.getText().toString().trim().length() == 0) {
                            Toast.makeText(mContext, mContext.getString(R.string.dialog_message_username_empty), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mCtrlUserPsw.getText().toString().trim().length() == 0) {
                            Toast.makeText(mContext, mContext.getString(R.string.dialog_message_userpsw_empty), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SPUtils.put(mContext, SPUtils.KEY_USE_CLOUD, true);
                        FaceEngine.setCloudAddr(mCtrlIP.getText().toString().trim(), Integer.parseInt(mCtrlPort.getText().toString().trim()));
                        FaceEngine.setCloudLoginAccount(mCtrlUserName.getText().toString().trim(), mCtrlUserPsw.getText().toString().trim());

                        SPUtils.put(SettingDialog.this.mContext, SPUtils.KEY_CLOUD_IP, mCtrlIP.getText().toString().trim());
                        SPUtils.put(SettingDialog.this.mContext, SPUtils.KEY_CLOUD_PORT, mCtrlPort.getText().toString().trim());
                        SPUtils.put(SettingDialog.this.mContext, SPUtils.KEY_CLOUD_USERNAME, mCtrlUserName.getText().toString().trim());
                        SPUtils.put(SettingDialog.this.mContext, SPUtils.KEY_CLOUD_USERPSW, mCtrlUserPsw.getText().toString().trim());
                    } else {
                        SPUtils.put(mContext, SPUtils.KEY_USE_CLOUD, false);
                        FaceEngine.setCloudAddr("", 0);
                        FaceEngine.setCloudLoginAccount("", "");
                    }

                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
