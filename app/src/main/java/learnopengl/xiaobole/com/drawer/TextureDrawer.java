package learnopengl.xiaobole.com.drawer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import learnopengl.xiaobole.com.utils.GLUtils;
import learnopengl.xiaobole.com.utils.ImageMatrix;

public class TextureDrawer implements IDrawer {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXCOORD_COMPONENT_COUNT = 2;

    private int mProgramId;
    private int mPositionLocation;
    private int mTexCoordLocation;
    private int mMVPMatrixLocation;
    private int mTexMatrixLocation;
    private int mSamplerLocation;

    private int mTexId;

    private int mVAOId;
    private int mVBOId;
    private int mEBOId;

    private FloatBuffer mVertexData;
    private ShortBuffer mIndexBuffer;

    private Bitmap mBitmap;

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

    private static final short[] INDICES = {
            0, 1, 2,
            0, 3, 1
    };

    public TextureDrawer setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    @Override
    public void init() {
        setupShaders();
        setupLocations();
        mTexId = GLUtils.createTexture();
    }

    @Override
    public void release() {
        if (mVBOId != 0) {
            GLES20.glDeleteBuffers(1, new int[]{mVBOId}, 0);
            mVBOId = 0;
        }
        if (mEBOId != 0) {
            GLES20.glDeleteBuffers(1, new int[]{mEBOId}, 0);
        }
        if (mVAOId != 0) {
            GLES20.glDeleteBuffers(1, new int[]{mVAOId}, 0);
            mVAOId = 0;
        }
        if (mProgramId != 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }

    @Override
    public void setViewPort(int x, int y, int width, int height) {
        Log.i("liujingbo", "x = " + x + " y = " + y + " width = " + width + " height = " + height);
        GLES20.glViewport(x, y, width, height);
        mImageMatrix.setViewport(mBitmap.getWidth(), mBitmap.getHeight(), width, height);
    }

    @Override
    public void draw() {
        draw(mTexId, mImageMatrix.getMVPMatrix(), null);
    }

    public void draw(int texId, float[] mvpMatrix) {
        draw(texId, mvpMatrix, null);
    }

    public void draw(int texId, float[] mvpMatrix, float[] texMatrix) {
        if (mProgramId == 0) {
            return;
        }

        if (mvpMatrix == null) {
            mvpMatrix = GLUtils.IDENTITY_MATRIX;
        }

        if (texMatrix == null) {
            texMatrix = GLUtils.IDENTITY_MATRIX;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLUtils.drawBitmap(texId, mBitmap);
        setupBuffers();

        GLES20.glUniformMatrix4fv(mMVPMatrixLocation, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(mTexMatrixLocation, 1, false, texMatrix, 0);

        GLES20.glUniform1i(mSamplerLocation, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        GLES20.glUseProgram(mProgramId);
        GLES30.glBindVertexArray(mVAOId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);
    }

    private boolean setupShaders() {
        int vertexShaderId = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShaderId = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShaderId, fragmentShaderId);
        return mProgramId != 0;
    }

    private void setupLocations() {
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "aPosition");
        mTexCoordLocation = GLES20.glGetAttribLocation(mProgramId, "aTexCoord");
        mMVPMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uMVPMatrix");
        mTexMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uTexMatrix");
        mSamplerLocation = GLES20.glGetUniformLocation(mProgramId, "sTexture");
    }

    private void setupBuffers() {
        mVAOId = GLUtils.createVAO();
        GLES30.glBindVertexArray(mVAOId);
        setupVBOBuffers();
        setupEBOBuffers();
        GLES30.glBindVertexArray(0);
    }

    private void setupVBOBuffers() {
        if (mVertexData == null) {
            mVertexData = GLUtils.createFloatBuffer(VERTICES);
        }
        mVBOId = GLUtils.createVBO();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexData.capacity() * 4, mVertexData, GLES20.GL_STATIC_DRAW);

        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 5 * 4, 0);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        GLES20.glVertexAttribPointer(mTexCoordLocation, TEXCOORD_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);
        GLES20.glEnableVertexAttribArray(mTexCoordLocation);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void setupEBOBuffers() {
        if (mIndexBuffer == null) {
            mIndexBuffer = GLUtils.createShortBuffer(INDICES);
        }

        mEBOId = GLUtils.createEBO();
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mEBOId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity() * 2, mIndexBuffer, GLES20.GL_STATIC_DRAW);
    }
}
