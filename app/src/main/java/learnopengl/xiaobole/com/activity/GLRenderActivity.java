package learnopengl.xiaobole.com.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import learnopengl.xiaobole.com.drawer.IDrawer;
import learnopengl.xiaobole.com.drawer.RectangleDrawer;
import learnopengl.xiaobole.com.drawer.TriangleDrawer;
import learnopengl.xiaobole.com.drawer.VAOTriangleDrawer;
import learnopengl.xiaobole.com.drawer.VBOTriangleDrawer;
import learnopengl.xiaobole.com.render.OpenGLRender;

public class GLRenderActivity extends AppCompatActivity {

    public static final int RENDER_TYPE_TRIANGLE = 0;
    public static final int RENDER_TYPE_RECTANGLE = 1;
    public static final int RENDER_TYPE_VBO_TRIANGLE = 2;
    public static final int RENDER_TYPE_VAO_TRIANGLE = 3;

    private GLSurfaceView mGLSurfaceView;
    private IDrawer mDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        int renderType = getIntent().getIntExtra("RenderType", 0);
        mDrawer = createDrawer(renderType);
        if (mDrawer == null) {
            return;
        }
        mGLSurfaceView.setRenderer(new OpenGLRender(mDrawer));
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLSurfaceView);
    }

    private IDrawer createDrawer(int renderType) {
        switch (renderType) {
            case RENDER_TYPE_TRIANGLE:
                return new TriangleDrawer();
            case RENDER_TYPE_RECTANGLE:
                return new RectangleDrawer();
            case RENDER_TYPE_VBO_TRIANGLE:
                return new VBOTriangleDrawer();
            case RENDER_TYPE_VAO_TRIANGLE:
                return new VAOTriangleDrawer();
        }
        return null;
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
