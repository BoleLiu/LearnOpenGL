package learnopengl.xiaobole.com.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import learnopengl.xiaobole.com.render.GLVideoRender;
import learnopengl.xiaobole.com.utils.Utils;

import static android.graphics.ImageFormat.NV21;

public class CameraManager implements SurfaceTexture.OnFrameAvailableListener,
        GLVideoRender.VideoFilterListener{

    private static final String TAG = "CameraManager";
    private static final double ASPECT_TOLERANCE = 0.05;

    private Context mContext;
    private CameraSetting mCameraSetting;
    private int mCurrentFacingId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private WeakReference<GLSurfaceView> mGLSurfaceView;
    private GLVideoRender mVideoRender;
    private CameraParamSelectCallback mCameraParamSelectCallback;

    public interface CameraParamSelectCallback {
        Camera.Size onPreviewSizeSelected(List<Camera.Size> list);
        int[] onPreviewFpsSelected(List<int[]> list);
        String onFocusModeSelected(List<String> list);
    }

    public CameraManager(Context context, CameraSetting setting) {
        Log.i(TAG, "CameraManager created !");
        mContext = context;
        mCameraSetting = setting;
        mCurrentFacingId = mCameraSetting.getCameraId();
    }

    public CameraManager(GLSurfaceView glSurfaceView, CameraSetting setting) {
        this(glSurfaceView.getContext(), setting);
        mVideoRender = new GLVideoRender();
        mVideoRender.setOnFrameAvailableListener(this);
        mVideoRender.setVideoFilterListener(this);

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(mVideoRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLSurfaceView = new WeakReference<>(glSurfaceView);
    }

    public void resume() {
        Log.i(TAG, "resume");
        GLSurfaceView glSurfaceView = mGLSurfaceView.get();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }

    public void pause() {
        Log.i(TAG, "pause");
        GLSurfaceView glSurfaceView = mGLSurfaceView.get();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
        CameraDevice.getInstance().stopPreview();
        CameraDevice.getInstance().releaseCamera();
    }

    public void destroy() {
        Log.i(TAG, "destroy");
        mVideoRender.destroy();
    }

    public void setCameraParamSelectCallback(CameraParamSelectCallback callback) {
        mCameraParamSelectCallback = callback;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i(TAG, "onFrameAvailable");
        GLSurfaceView glSurfaceView = mGLSurfaceView.get();
        if (glSurfaceView != null) {
            glSurfaceView.requestRender();
        }
    }

    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        if (!setupCamera()) {
            return;
        }
        CameraDevice.getInstance().setPreviewTexture(mVideoRender.getSurfaceTexture());
        CameraDevice.getInstance().startPreview();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged width = " + width + " height = " + height);

    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] matrix, long timestamp) {
        Log.i(TAG, "onDrawFrame timestamp = " + timestamp);
        return texId;
    }

    public boolean setupCamera() {
        if (!Utils.checkCameraDevice(mContext)) {
            Log.i(TAG, "Fatal error. No camera hardware !");
            return false;
        }
        /**
         * open camera
         */
        if (!CameraDevice.getInstance().openCamera(mCurrentFacingId)) {
            return false;
        }
        Camera.Parameters parameters = CameraDevice.getInstance().getParameters();
        if (parameters == null) {
            return false;
        }

        /**
         * select camera preview format
         */
        List<Integer> supportedFormat = parameters.getSupportedPreviewFormats();
        for(Integer format : supportedFormat) {
            if (format == NV21) {
                Log.i(TAG, "set camera preview format NV21");
                parameters.setPreviewFormat(NV21);
                break;
            }
        }

        /**
         * select camera focus mode
         */
        String focusMode = null;
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes != null && !focusModes.isEmpty()) {
            if (mCameraParamSelectCallback != null) {
                focusMode = mCameraParamSelectCallback.onFocusModeSelected(focusModes);
                if (!focusModes.contains(focusMode)) {
                    Log.i(TAG, "no such focus mode exists in this camera");
                    focusMode = null;
                }
            }

            if (focusMode == null) {
                focusMode = focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
                        ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                        : focusModes.get(0);
            }
            parameters.setFocusMode(focusMode);
        }

        /**
         * select camera preview size
         */
        Camera.Size selectedSize = null;
        List<Camera.Size> previewSizes = sortCameraPreviewSize(filterCameraPreviewSize(parameters.getSupportedPreviewSizes(),
                mCameraSetting.getPreviewSizeRatio(), mCameraSetting.getPreviewSizeLevel()));
        if (previewSizes != null && !previewSizes.isEmpty()) {
            if (mCameraParamSelectCallback != null) {
                selectedSize = mCameraParamSelectCallback.onPreviewSizeSelected(previewSizes);
            }
            selectedSize = selectedSize == null ? previewSizes.get(previewSizes.size() / 2) : selectedSize;
            parameters.setPreviewSize(selectedSize.width, selectedSize.height);
            Log.i(TAG, "set camera preview size: " + selectedSize.width + "x" + selectedSize.height);
        }

        /**
         * select camera preview fps
         */
        List<int[]> fpsRange = CameraDevice.getInstance().getSupportFpsRange();
        int[] selectedFps = null;
        if (fpsRange != null && !fpsRange.isEmpty()) {
            if (mCameraParamSelectCallback != null) {
                selectedFps = mCameraParamSelectCallback.onPreviewFpsSelected(fpsRange);
            }
            if (selectedFps != null && selectedFps.length == 2) {
                parameters.setPreviewFpsRange(selectedFps[0], selectedFps[1]);
                Log.i(TAG, "set camera preview fps: " + selectedFps[0] + "~" + selectedFps[1]);
            }
        }

        /**
         * select camera display orientation
         */
        int degree = Utils.getDeviceRotationDegree(mContext);
        Camera.CameraInfo cameraInfo = CameraDevice.getInstance().getCameraInfo();
        int orientation;
        if (mCurrentFacingId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (cameraInfo.orientation + degree) % 360;
            orientation = (360 - orientation) % 360;    // compensate the mirror
        } else {    // back-facing
            orientation = (cameraInfo.orientation - degree + 360) % 360;
        }
        CameraDevice.getInstance().setDisplayOrientation(orientation);
        Log.i(TAG, "set camera display orientation: " + orientation);
        CameraDevice.getInstance().setParameters(parameters);

        /**
         * set video size for render
         */
        if (orientation == 90 || orientation == 270) {
            mVideoRender.setVideoSize(CameraDevice.getInstance().getPreviewHeight(),
                    CameraDevice.getInstance().getPreviewWidth());
        } else {
            mVideoRender.setVideoSize(CameraDevice.getInstance().getPreviewWidth(),
                    CameraDevice.getInstance().getPreviewHeight());
        }

        return true;
    }

    private List<Camera.Size> filterCameraPreviewSize(List<Camera.Size> sizes, CameraSetting.CAMERA_PREVIEW_RATIO ratio,
                                                      CameraSetting.CAMERA_PREVIEW_SIZE_LEVEL level) {
        if (sizes == null) {
            return null;
        }

        /**
         * filter by ratio
         */
        double targetRatio = CameraSetting.calcCameraPreviewSizeRatio(ratio);
        Iterator<Camera.Size> iterator = sizes.iterator();
        while (iterator.hasNext()) {
            Camera.Size size = iterator.next();
            Log.i(TAG, "size.width:" + size.width + ",size.height:" + size.height);
            double r = (double) size.width / size.height;
            if (Math.abs(r - targetRatio) > ASPECT_TOLERANCE) {
                iterator.remove();
            }
        }

        /**
         * filter by size level
         */
        List<Camera.Size> discards = new ArrayList<>();
        int targetLevel = CameraSetting.calcCameraPreviewSizeLevel(level);
        iterator = sizes.iterator();
        while (iterator.hasNext()) {
            Camera.Size size = iterator.next();
            if (size.height != targetLevel) {
                discards.add(size);
                iterator.remove();
            }
        }

        for (Camera.Size size : sizes) {
            Log.i(TAG, "after filter size.w:" + size.width + ", size.h:" + size.height);
        }

        return sizes.isEmpty() ? discards : sizes;
    }

    private List<Camera.Size> sortCameraPreviewSize(List<Camera.Size> sizes) {
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size size1, Camera.Size size2) {
                return size1.width * size1.height - size2.width * size2.height;
            }
        });
        return sizes;
    }
}
