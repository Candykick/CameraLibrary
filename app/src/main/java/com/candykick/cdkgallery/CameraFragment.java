package com.candykick.cdkgallery;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.candykick.cdkgallery.databinding.FragmentCameraBinding;

import java.io.File;

/*
 * CdkCamera 기능
 * 1. 기본적인 사진 촬영
 * 2. 카메라 전환(전면<->후면) (광각 전환은 아직 없음)
 * 3. 플래시(자동, 수동)
 * 4. 전면 카메라의 경우 좌우반전 저장 옵션
 * 5. 안내선(3x3 분할, 4*4 분할, 최대 크기 정사각형, 명함)
 * (6. 타이머(아직 없음))
 */

/*
 * 남은 수정사항
 * 1. 화면 출력 시 비율이 맞지 않는 문제
 * 2. 화면 회전 반영
 * 3. 플래시 자동 작동 안함
 */

public class CameraFragment extends Fragment implements CdkCameraInterface {

    FragmentCameraBinding binding;
    private CdkCamera cdkCamera;

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_SETTING = 10;

    int flag = 0; int flashFlag = 0;
    int[] flashImage = new int[]{R.drawable.ic_flash_off_white_24dp, R.drawable.ic_flash_on_white_24dp, R.drawable.ic_flash_auto_white_24dp};

    public CameraFragment() {}

    public interface CameraListener {
        void receivedCameraImage(Bitmap bitmap, Uri path);
        void receivedCameraImage(String path);
    }

    private CameraListener cameraListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);

        cdkCamera = new CdkCamera(getActivity(), binding.cameraView);
        cdkCamera.setOnCallbackListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof CameraListener) {
            cameraListener = (CameraListener)getActivity();
        }
    }

    @Override
    public void onSave(File filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(filePath));
        getActivity().sendBroadcast(intent);
    }

    public void capture() {
        String path = cdkCamera.takePicture();

        if(path != null) {
            cameraListener.receivedCameraImage(path);
        } else {
            Toast.makeText(getActivity(), "카메라가 정상적으로 실행되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }
    public void changeCamera() {
        if(flag == 0) {
            cdkCamera.onPause();
            cdkCamera.openCamera(1);
            flag = 1;
        } else {
            cdkCamera.onPause();
            cdkCamera.openCamera(0);
            flag = 0;
        }
    }
    public void flash() {
        //0: 꺼짐, 1: 켜짐, 2: 자동
        switch (flashFlag) {
            case 0:
                flashFlag = 1;
                binding.btnFlash.setImageResource(flashImage[1]);
                cdkCamera.flashlight(1);
                break;
            case 1:
                flashFlag = 2;
                binding.btnFlash.setImageResource(flashImage[2]);
                cdkCamera.flashlight(2);
                break;
            case 2:
                flashFlag = 0;
                binding.btnFlash.setImageResource(flashImage[0]);
                cdkCamera.flashlight(0);
                break;
        }
    }
    public void setting() {
        Intent intent = new Intent(getActivity(), CameraSettingDialog.class);
        startActivityForResult(intent, REQUEST_SETTING);
        cdkCamera.onPause();
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);

        if(request == REQUEST_SETTING) {
            boolean isReversal = data.getBooleanExtra("isReversal", false);
            cdkCamera.reversalCamera(isReversal);

            int guideline = data.getIntExtra("guideline", 0);
            switch (guideline) {//0: 없음, 1: 3x3 분할, 2: 4*4 분할, 3: 최대 크기 정사각형, 4: 명함
                case 0:
                    binding.gl3x31.setVisibility(View.GONE);
                    binding.gl3x32.setVisibility(View.GONE);
                    binding.gl3x33.setVisibility(View.GONE);
                    binding.gl3x34.setVisibility(View.GONE);
                    binding.gl4x41.setVisibility(View.GONE);
                    binding.gl4x42.setVisibility(View.GONE);
                    binding.gl4x43.setVisibility(View.GONE);
                    binding.gl4x44.setVisibility(View.GONE);
                    binding.gl4x45.setVisibility(View.GONE);
                    binding.gl4x46.setVisibility(View.GONE);
                    binding.glSquare.setVisibility(View.GONE);
                    binding.glNamecard.setVisibility(View.GONE);
                    break;
                case 1:
                    binding.gl3x31.setVisibility(View.VISIBLE);
                    binding.gl3x32.setVisibility(View.VISIBLE);
                    binding.gl3x33.setVisibility(View.VISIBLE);
                    binding.gl3x34.setVisibility(View.VISIBLE);
                    binding.gl4x41.setVisibility(View.GONE);
                    binding.gl4x42.setVisibility(View.GONE);
                    binding.gl4x43.setVisibility(View.GONE);
                    binding.gl4x44.setVisibility(View.GONE);
                    binding.gl4x45.setVisibility(View.GONE);
                    binding.gl4x46.setVisibility(View.GONE);
                    binding.glSquare.setVisibility(View.GONE);
                    binding.glNamecard.setVisibility(View.GONE);
                    break;
                case 2:
                    binding.gl3x31.setVisibility(View.GONE);
                    binding.gl3x32.setVisibility(View.GONE);
                    binding.gl3x33.setVisibility(View.GONE);
                    binding.gl3x34.setVisibility(View.GONE);
                    binding.gl4x41.setVisibility(View.VISIBLE);
                    binding.gl4x42.setVisibility(View.VISIBLE);
                    binding.gl4x43.setVisibility(View.VISIBLE);
                    binding.gl4x44.setVisibility(View.VISIBLE);
                    binding.gl4x45.setVisibility(View.VISIBLE);
                    binding.gl4x46.setVisibility(View.VISIBLE);
                    binding.glSquare.setVisibility(View.GONE);
                    binding.glNamecard.setVisibility(View.GONE);
                    break;
                case 3:
                    binding.gl3x31.setVisibility(View.GONE);
                    binding.gl3x32.setVisibility(View.GONE);
                    binding.gl3x33.setVisibility(View.GONE);
                    binding.gl3x34.setVisibility(View.GONE);
                    binding.gl4x41.setVisibility(View.GONE);
                    binding.gl4x42.setVisibility(View.GONE);
                    binding.gl4x43.setVisibility(View.GONE);
                    binding.gl4x44.setVisibility(View.GONE);
                    binding.gl4x45.setVisibility(View.GONE);
                    binding.gl4x46.setVisibility(View.GONE);
                    binding.glSquare.setVisibility(View.VISIBLE);
                    binding.glNamecard.setVisibility(View.GONE);
                    break;
                case 4:
                    binding.gl3x31.setVisibility(View.GONE);
                    binding.gl3x32.setVisibility(View.GONE);
                    binding.gl3x33.setVisibility(View.GONE);
                    binding.gl3x34.setVisibility(View.GONE);
                    binding.gl4x41.setVisibility(View.GONE);
                    binding.gl4x42.setVisibility(View.GONE);
                    binding.gl4x43.setVisibility(View.GONE);
                    binding.gl4x44.setVisibility(View.GONE);
                    binding.gl4x45.setVisibility(View.GONE);
                    binding.gl4x46.setVisibility(View.GONE);
                    binding.glSquare.setVisibility(View.GONE);
                    binding.glNamecard.setVisibility(View.VISIBLE);
                    break;
            }

            cdkCamera.openCamera(flag);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cdkCamera.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cdkCamera.onPause();
    }
}