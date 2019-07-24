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

import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Group;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;

public class CreateGroupDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    private static String TAG = "AFE_CreateGroupDialog";

    private Context mContext;
    private Button mBtnCancel, mBtnConfirm;
    private EditText mGroupNameCtrl;
    private RadioGroup mGroupTypeCtrl;
    private FaceRegister mFaceRegister;
    private int mModelType;
    private RadioButton mModelBigCtrl, mModelSmallCtrl;

    public CreateGroupDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /*
       初始化组件
     */
    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.create_group, null);
        setContentView(view);
        mGroupNameCtrl = (EditText) view.findViewById(R.id.input_group_name);
        mBtnCancel = (Button) view.findViewById(R.id.dialog_wel_btn_cancel);
        mBtnConfirm = (Button) view.findViewById(R.id.dialog_wel_btn_confirm);
        mGroupTypeCtrl = (RadioGroup) view.findViewById(R.id.dialog_rg);
        mGroupTypeCtrl.setOnCheckedChangeListener(this);
        mModelBigCtrl = (RadioButton) view.findViewById(R.id.dialog_radio_big);
        mModelSmallCtrl = (RadioButton) view.findViewById(R.id.dialog_radio_small);
        mBtnCancel.setOnClickListener(new clickListener());
        mBtnConfirm.setOnClickListener(new clickListener());

        //设置dialog属性
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); //获取屏幕宽、高
        lp.width = (int) (d.widthPixels * 0.8); //设置宽度
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.dialog_radio_big:
                mModelType = ModelType.MODEL_100K;
                break;
            case R.id.dialog_radio_small:
                mModelType = ModelType.MODEL_3K;
                break;
            default:
                break;
        }
    }

    //点击事件
    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_wel_btn_cancel:
                    dismiss();
                    break;
                case R.id.dialog_wel_btn_confirm:
                    if (!mModelBigCtrl.isChecked() && !mModelSmallCtrl.isChecked()) {
                        Toast.makeText(mContext, mContext.getString(R.string.dialog_message_type), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mGroupNameCtrl.getText().toString().trim().length() == 0) {
                        Toast.makeText(mContext, mContext.getString(R.string.dialog_message_group_name), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (mModelBigCtrl.isChecked()) {
                        if (SPUtils.getRunMode(CreateGroupDialog.this.mContext) == Mode.TERMINAL) {
                            Toast.makeText(mContext, mContext.getString(R.string.please_enable_cloud_run_mode),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    if (mFaceRegister == null) {
                        mFaceRegister = FaceRegister.createInstance();
                    }

                    Group group = new Group();
                    group.name = mGroupNameCtrl.getText().toString().trim();
                    group.modelType = mModelType;

                    int status = mFaceRegister.createGroup(group);
                    if (status == 0) {
                        Toast.makeText(mContext, mGroupNameCtrl.getText().toString() + mContext.getString(R.string.dialog_message_success), Toast.LENGTH_LONG).show();
                        dismiss();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.dialog_message_failure) + status, Toast.LENGTH_LONG).show();
                        dismiss();
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
