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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.Mode;

public class SetRunModeDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    private static String TAG = "AFE_" + SetRunModeDialog.class.getSimpleName();

    private Context mContext;
    private Button mBtnCancel, mBtnConfirm;
    private RadioGroup mCtrlModes;
    private RadioButton mCtrModeTerminal, mCtrModeCloud;

    public SetRunModeDialog(@NonNull Context context) {
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
        View view = layoutInflater.inflate(R.layout.set_run_mode, null);
        setContentView(view);
        mBtnCancel = (Button) view.findViewById(R.id.cancel);
        mBtnConfirm = (Button) view.findViewById(R.id.confirm);
        mCtrlModes = (RadioGroup) view.findViewById(R.id.radio_group);
        mCtrlModes.setOnCheckedChangeListener(this);
        mCtrModeTerminal = (RadioButton) view.findViewById(R.id.radio_mode_terminal);
        mCtrModeCloud = (RadioButton) view.findViewById(R.id.radio_mode_cloud);
        mBtnCancel.setOnClickListener(new clickListener());
        mBtnConfirm.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); //获取屏幕宽、高
        lp.width = (int) (d.widthPixels * 0.8);
        dialogWindow.setAttributes(lp);

        int mode = SPUtils.getRunMode(mContext);
        if (mode == Mode.TERMINAL) {
            mCtrModeTerminal.setChecked(true);
        } else if (mode == Mode.CLOUD) {
            mCtrModeCloud.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radio_mode_terminal:
                //SPUtils.put(mContext, SPUtils.KEY_RUN_MODE, Mode.TERMINAL);
                break;
            case R.id.radio_mode_cloud:
                //SPUtils.put(mContext, SPUtils.KEY_RUN_MODE, Mode.CLOUD);
                break;
            default:
                break;
        }
    }

    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == mBtnCancel) {
                dismiss();
            } else if (v == mBtnConfirm) {
                if (!mCtrModeTerminal.isChecked() && !mCtrModeCloud.isChecked()) {
                    Toast.makeText(mContext, mContext.getString(R.string.dialog_message_type), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mCtrModeTerminal.isChecked()) {
                    SPUtils.put(mContext, SPUtils.KEY_RUN_MODE, Mode.TERMINAL);
                } else if (mCtrModeCloud.isChecked()) {
                    if (!SPUtils.getUseCloud(SetRunModeDialog.this.mContext)) {
                        Toast.makeText(mContext, mContext.getString(R.string.please_set_cloud_face_database), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SPUtils.put(mContext, SPUtils.KEY_RUN_MODE, Mode.CLOUD);
                }

                dismiss();
            }
        }
    }
}
