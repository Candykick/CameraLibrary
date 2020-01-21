package com.candykick.cdkgallery;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.candykick.cdkgallery.base.CroppingView;
import com.candykick.cdkgallery.base.CroppingViewEx;
import com.candykick.cdkgallery.databinding.ActivityCropBinding;

/*
 * 자르기 옵션
 * 자유롭게, 1:1 원형(프로필), 1:1 사각형, 9:5 명함, 사이즈입력
 */

public class CropActivity extends AppCompatActivity {

    ActivityCropBinding binding;

    public static int SET_CUSTOM_RATIO = 1996;

    private String path;
    //CroppingView croppingView;
    CroppingViewEx croppingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop);
        binding.setActivity(this);

        path = getIntent().getStringExtra("image");

        //Glide.with(this).load(path).into(binding.ivCropImage);

        croppingView = new CroppingViewEx(CropActivity.this, path);
        croppingView.setRatio(false);
        //croppingView.setBackground(BitmapFactory.decodeFile(path));
        binding.ctCroppingView.addView(croppingView);
    }

    public void cropFree() {
        croppingView.setRatio(false);
    }
    public void cropProfileCircle() {
        croppingView.setRatio(true, 1, 1, true);
    }
    public void cropProfileRect() {
        croppingView.setRatio(true, 1, 1);
    }
    public void cropNameCard() {
        croppingView.setRatio(true, 9, 5);
    }
    public void cropRatio() {
        Intent intent = new Intent(CropActivity.this, RatioDialog.class);
        startActivityForResult(intent, SET_CUSTOM_RATIO);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);

        if(request == SET_CUSTOM_RATIO && result == RESULT_OK) {
            int xRatio = data.getIntExtra("x", 1);
            int yRatio = data.getIntExtra("y", 1);
            croppingView.setRatio(true, xRatio, yRatio);
        } else if(request == SET_CUSTOM_RATIO) {
            Toast.makeText(CropActivity.this, getResources().getString(R.string.crop_ratio_cancel), Toast.LENGTH_SHORT).show();
        }
    }
}