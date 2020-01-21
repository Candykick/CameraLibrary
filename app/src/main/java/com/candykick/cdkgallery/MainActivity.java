package com.candykick.cdkgallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;

import com.candykick.cdkgallery.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final int PICK_FROM_ALBUM = 11198, PICK_FROM_CAMERA = 11199;
    private File tempFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
    }

    public void start() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
        builder.setTitle("이미지를 어디서 가져오시겠습니까?");
        builder.setMessage("이미지를 가져올 곳을 선택해주세요.");
        builder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("start",0);
                startActivity(intent);
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    tempFile = createImageFile();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"이미지 처리 중 오류가 발생했습니다. 다시 시도해 주세요. 오류코드: " + e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                if(tempFile != null) {
                    if(Build.VERSION.SDK_INT >= 24) {
                        Uri photoUri = FileProvider.getUriForFile(MainActivity.this, "com.candykick.startup.provider", tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } else {
                        Uri photoUri = Uri.fromFile(tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }
                }*/
            }
        });
        builder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("start",1);
                startActivity(intent);
                /*CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .start(Join1Activity.this);*/
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);*/
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*ImageViewSimpleAdapter adapter = new ImageViewSimpleAdapter(MainActivity.this, questionImage);
        binding.vpMain.setAdapter(adapter);*/
    }

    private File createImageFile() throws IOException {
        // 이미지 파일 이름: startup_{시간}
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "startup_"+timeStamp;
        // 이미지가 저장될 폴더 이름: startup
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/startup/");
        if(!storageDir.exists())
            storageDir.mkdirs();
        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }
}
