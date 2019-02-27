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

import com.alibaba.cloud.faceengine.FaceRecognize;
import com.alibaba.cloud.faceengine.FaceRegister;
import com.alibaba.cloud.faceengine.Feature;
import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PersonActivity extends Activity {
    private TextView title, person_name;
    private RecyclerView rv;
    private String mPersonId;
    private Person mPerson;
    private Feature[] datas;
    private int mFeatureCount;
    private HashMap<String, String> data;
    private FeatureAdapter adapter;
    private FaceRegister faceRegister;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        faceRegister = FaceRegister.createInstance();
        initData();
        init();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mPersonId = intent.getStringExtra("personId");
        mPerson = faceRegister.getPerson(mPersonId);
        datas = mPerson.features;
        if (datas == null) {
            mFeatureCount = 0;
        } else {
            mFeatureCount = datas.length;
        }
    }

    private void initView() {
        title.setText(R.string.personal);
        person_name.setText(mPerson.name);
        rv.setLayoutManager(new LinearLayoutManager(PersonActivity.this, LinearLayout.VERTICAL, false));
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerItemDecoration(PersonActivity.this, LinearLayout.VERTICAL));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonActivity.this.finish();
            }
        });
    }

    private void init() {
        title = (TextView) findViewById(R.id.currency_tv_title);
        person_name = (TextView) findViewById(R.id.activity_personal_name);
        rv = (RecyclerView) findViewById(R.id.activity_personal_rv);
        btnBack = (ImageButton) findViewById(R.id.currency_btn_back);
        adapter = new FeatureAdapter(PersonActivity.this, datas);
    }
}
