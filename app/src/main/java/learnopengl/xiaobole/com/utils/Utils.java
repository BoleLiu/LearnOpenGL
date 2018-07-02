package learnopengl.xiaobole.com.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Surface;
import android.view.WindowManager;

public class Utils {

    /**
     * check if the device has a camera
     *
     * @param context the context
     * @return true or not
     */
    public static boolean checkCameraDevice(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static int getDeviceRotationDegree(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }
}
