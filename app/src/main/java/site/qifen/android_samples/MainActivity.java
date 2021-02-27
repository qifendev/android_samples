package site.qifen.android_samples;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ViewPort;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.extensions.BokehImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CameraX图片和视频拍摄，所有拍摄的资源都会存放在getFilesDir方法返回的路径下
 */

public class MainActivity extends AppCompatActivity {

    private ImageCapture imageCapture;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;

    private static final int CAMERA_BACK = CameraSelector.LENS_FACING_BACK; //back camera
    private static final int CAMERA_FRONT = CameraSelector.LENS_FACING_FRONT; //front camera
    private CameraControl cameraControl;
    private ExecutorService executor;
    private Button takePicture;
    private TextView cameraInfo;
    private SwitchMaterial flushSwitch;
    private SwitchMaterial cameraSwitch;
    private SwitchMaterial videoRecordSwitch;
    private CameraSelector backCameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private ImageAnalysis imageAnalysis;
    private UseCaseGroup useCaseGroup;
    private ViewPort viewPort;
    private Camera camera;
    private CameraSelector frontCameraSelector;
    private ListenableFuture<FocusMeteringResult> future;
    private OrientationEventListener orientationEventListener;
    private MeteringPointFactory meteringPointFactory;
    private VideoCapture videoCapture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);

        initCamera();


        previewView = findViewById(R.id.previewView);

        takePicture = findViewById(R.id.takePicture);

        cameraInfo = findViewById(R.id.cameraInfo);

        flushSwitch = findViewById(R.id.flushSwitch);

        cameraSwitch = findViewById(R.id.cameraSwitch);

        videoRecordSwitch = findViewById(R.id.takeVideoSwitch);

        findViewById(R.id.viewImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewImageActivity.class));
            }
        });


        previewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    touchFocus(event.getX(), event.getY());
                Log.e("focus", "onTouch focus x: " + event.getX() + " y: " + event.getY());
                previewView.performClick();
                return false;
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = getFilesDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";

                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(new File(path)).build();
                imageCapture.takePicture(outputFileOptions, executor,
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                t("image saved");
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                t("save error");
                            }
                        });
            }
        });

        cameraInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        flushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                } else {
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                }
            }
        });

        cameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraProvider.unbindAll();

                if (isChecked) {

                    camera = cameraProvider.bindToLifecycle(
                            ((LifecycleOwner) MainActivity.this),
                            frontCameraSelector,
                            preview,
                            imageCapture,
                            videoCapture);

                    flushSwitch.setEnabled(false);
                    flushSwitch.setText("flush disabled");
                    cameraSwitch.setText("camera");
                } else {

                    camera = cameraProvider.bindToLifecycle(
                            ((LifecycleOwner) MainActivity.this),
                            backCameraSelector,
                            preview,
                            imageCapture,
                            videoCapture);

                    flushSwitch.setEnabled(true);
                    flushSwitch.setText("flush enabled");
                    cameraSwitch.setText("camera");
                }

                cameraControl = camera.getCameraControl();


            }
        });

        videoRecordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRecordVideo();
                    videoRecordSwitch.setText("video record starting");
                } else {
                    videoCapture.stopRecording();
                    videoRecordSwitch.setText("video record stop");
                }
            }
        });


    }

    @SuppressLint({"UnsafeExperimentalUsageError", "RestrictedApi"})
    private void initCamera() {

        executor = Executors.newCachedThreadPool();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);


        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                cameraProvider = cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                preview = new Preview.Builder().build();

                // Set up the capture use case to allow users to take photos
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) //image quality
//                        .setFlashMode(ImageCapture.FLASH_MODE_ON) //flush on
                        .build();


                videoCapture = new VideoCapture.Builder()
                        //设置宽高比
//                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        //分辨率(宽高比和分辨率不能同时设置)
                        .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
                        //视频帧率  越高视频体积越大
//                        .setVideoFrameRate(25)
                        //bit率  越大视频体积越大
//                        .setBitRate(3 * 1024 * 1024)
                        .build();


                // Choose the camera by requiring a lens facing
                backCameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CAMERA_BACK)
                        .build();

                frontCameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CAMERA_FRONT)
                        .build();


                //imageAnalysis
                imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() { //获取帧
                    @Override
                    public void analyze(@NonNull ImageProxy image) {

                        int rotationDegrees = image.getImageInfo().getRotationDegrees();
                        // insert your code here.
                    }
                });

                //view size
                viewPort = previewView.getViewPort();
                useCaseGroup = new UseCaseGroup.Builder()
                        .addUseCase(preview)
                        .addUseCase(imageAnalysis)
                        .addUseCase(imageCapture)
                        .setViewPort(viewPort)
                        .build();

                camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        backCameraSelector,
                        preview,
                        imageCapture,
                        videoCapture);

//                // Attach use cases to the camera with the same lifecycle owner
//                camera = cameraProvider.bindToLifecycle(
//                        ((LifecycleOwner) MainActivity.this),
//                        backCameraSelector,
//                        useCaseGroup);


                cameraControl = camera.getCameraControl();

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());


            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));


        orientationEventListener = new OrientationEventListener((Context) this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;

                // Monitors orientation values to determine the target rotation value
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }
                if (imageCapture != null) {
                    preview.setTargetRotation(rotation); //preview rotation
                    imageCapture.setTargetRotation(rotation); //capture rotation
                    videoCapture.setTargetRotation(rotation); //video rotation
                }
            }
        };
        orientationEventListener.enable();
    }


    @SuppressLint("RestrictedApi")
    void startRecordVideo() {
        String path = getFilesDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4";
        VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(new File(path)).build();
        videoCapture.startRecording(outputFileOptions, executor, new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
//                Uri savedUri = outputFileResults.getSavedUri();
                t("video saved success");
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                t("video saved fail");

            }
        });
    }


    private void touchFocus(float x, float y) {
        meteringPointFactory = new SurfaceOrientedMeteringPointFactory(previewView.getWidth(), previewView.getHeight());
        MeteringPoint point = meteringPointFactory.createPoint(x, y);
        FocusMeteringAction focusMeteringAction = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .addPoint(point, FocusMeteringAction.FLAG_AE) // could have many
                // auto calling cancelFocusAndMetering in 5 seconds
//                .setAutoCancelDuration(5, TimeUnit.SECONDS)
                .build();

        future = cameraControl.startFocusAndMetering(focusMeteringAction);

        future.addListener(() -> {
            try {
                if (future != null) {
                    FocusMeteringResult result = future.get();
                    String text = "focus x: " + x + " y: " + y + " " + result.isFocusSuccessful();
                    Log.e("focus", text);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cameraInfo.setText(text);
                        }
                    });
                }
                // process the result
            } catch (Exception e) {
                Log.e("focus", "touchFocus: ", e);
            }
        }, executor);

    }

    private void t(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                t("please granted all permission");
            }
        }

    }
}
















