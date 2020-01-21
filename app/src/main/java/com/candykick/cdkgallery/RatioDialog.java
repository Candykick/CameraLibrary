package com.candykick.cdkgallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.candykick.cdkgallery.databinding.ActivityRatioDialogBinding;

import static com.candykick.cdkgallery.CropActivity.SET_CUSTOM_RATIO;

public class RatioDialog extends AppCompatActivity {

    ActivityRatioDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ratio_dialog);
        binding.setActivity(this);
    }

    public void okay() {
        Intent intent = new Intent();
        intent.putExtra("x", Integer.parseInt(binding.etRatio1.getText().toString()));
        intent.putExtra("y", Integer.parseInt(binding.etRatio2.getText().toString()));
        setResult(RESULT_OK,intent);
        finish();
    }
}
