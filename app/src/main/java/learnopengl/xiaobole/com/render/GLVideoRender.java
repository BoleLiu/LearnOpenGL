package learnopengl.xiaobole.com.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import learnopengl.xiaobole.com.drawer.OESTextureDrawer;
import learnopengl.xiaobole.com.utils.GLUtils;
import learnopengl.xiaobole.com.utils.ImageMatrix;

public class GLVideoRender implements GLSurfaceView.Renderer {

    private static final String TAG = "GLVideoRender";

    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;
    private VideoFilterListener mVideoFilterListener;

    private OESTextureDrawer mOESTextureDrawer = new OESTextureDrawer();
    private ImageMatrix mImageMatrix = new ImageMatrix();

    private int mTexId;
    private float[] mTexMatrix = new float[16];

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;

    public interface VideoFilterListener {
        void onSurfaceCreated();
        void onSurfaceChanged(int width, int height);
        int onDrawFrame(int texId, int texWidth, int texHeight, float[] matrix, long timestamp);
    }

    public void setVideoFilterListener(VideoFilterListener listener) {
        mVideoFilterListener = listener;
    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
        mOnFrameAvailableListener = listener;
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }
    }

    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void destroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mVideoWidth = -1;
        mVideoHeight = -1;
        mOESTextureDrawer.release();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        mOESTextureDrawer.init();
        mTexId = GLUtils.createTexture();
        mSurfaceTexture = new SurfaceTexture(mTexId);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }
        if (mVideoFilterListener != null) {
            mVideoFilterListener.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
        if (mVideoFilterListener != null) {
            mVideoFilterListener.onSurfaceChanged(width, height);
        }
        mOESTextureDrawer.setViewPort(0, 0, width, height);
        mImageMatrix.setViewport(mVideoWidth, mVideoHeight, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mTexMatrix);
        int texId = mTexId;
        if (mVideoFilterListener != null) {
            texId = mVideoFilterListener.onDrawFrame(mTexId, mVideoWidth, mVideoHeight, mTexMatrix, mSurfaceTexture.getTimestamp());
        }
        if (texId <= 0) {
            texId = mTexId;
        }
        mOESTextureDrawer.draw(texId, null, mTexMatrix);
    }
}
