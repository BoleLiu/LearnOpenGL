package learnopengl.xiaobole.com.render;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import learnopengl.xiaobole.com.drawer.IDrawer;

import static android.opengl.GLES20.glClearColor;

public class OpenGLRender implements GLSurfaceView.Renderer {

    private IDrawer mDrawer;

    public OpenGLRender(IDrawer drawer) {
        mDrawer = drawer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mDrawer.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDrawer.setViewPort(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mDrawer.draw();
    }
}
