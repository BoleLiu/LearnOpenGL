package learnopengl.xiaobole.com.drawer;

import android.opengl.GLES20;

import learnopengl.xiaobole.com.utils.GLUtils;

public class OESTextureDrawer extends TextureDrawer {

    private static final String TAG = "OESTextureDrawer";

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTexCoord;\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "   gl_Position = uMVPMatrix * aPosition;\n" +
                    "   vTexCoord = (uTexMatrix * aTexCoord).xy;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" + // 声明 OES 纹理使用扩展
                    "precision mediump float;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +  // OES 纹理，接受相机纹理作为输入
                    "void main() {" +
                    "   gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                    "}";

//    "   float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;\n" +
//    "   gl_FragColor = vec4(color, color, color, 1.0);" +

    private static final float[] VERTICES = {
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, // bottom, left
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f,   // top, right
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // top, left
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f   // bottom, right
    };

    @Override
    boolean setupShaders() {
        int vertexShader = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShader = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShader, fragmentShader);
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
