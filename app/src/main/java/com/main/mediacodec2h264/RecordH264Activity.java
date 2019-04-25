package com.main.mediacodec2h264;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class RecordH264Activity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private final static int REQ_PERMISSION_CODE = 0x1000;
    private static int yuvqueuesize = 10;
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);
    int width = 640;
    int height = 480;
    int framerate = 24;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Parameters parameters;
    private AvcEncoder avcCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordh264_activity);
        requestPermissin();
    }

    private void initViews() {
        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        SupportAvcCodec();
    }

    private void requestPermissin() {
        PermissionGen.needPermission(RecordH264Activity.this, REQ_PERMISSION_CODE,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        startCamera(camera);
        avcCodec = new AvcEncoder(width, height, framerate);
        avcCodec.StartEncoderThread();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            avcCodec.StopThread();
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
        // TODO Auto-generated method stub
        putYUVData(data);
    }

    public void putYUVData(byte[] buffer) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }

    @SuppressLint("NewApi")
    private boolean SupportAvcCodec() {
        if (Build.VERSION.SDK_INT >= 18) {
            for (int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);

                String[] types = codecInfo.getSupportedTypes();
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void startCamera(Camera mCamera) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(this);
                mCamera.setDisplayOrientation(90);
                if (parameters == null) {
                    parameters = mCamera.getParameters();
                }
                parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewSize(width, height);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = REQ_PERMISSION_CODE)
    public void requestPermissionsSuccess() {
        Log.e("权限", "Success");
        initViews();
    }


    @PermissionFail(requestCode = REQ_PERMISSION_CODE)
    public void requestPermissionsFail() {
        Log.e("权限", "Fail");
    }
}
