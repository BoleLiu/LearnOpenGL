package learnopengl.xiaobole.com.activity;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import learnopengl.xiaobole.com.drawer.IDrawer;
import learnopengl.xiaobole.com.drawer.TriangleDrawer;
import learnopengl.xiaobole.com.render.OpenGLRender;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private IDrawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mDrawer = new TriangleDrawer();
        mGLSurfaceView.setRenderer(new OpenGLRender(mDrawer));
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        mDrawer.release();
        super.onDestroy();
    }
}
