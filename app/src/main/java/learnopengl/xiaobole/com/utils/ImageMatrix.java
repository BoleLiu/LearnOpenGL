package learnopengl.xiaobole.com.utils;

import android.opengl.Matrix;

public class ImageMatrix {

    private final float[] mCameraMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    public boolean setViewport(int imageWidth, int imageHeight, int viewWidth, int viewHeight) {
        float imageRatio = imageWidth / (float) imageHeight;
        float screenRatio = viewWidth / (float) viewHeight;
        if (viewWidth > viewHeight) {
            float ratio = (imageRatio > screenRatio) ? screenRatio * imageRatio : screenRatio / imageRatio;
            Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        } else {
            float ratio = (imageRatio > screenRatio) ? 1 / screenRatio * imageRatio : imageRatio / screenRatio;
            Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);
        }
        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);
        return true;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }
}
