package com.alibaba.cloud.alifaceenginedemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.FaceEngine;

public class AuthDialog extends Dialog {
    private static String TAG = "AFE_" + AuthDialog.class.getSimpleName();

    private Context mContext;
    private Button mBtnCancel, mBtnConfirm;
    private EditText mCtrlAuthKey;

    public AuthDialog(@NonNull Context context) {
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
        View view = layoutInflater.inflate(R.layout.auth_dialog, null);
        setContentView(view);
        mCtrlAuthKey = (EditText) view.findViewById(R.id.auth_key);
        mCtrlAuthKey.setText(SPUtils.getAuthKey(mContext));
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_auth);
        mBtnCancel.setOnClickListener(new clickListener());
        mBtnConfirm.setOnClickListener(new clickListener());

        //设置dialog属性
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); //获取屏幕宽、高
        lp.width = (int) (d.widthPixels * 0.8); //设置宽度
        dialogWindow.setAttributes(lp);
    }

    //点击事件
    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    dismiss();
                    break;
                case R.id.btn_auth:
                    if (mCtrlAuthKey.getText().toString().trim().length() == 0) {
                        Toast.makeText(mContext,
                                mContext.getString(R.string.dialog_message_auth_key_is_empty),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String key = mCtrlAuthKey.getText().toString().trim();
                    int status = FaceEngine.authorize(key);

                    if (status == 0) {
                        WelcomeActivity.AuthSuccess = true;
                        SPUtils.setAuthKey(mContext, mCtrlAuthKey.getText().toString().trim());
                        Toast.makeText(mContext,
                                mContext.getString(R.string.dialog_message_auth_success),
                                Toast.LENGTH_LONG).show();
                        dismiss();
                    } else {
                        WelcomeActivity.AuthSuccess = false;
                        Toast.makeText(mContext,
                                mContext.getString(R.string.dialog_message_auth_fail) + " : " + status,
                                Toast.LENGTH_LONG).show();
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
