package com.candykick.cdkgallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.candykick.cdkgallery.adapter.GalleryImageAdapter;
import com.candykick.cdkgallery.adapter.GalleryImageResultAdapter;
import com.candykick.cdkgallery.databinding.ActivityGalleryBinding;

import java.util.ArrayList;
import java.util.List;

/* 필요한 작업들
 * [0. 선택된 사진들 Uri나 Bitmap으로 넘기기(File로는 넘길 필요 없는 듯.)]
 * 1. 로딩 화면 넣기(이미지뷰+ProgressBar, 이미지뷰 및 ProgressBar VISIBLITY 조절 가능, 이미지뷰의 이미지 설정 가능)
 * 2. 필요없는 import 삭제
 * 3. 실행 전 권한 확인
 * 4. Activity가 메모리 문제나 오래 비활성화되는 등의 문제로 메모리 헤재된 경우 onResume 시 가지고 있는 내용 복구(근데 이 작업 굳이 필요함?)
 * 5. 카메라로 찍은 사진의 경우, 처음에 미리보기가 비어 있는 오류. 다음 사진이 추가되면 그 때는 정상적으로 보인다. (String이 아닌 File을 전송하면 되지 않을까...??)
 */

public class GalleryActivity extends AppCompatActivity implements CameraFragment.CameraListener, GalleryFragment.GalleryListener {

    ActivityGalleryBinding binding;

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_SETTING = 10;

    CameraFragment fragment1; GalleryFragment fragment2;

    ArrayList<String> resultImage = new ArrayList<>();
    //ArrayList<Bitmap> resultBitmap = new ArrayList<>();
    GalleryImageResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        binding.setActivity(this);

        if(getIntent().getIntExtra("start", 0) == 0) { //카메라 실행
            fragment1 = new CameraFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.containerGallery, fragment1).commit();
            TabLayout.Tab tab = binding.tlGallery.getTabAt(0);
            tab.select();
        } else if(getIntent().getIntExtra("start", 0) == 1) { //갤러리 실행
            fragment2 = new GalleryFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.containerGallery, fragment2).commit();
            TabLayout.Tab tab = binding.tlGallery.getTabAt(1);
            tab.select();
        }

        binding.tlGallery.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0: //카메라
                        fragment1 = new CameraFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerGallery, fragment1).commit();
                        break;
                    case 1: //갤러리
                        fragment2 = new GalleryFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.containerGallery, fragment2).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GalleryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvSelectedImage.setLayoutManager(linearLayoutManager);
        resultAdapter = new GalleryImageResultAdapter(GalleryActivity.this, resultImage);
        resultAdapter.setOnItemClickListener(new GalleryImageResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                resultImage.remove(pos);
                resultAdapter.notifyDataSetChanged();
            }
        });
        binding.rvSelectedImage.setAdapter(resultAdapter);
    }

    @Override
    public void receivedCameraImage(String path) {//CameraFragment로부터 이미지를 전달받은 경우
        resultImage.add(path);
        resultAdapter.notifyDataSetChanged();
    }
    @Override
    public void receivedCameraImage(Bitmap bitmap, Uri path) {}
    @Override
    public void receivedGalleryImage(String path) { //GalleryFragment로부터 이미지를 전달받은 경우
        resultImage.add(path);
        //resultBitmap.add(bitmap);
        resultAdapter.notifyDataSetChanged();
    }
    @Override
    public void receivedGalleryImage(Bitmap bitmap, Uri path) {}

    public void next() {
        Intent intent = new Intent(GalleryActivity.this, ImageModifyActivity.class);
        intent.putStringArrayListExtra("path", resultImage);
        //intent.putExtra("bitmaps", resultBitmap);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);

        if(request == REQUEST_SETTING) {
            fragment1.onActivityResult(request, result, data);
        }
    }
}
