package com.candykick.cdkgallery;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.candykick.cdkgallery.adapter.GalleryImageAdapter;
import com.candykick.cdkgallery.databinding.ActivityTmpBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TmpActivity extends AppCompatActivity {

    ActivityTmpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tmp);
        binding.setActivity(this);
    }
}
