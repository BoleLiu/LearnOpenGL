package learnopengl.xiaobole.com.drawer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import learnopengl.xiaobole.com.utils.GLUtils;
import learnopengl.xiaobole.com.utils.ImageMatrix;

public class BitmapDrawer extends TextureDrawer {
    private static final String TAG = "BitmapDrawer";

    private Bitmap mBitmap;

    private int mTexId = -1;
    private ImageMatrix mImageMatrix = new ImageMatrix();

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTexCoord;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;" +
                    "void main() {\n" +
                    "   gl_Position = uMVPMatrix * aPosition;\n" +
                    "   vTexCoord = (uTexMatrix * aTexCoord).xy;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                    "}";

    private static final float[] VERTICES = {
            -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, // bottom, left
            1.0f, 1.0f, 0.0f, 1.0f, 0.0f,   // top, right
            -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,  // top, left
            1.0f, -1.0f, 0.0f, 1.0f, 1.0f   // bottom, right
    };

    public BitmapDrawer setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    @Override
    public void init() {
        super.init();
        mTexId = GLUtils.createTexture();
    }

    @Override
    public void setViewPort(int x, int y, int width, int height) {
        if (mBitmap == null) {
            Log.e(TAG, "must setBitmap first !");
            return;
        }
        super.setViewPort(x, y, width, height);
        mImageMatrix.setViewport(mBitmap.getWidth(), mBitmap.getHeight(), width, height);
    }

    @Override
    public void draw() {
        if (mBitmap == null) {
            Log.e(TAG, "must setBitmap first !");
            return;
        }
        GLUtils.drawBitmap(mTexId, mBitmap);
        super.draw(mTexId, mImageMatrix.getMVPMatrix());
    }

    @Override
    boolean setupShaders() {
        int vertexShaderId = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShaderId = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShaderId, fragmentShaderId);
        return mProgramId != 0;
    }

    @Override
    void setupLocations() {
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "aPosition");
        mTexCoordLocation = GLES20.glGetAttribLocation(mProgramId, "aTexCoord");
        mMVPMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uMVPMatrix");
        mTexMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uTexMatrix");
        mSamplerLocation = GLES20.glGetUniformLocation(mProgramId, "sTexture");
    }

    @Override
    float[] getVertices() {
        return VERTICES;
    }
}
