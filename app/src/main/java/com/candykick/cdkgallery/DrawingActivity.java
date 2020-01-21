package com.candykick.cdkgallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.candykick.cdkgallery.adapter.DrawingColorAdapter;
import com.candykick.cdkgallery.base.DrawingImageView;
import com.candykick.cdkgallery.databinding.ActivityDrawingBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

//https://androphil.tistory.com/370?category=423967(저장)

public class DrawingActivity extends AppCompatActivity {

    ActivityDrawingBinding binding;

    String path; int position;
    DrawingImageView drawingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drawing);
        binding.setActivity(this);

        path = getIntent().getStringExtra("image");
        position = getIntent().getIntExtra("position",0);

        drawingImageView = new DrawingImageView(DrawingActivity.this, path);
        binding.ctDrawing.addView(drawingImageView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DrawingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvDrawingColor.setLayoutManager(linearLayoutManager);
        DrawingColorAdapter colorAdapter = new DrawingColorAdapter(DrawingActivity.this);
        colorAdapter.setOnItemClickListener(new DrawingColorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int color) {
                drawingImageView.setPenColor(color);
            }
        });
        binding.rvDrawingColor.setAdapter(colorAdapter);
    }

    public void setPen() {
        binding.rvDrawingColor.setVisibility(View.VISIBLE);
        drawingImageView.setPencil(true);
    }
    public void setEraser() {
        binding.rvDrawingColor.setVisibility(View.GONE);
        drawingImageView.setPencil(false);
    }
    public void undo() {
        binding.rvDrawingColor.setVisibility(View.GONE);
        drawingImageView.undo();
    }
    public void redo() {
        binding.rvDrawingColor.setVisibility(View.GONE);
        drawingImageView.redo();
    }
    public void deleteAll() {
        binding.rvDrawingColor.setVisibility(View.GONE);
        drawingImageView.deleteAll();
    }
    public void savePicture() {
        // 1. 캐쉬(Cache)를 허용시킨다.
        // 2. 그림을 Bitmap 으로 저장.
        // 3. 캐쉬를 막는다.
        drawingImageView.setDrawingCacheEnabled(true);    // 캐쉬허용
        // 캐쉬에서 가져온 비트맵을 복사해서 새로운 비트맵(스크린샷) 생성
        Bitmap screenshot = Bitmap.createBitmap(drawingImageView.getDrawingCache());
        drawingImageView.setDrawingCacheEnabled(false);   // 캐쉬닫기

        // SDCard(ExternalStorage) : 외부저장공간
        // 접근하려면 반드시 AndroidManifest.xml에 권한 설정을 한다.
        //File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // 폴더가 있는지 확인 후 없으면 새로 만들어준다.
        //if(!dir.exists())
            //dir.mkdirs();
        FileOutputStream fos;
        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
            File file = new File(Environment.getExternalStorageDirectory()+"/DCIM", dateFormat.format(date)+".jpg");
            fos = new FileOutputStream(file);
            screenshot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.putExtra("path", file.getAbsolutePath());
            setResult(RESULT_OK,intent);
            finish();
        } catch (Exception e) {
            Log.e("phoro","그림저장오류",e);
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
