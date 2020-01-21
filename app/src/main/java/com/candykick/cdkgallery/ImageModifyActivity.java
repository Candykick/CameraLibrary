package com.candykick.cdkgallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.candykick.cdkgallery.adapter.ImageViewSimpleAdapter;
import com.candykick.cdkgallery.adapter.GalleryImageResultAdapter;
import com.candykick.cdkgallery.databinding.ActivityImageModifyBinding;

import java.util.ArrayList;

public class ImageModifyActivity extends AppCompatActivity {

    ActivityImageModifyBinding binding;

    ArrayList<String> resultImage = new ArrayList<>();
    ArrayList<Bitmap> resultBitmap = new ArrayList<>();
    ImageViewSimpleAdapter vpAdapter;

    public static final int REQUEST_CROP = 20;
    public static final int REQUEST_ROTATION = 21;
    public static final int REQUEST_DRAWING = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_modify);
        binding.setActivity(this);

        resultImage = getIntent().getStringArrayListExtra("path");
        //resultBitmap = getIntent().getParcelableArrayListExtra("bitmaps");

        for(int i=0;i<resultImage.size();i++) {
            resultBitmap.add(BitmapFactory.decodeFile(resultImage.get(i)));
        }

        vpAdapter = new ImageViewSimpleAdapter(ImageModifyActivity.this, resultBitmap);
        binding.vpModifyImageList.setAdapter(vpAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ImageModifyActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvModifyImageList.setLayoutManager(linearLayoutManager);
        GalleryImageResultAdapter rvAdapter = new GalleryImageResultAdapter(ImageModifyActivity.this, resultImage);
        rvAdapter.setOnItemClickListener(new GalleryImageResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                binding.vpModifyImageList.setCurrentItem(pos);
            }
        });
        binding.rvModifyImageList.setAdapter(rvAdapter);
    }

    public void crop() {
        Intent intent = new Intent(ImageModifyActivity.this, CropActivity.class);
        intent.putExtra("image",resultImage.get(binding.vpModifyImageList.getCurrentItem()));
        startActivityForResult(intent, REQUEST_CROP);
    }
    public void rotate() {
        //여기서는 이미지뷰를 회전시키기만 하고, 최종적으로 저장할 때 회전된 상태로 저장 후 내보낸다.
        /*Matrix matrix = new Matrix();

        vpAdapter.getSelectedImageView(binding.vpModifyImageList.getCurrentItem()).setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate(90f);
        vpAdapter.getSelectedImageView(binding.vpModifyImageList.getCurrentItem()).setImageMatrix(matrix);*/

        //비트맵 객체를 직접 회전시킴
        int position = binding.vpModifyImageList.getCurrentItem();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap.get(position), resultBitmap.get(position).getWidth(), resultBitmap.get(position).getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        //resultBitmap.remove(position);
        resultBitmap.set(position, rotatedBitmap);
        vpAdapter.notifyDataSetChanged();
    }
    public void drawing() {
        Intent intent = new Intent(ImageModifyActivity.this, DrawingActivity.class);
        intent.putExtra("image",resultImage.get(binding.vpModifyImageList.getCurrentItem()));
        intent.putExtra("position",binding.vpModifyImageList.getCurrentItem());
        startActivityForResult(intent, REQUEST_DRAWING);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);

        if(request == REQUEST_DRAWING && result == RESULT_OK) {
            int current = data.getIntExtra("position",0);

            resultImage.set(current, data.getStringExtra("path"));
            resultBitmap.set(current, BitmapFactory.decodeFile(resultImage.get(current)));
            vpAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() { //뒤로 가기를 누른 경우, GalleryActivity의 GalleryFragment를 띄운다. 아직 처리 안 된 부분.
        super.onBackPressed();
    }
}