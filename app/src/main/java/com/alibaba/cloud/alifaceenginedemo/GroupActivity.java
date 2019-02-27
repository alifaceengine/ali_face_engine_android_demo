package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Group;
import com.alibaba.cloud.faceengine.ModelType;
import com.alibaba.cloud.faceengine.Person;

public class GroupActivity extends Activity {
    private static final String TAG = "AFE_" + GroupActivity.class.getSimpleName();

    private TextView tvPeopleNum, title, tvGroupName, mModelTypeCtrl;
    private RecyclerView recycler;
    private PersonAdapter mPersonAdapter;
    private String mGroupId;
    private String mGroupName;
    private int mModelType;
    private Person[] mPersons;
    private int mPersonNum;
    private Button btn_delete, btn_modify;
    private FaceRegister mFaceRegister;
    private ImageButton btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mFaceRegister = FaceRegister.createInstance();
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");
        mGroupName = intent.getStringExtra("groupName");
        mModelType = intent.getIntExtra("modelType", 1);

        mPersons = mFaceRegister.getAllPersons(mGroupId);
        if (mPersons != null) {
            mPersonNum = mPersons.length;
        } else {
            mPersonNum = 0;
        }
        Log.d(TAG, "mGroupId:" + mGroupId + " mGroupName:" + mGroupName + " personNum:" + mPersonNum);
    }

    private void initView() {
        tvGroupName = (TextView) findViewById(R.id.activity_facelibrary_groupname);
        tvPeopleNum = (TextView) findViewById(R.id.activity_facelibrary_people_num);
        mModelTypeCtrl = (TextView) findViewById(R.id.model_type);
        recycler = (RecyclerView) findViewById(R.id.activity_facelibrary_ry);
        title = (TextView) findViewById(R.id.currency_tv_title);
        btn_delete = (Button) findViewById(R.id.activity_facelibrary_delete);
        btn_modify = (Button) findViewById(R.id.activity_facelibrary_modify);
        btn_back = (ImageButton) findViewById(R.id.currency_btn_back);

        title.setText(R.string.face_library_title);
        tvGroupName.setText(mGroupName);
        tvPeopleNum.setText(mPersonNum + this.getString(R.string.face_library_peolpe));
        if (mModelType == ModelType.MODEL_BIG) {
            mModelTypeCtrl.setText(R.string.big);
        } else if (mModelType == ModelType.MODEL_SMALL) {
            mModelTypeCtrl.setText(R.string.small);
        }

        mPersonAdapter = new PersonAdapter(GroupActivity.this, mPersons);
        mPersonAdapter.setOnItemClickListener(new PersonAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, Person data) {
                Intent intent = new Intent(GroupActivity.this, PersonActivity.class);
                intent.putExtra("personId", data.id);
                startActivity(intent);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(GroupActivity.this, LinearLayout.VERTICAL, false));
        recycler.setAdapter(mPersonAdapter);
        recycler.addItemDecoration(new DividerItemDecoration(GroupActivity.this, LinearLayout.VERTICAL));
        recycler.setHasFixedSize(true);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogModify();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete();

            }
        });
        mPersonAdapter.setOnItemLongClickListener(new PersonAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, int position) {
                dialogDeletePerson(mPersons[position].name, position);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupActivity.this.finish();
            }
        });
    }

    private void dialogModify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        builder.setIcon(R.drawable.dialog_icon);
        builder.setTitle(R.string.enter_new_group_name);
        View view = View.inflate(GroupActivity.this, R.layout.dialog_modify, null);
        builder.setView(view);
        final TextView tv_old = (TextView) view.findViewById(R.id.dialog_modify_tv_old);
        final EditText ed_new = (EditText) view.findViewById(R.id.dialog_modify_ed_new);
        tv_old.setText(mGroupName);
        builder.setPositiveButton(this.getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_name = ed_new.getText().toString();
                if (new_name.equals("") || new_name == null) {
                    Toast.makeText(GroupActivity.this, GroupActivity.this.getString(R.string.dialog_message_group_name), Toast.LENGTH_SHORT).show();
                } else {
                    Group group = new Group();
                    group.id = mGroupId;
                    group.name = new_name;
                    int result = mFaceRegister.updateGroup(mGroupId, group);
                    if (result == 0) {
                        Toast.makeText(GroupActivity.this, GroupActivity.this.getString(R.string.dialog_message_amend_success), Toast.LENGTH_SHORT).show();
                        tvGroupName.setText(new_name);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(GroupActivity.this,
                                GroupActivity.this.getString(R.string.dialog_message_amend_failure) + Utils.getError(result),
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

            }
        });
        builder.setNegativeButton(GroupActivity.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void dialogDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        builder.setIcon(R.drawable.dialog_icon);
        builder.setTitle(R.string.system_message);
        builder.setMessage(R.string.dialog_message_delete_group);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int status = mFaceRegister.deleteGroup(mGroupId);
                if (status == 0) {
                    Toast.makeText(GroupActivity.this, R.string.dialog_message_delete_success, Toast.LENGTH_LONG).show();
                    GroupActivity.this.finish();
                } else {
                    Toast.makeText(GroupActivity.this, GroupActivity.this.getString(R.string.dialog_message_delete_failure) + Utils.getError(status), Toast.LENGTH_LONG).show();

                }
            }
        });
        builder.show();
    }

    private void dialogDeletePerson(final String personName, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        builder.setIcon(R.drawable.dialog_icon);
        builder.setTitle(R.string.system_message);
        builder.setMessage(GroupActivity.this.getString(R.string.dialog_message_delete_person) + personName);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int status = mFaceRegister.deletePerson(personName);
                if (status == 0) {
                    mPersonAdapter.notifyDataSetChanged();
                    tvPeopleNum.setText(mFaceRegister.getPersonNum(mGroupId) + GroupActivity.this.getString(R.string.face_library_peolpe));
                    Toast.makeText(GroupActivity.this, GroupActivity.this.getString(R.string.dialog_message_delete_success), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GroupActivity.this, GroupActivity.this.getString(R.string.dialog_message_delete_failure) + status, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }
}
