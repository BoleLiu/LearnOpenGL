package learnopengl.xiaobole.com.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.InputStream;

import learnopengl.xiaobole.com.R;
import learnopengl.xiaobole.com.drawer.BitmapDrawer;
import learnopengl.xiaobole.com.drawer.IDrawer;
import learnopengl.xiaobole.com.drawer.RectangleDrawer;
import learnopengl.xiaobole.com.drawer.TriangleDrawer;
import learnopengl.xiaobole.com.drawer.VAOTriangleDrawer;
import learnopengl.xiaobole.com.drawer.VBOTriangleDrawer;
import learnopengl.xiaobole.com.drawer.YUVImageDrawer;
import learnopengl.xiaobole.com.render.OpenGLRender;

public class GLRenderActivity extends AppCompatActivity {

    public static final int RENDER_TYPE_TRIANGLE = 0;
    public static final int RENDER_TYPE_RECTANGLE = 1;
    public static final int RENDER_TYPE_VBO_TRIANGLE = 2;
    public static final int RENDER_TYPE_VAO_TRIANGLE = 3;
    public static final int RENDER_TYPE_BITMAP = 4;
    public static final int RENDER_TYPE_YUV = 5;

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
            case RENDER_TYPE_BITMAP:
                return new BitmapDrawer().setBitmap(loadBitmap(this, R.drawable.test));
            case RENDER_TYPE_YUV:
                byte[] i420 = readYUVFrame(this, R.raw.test);
                return new YUVImageDrawer().setI420Frame(i420, 1280, 720);
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

    private static Bitmap loadBitmap(Context context, int resId) {
        /**
         * No pre-scaling
         * Android applies pre-scaling to bitmaps depending on the resolution of your device
         * and which resource folder you placed the image in. We don’t want Android to scale
         * our bitmap at all, so to be sure, we set inScaled to false.
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        /**
         * decode the image resource
         */
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    private static byte[] readYUVFrame(Context context, int resId){
        try {
            InputStream in = context.getResources().openRawResource(resId);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
