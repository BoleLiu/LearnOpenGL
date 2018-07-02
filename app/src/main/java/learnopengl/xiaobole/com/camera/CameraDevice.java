package learnopengl.xiaobole.com.camera;


import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

public class CameraDevice {
    private static final String TAG = "CameraDevice";

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    private CameraDevice() {
        mCameraInfo = new Camera.CameraInfo();
    }

    private static class CameraDeviceHolder {
        private static final CameraDevice mInstance = new CameraDevice();
    }

    public static CameraDevice getInstance() {
        return CameraDeviceHolder.mInstance;
    }

    public int getNumbersOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public Camera.CameraInfo getCameraInfo() {
        return mCameraInfo;
    }

    public boolean isFrontCamera() {
        return mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public boolean openCamera(int cameraId) {
        boolean opened = false;

        try {
            releaseCamera();
            mCamera = Camera.open(cameraId);
            opened = mCamera != null;
        } catch (RuntimeException e) {
            Log.e(TAG, "failed to open camera: " + cameraId);
            e.printStackTrace();
        }

        if (opened) {
            Camera.getCameraInfo(cameraId, mCameraInfo);
            Log.i(TAG, "open camera " + cameraId + " success");
        }

        return opened;
    }

    public void releaseCamera() {
        if (mCamera == null) {
            Log.i(TAG, "camera is null !!!");
            return;
        }

        mCamera.release();
        mCamera = null;
        Log.i(TAG, "release camera success !!!");
    }

    public int getPreviewWidth() {
        Camera.Parameters parameters = getParameters();
        if (parameters == null) {
            return 0;
        }
        return parameters.getPreviewSize().width;
    }

    public int getPreviewHeight() {
        Camera.Parameters parameters = getParameters();
        if (parameters == null) {
            return 0;
        }
        return parameters.getPreviewSize().height;
    }

    public Camera.Parameters getParameters() {
        if (mCamera == null) {
            Log.i(TAG, "getParameters failed, camera is null !!!");
            return null;
        }
        return mCamera.getParameters();
    }

    public List<int[]> getSupportFpsRange() {
        Camera.Parameters parameters = getParameters();
        if (parameters == null) {
            return null;
        }
        return parameters.getSupportedPreviewFpsRange();
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        Camera.Parameters parameters = getParameters();
        if (parameters == null) {
            return null;
        }
        return parameters.getSupportedPreviewSizes();
    }

    public void setParameters(Camera.Parameters parameters) {
        if (mCamera == null) {
            Log.w(TAG, "setParameters failed, camera is null");
            return;
        }
        mCamera.setParameters(parameters);
    }

    public boolean setPreviewDisplay(SurfaceHolder holder) {
        if (mCamera == null) {
            Log.w(TAG, "setPreviewDisplay failed, camera is null");
            return false;
        }
        try {
            mCamera.setPreviewDisplay(holder);
            return true;
        } catch (IOException e) {
            Log.w(TAG, "setPreviewDisplay failed, camera is null");
            e.printStackTrace();
        }
        return false;
    }

    public void setDisplayOrientation(int degrees) {
        if (mCamera == null) {
            Log.w(TAG, "setDisplayOrientation failed, camera is null");
            return;
        }
        mCamera.setDisplayOrientation(degrees);
    }

    public void startPreview() {
        if (mCamera == null) {
            Log.w(TAG, "startPreview failed, camera is null");
            return;
        }
        mCamera.startPreview();
    }

    public void stopPreview () {
        if (mCamera == null) {
            Log.w(TAG, "stopPreview failed, camera is null");
            return;
        }
        mCamera.stopPreview();
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback cb) {
        if (mCamera == null) {
            Log.w(TAG, "setPreviewCallbackWithBuffer failed, camera is null");
            return;
        }
        mCamera.setPreviewCallbackWithBuffer(cb);
    }

    public void addCallbackBuffer(byte[] callbackBuffer) {
        if (mCamera == null) {
            Log.w(TAG, "addCallbackBuffer failed, camera is null");
            return;
        }
        mCamera.addCallbackBuffer(callbackBuffer);
    }

    public void setPreviewCallback(Camera.PreviewCallback cb) {
        if (mCamera == null) {
            Log.w(TAG, "setPreviewCallback failed, camera is null");
            return;
        }
        mCamera.setPreviewCallback(cb);
    }

    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        if (mCamera == null) {
            Log.w(TAG, "setPreviewTexture failed, camera is null");
            return;
        }
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            Log.w(TAG, "setPreviewTexture failed " + e.getMessage());
            e.printStackTrace();
        }
    }
}
