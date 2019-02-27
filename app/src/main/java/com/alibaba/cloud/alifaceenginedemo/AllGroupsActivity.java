package com.alibaba.cloud.alifaceenginedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Group;

import java.util.ArrayList;
import java.util.List;

public class AllGroupsActivity extends Activity {
    public static final String TAG = "AFE_" + AllGroupsActivity.class.getSimpleName();

    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private GroupAdapter mGroupAdapter;

    private List<GroupAdapter.Group> mGroupAdapterGroups;
    private FaceRegister mFaceRegister;
    private ImageButton mBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        mFaceRegister = FaceRegister.createInstance();
        initData();
        init();
        initView();
    }

    private void initView() {
        mTitle.setText(R.string.all_groups);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllGroupsActivity.this, LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(mGroupAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(AllGroupsActivity.this, LinearLayout.VERTICAL));
        mGroupAdapter.setOnItemClickListener(new GroupAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, GroupAdapter.Group data) {
                Intent intent = new Intent(AllGroupsActivity.this, GroupActivity.class);
                intent.putExtra("groupId", data.group.id);
                intent.putExtra("groupName", data.group.name);
                intent.putExtra("modelType", data.group.modelType);
                startActivity(intent);
                AllGroupsActivity.this.finish();
            }
        });
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllGroupsActivity.this.finish();
            }
        });
    }

    private void initData() {
        Group[] groups = mFaceRegister.getAllGroups();
        if (groups == null) {
            Log.d(TAG, "getAllGroups size:0");
            return;
        }
        Log.d(TAG, "getAllGroups size:" + groups.length);
        for (int i = 0; i < groups.length; i++) {
            Log.d(TAG, "getAllGroups[" + i + "] : " + groups[i]);
        }

        mGroupAdapterGroups = new ArrayList<GroupAdapter.Group>();
        for (int i = 0; i < groups.length; i++) {
            GroupAdapter.Group group = new GroupAdapter.Group();
            group.group = groups[i];
            group.personNum = mFaceRegister.getPersonNum(groups[i].id);
            mGroupAdapterGroups.add(group);
        }
    }

    private void init() {
        mTitle = (TextView) findViewById(R.id.currency_tv_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_groups_rv);
        mGroupAdapter = new GroupAdapter(AllGroupsActivity.this, mGroupAdapterGroups);
        mBtnBack = (ImageButton) findViewById(R.id.currency_btn_back);
    }
}
