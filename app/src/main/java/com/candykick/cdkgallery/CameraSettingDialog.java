package com.candykick.cdkgallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.candykick.cdkgallery.databinding.ActivityCameraSettingDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class CameraSettingDialog extends AppCompatActivity {

    ActivityCameraSettingDialogBinding binding;
    static final int REQUEST_SETTING = 10;
    int guideLineSelect = 0; //0: 없음, 1: 3x3 분할, 2: 4*4 분할, 3: 최대 크기 정사각형, 4: 명함

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera_setting_dialog);
        binding.setActivity(this);

        //데이터를 저장하게 되는 리스트
        List<String> guideList = new ArrayList<>();
        guideList.add("없음"); guideList.add("3X3 분할");
        guideList.add("4X4 분할"); guideList.add("정사각형");
        guideList.add("명함");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, guideList);
        binding.lvCameraSetting.setAdapter(adapter);
        binding.lvCameraSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                guideLineSelect = position;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isReversal", binding.cbCameraSetting.isChecked());
        intent.putExtra("guideline", guideLineSelect);
        setResult(REQUEST_SETTING,intent);
        finish();
    }
}