package learnopengl.xiaobole.com.activity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import learnopengl.xiaobole.com.R;
import learnopengl.xiaobole.com.camera.CameraManager;
import learnopengl.xiaobole.com.camera.CameraSetting;

public class CameraPreviewActivity extends Activity {

    private static final String TAG = "CameraPreviewActivity";

    private CameraManager mCameraManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_preview);

        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surfaceview);

        CameraSetting cameraSetting = new CameraSetting();
        cameraSetting.setCameraId(CameraSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK)
                .setCameraPreviewSizeRatio(CameraSetting.CAMERA_PREVIEW_RATIO.RATIO_16_9)
                .setCameraPreviewSizeLevel(CameraSetting.CAMERA_PREVIEW_SIZE_LEVEL.PREVIEW_SIZE_LEVEL_720P);
        mCameraManager = new CameraManager(glSurfaceView, cameraSetting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraManager.destroy();
    }

    public void onCLickSwitchCamera(View v) {
    }
}
