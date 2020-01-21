package com.candykick.cdkgallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by candykick on 2019. 9. 20..
 *
 * 실질적인 카메라의 동작을 담당하는 부분.
 *
 * 참고 자료: https://brunch.co.kr/@mystoryg/55
 *          https://brunch.co.kr/@mystoryg/96
 *          https://myandroidarchive.tistory.com/6
 * */

// 촬영 시 촬영 이펙트 화면에 넣기

public class CdkCamera extends Thread {
    private Size previewSize;
    private Context context; //Activity를 넘겨줘야 한다.
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    private String cameraId = "0";
    private TextureView textureView;
    private CdkCameraInterface cdkCameraInterface;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CdkCamera(Context context, TextureView textureView) {
        this.context = context;
        this.textureView = textureView;
    }

    public void setOnCallbackListener(CdkCameraInterface cdkCameraInterface) {
        this.cdkCameraInterface = cdkCameraInterface;
    }

    //스마트폰에 탑재된 카메라의 식별자(cameraId)들 중 후면(flag==0)/전면(flag==1) 카메라의 식별자를 가져오는 함수.
    private String getCameraId(CameraManager cameraManager, int flag) {
        try {
            for (final String cameraId: cameraManager.getCameraIdList()) { //getCameraIdList: 문자열 배열.
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                /*
                 * CameraCharacteristics의 키 값에 따라서 사진을 찍을 카메라를 설정할 수 있다.
                 * CameraCharacteristics.LENS_FACING_FRONT: 전면 카메라(0)
                 * CameraCharacteristics.LENS_FACING_BACK: 후면 카메라(1)
                 * CameraCharacteristics.LENS_FACING_EXTERNAL: 기타 카메라(2)
                 */
                if(cOrientation == CameraCharacteristics.LENS_FACING_BACK & flag==0)
                    return cameraId;
                else if(cOrientation == CameraCharacteristics.LENS_FACING_FRONT & flag==1)
                    return cameraId;
            }
        } catch (CameraAccessException e) { //이 부분 때문에 minSdkVersion이 19에서 21로 올라갔다.
            e.printStackTrace();
        }

        return null;
    }

    //카메라를 여는 함수. flag==0: 후면, flag==1: 전면.
    public void openCamera(int flag) {
        CameraManager manager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if(flag == 0) { //전면 카메라에서 좌우반전이 걸린 경우 후면 카메라로 전환 시 좌우반전을 해제해 준다.
                reversalCamera(false);
            }

            cameraId = getCameraId(manager, flag);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            //촬영한 사진 크기에 대한 정보를 가져옴.
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //사진 크기들 중 0번째 크기의 정보를 가져옴.
            /*
             * Size[] sizes = map.getOutputSizes(ImageFormat.JPEG); : 이미지 포맷을 지정하면 지원하는 사진 크기 목록이 리턴된다.
             */
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            int permissionCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if(permissionCamera == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CAMERA}, GalleryActivity.REQUEST_CAMERA);
            } else {
                manager.openCamera(cameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {//이 부분 때문에 minSdkVersion이 19에서 21로 올라갔다.
            e.printStackTrace();
        }
    }

    //사진을 찍는 함수. 사진 경로를 반환한다.
    public String takePicture() {
        if(null == cameraDevice) {
            return null;
        }

        try {
            Size[] jpegSizes = null;

            CameraManager manager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if(map != null) {
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640; int height = 480;
            if(jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            //surface를 담은 list 생성.
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            //프리뷰가 아닌, 촬영을 위한 것이므로 TEMPLATE_STILL_CAPTURE 인자를 사용함.
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Orientation
            int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //촬영한 사진을 저장한다: 1. 파일명 및 경로를 구성한다.
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
            final File file = new File(Environment.getExternalStorageDirectory()+"/DCIM", dateFormat.format(date)+".jpg");

            //촬영한 사진을 저장한다: 2. 저장을 수행한다.
            //  ImageReader에 OnImageAvailableListener를 설정해서 카메라 session으로부터 데이터를 획득하면
            //  해당 정보를 바이트 정보로 가져온 후 파일로 쓴다.
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;

                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                            reader.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };

            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroundHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroundHandler);

            final Handler delayPreview = new Handler();
            Runnable delayPreviewRunnable = (() -> startPreview());
            //사진 촬영 후 호출되는 콜백.
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(context, "Saved: "+file, Toast.LENGTH_SHORT).show();

                    //1초 동안 촬영한 결과물을 보여준 후 다시 프리뷰가 시작됨.
                    delayPreview.postDelayed(delayPreviewRunnable, 1000);
                    //미디어 스캔을 실행(기본 갤러리로도 이미지를 확인할 수 있게 하기 위함)
                    cdkCameraInterface.onSave(file);
                }
            };

            //surface와 CameraCaptureSession을 연결함.
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        /*세션이 구성되면 capture() 함수를 호출. 각 인자의 역할은 다음과 같다.
                         * 1: CaptureRequest. TEMPLATE_STILL_CAPTURE 인자를 사용함.
                         * 2: 촬영 후 콜백
                         * 3: UI(프리뷰)를 계속해서 보여주기 위한 핸들러
                         */
                        session.capture(captureBuilder.build(), captureListener, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
            }, backgroundHandler);

            return file.getAbsolutePath();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    //카메라 플래시를 켜고 끄는 함수
    public void flashlight(int flag) {
        if (flag == 0) {
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
        } else if (flag == 1) {
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
        } else if (flag == 2) {
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH);
            previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
        }

        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //전면 카메라에서 화면 좌우반전하는 함수
    public void reversalCamera(boolean isReversal) {
        if(cameraId.equals("0")) { //후면 카메라인 경우
            Matrix matrix = new Matrix();
            matrix.setScale(1,1);
            textureView.setTransform(matrix);
        } else if(cameraId.equals("1")) { //전면 카메라인 경우
            if(isReversal) { //좌우반전을 원하면
                Matrix matrix = new Matrix();
                matrix.setScale(-1,1);
                matrix.postTranslate(textureView.getMeasuredWidth(), 0);
                //matrix.postTranslate(mWidth, 0);
                textureView.setTransform(matrix);
            } else { //좌우반전을 원하지 않으면
                Matrix matrix = new Matrix();
                matrix.setScale(1,1);
                textureView.setTransform(matrix);
            }
        }
    }

    //카메라 앱이 resume 시 작동할 리스너. 카메라 프리뷰를 보여줄 때 사용할 뷰이다.
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(0); //일단 후면으로 실행.
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
    };

    //카메라 상태 콜백
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {//카메라가 정상적으로 열렸을 때
            cameraDevice = camera;
            startPreview(); //프리뷰를 작동시킴.
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {}

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {}
    };

    //카메라 프리뷰를 가져오는 함수(카메라 상태 콜백의 onOpened에서 호출)
    protected void startPreview() {
        if(null == cameraDevice || !textureView.isAvailable() || null == previewSize) {}

        SurfaceTexture texture = textureView.getSurfaceTexture();
        if(null == texture) {
            return;
        }

        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            //프리뷰를 가져오기 위해 인자를 TEMPLATE_PREVIEW로 준다.
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        previewBuilder.addTarget(surface);

        try {
            //캡처 세션 콜백을 받아서 onConfigured 시에 프리뷰를 업데이트한다.
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    previewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(context, "카메라 설정에 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        if(null == cameraDevice) {}

        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void setSurfaceTextureListener() {
        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void onResume() {
        setSurfaceTextureListener();
    }

    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    public void onPause() {
        try {
            cameraOpenCloseLock.acquire();
            if(null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            cameraOpenCloseLock.release();
        }
    }
}