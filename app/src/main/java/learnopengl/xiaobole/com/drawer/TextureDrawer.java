package learnopengl.xiaobole.com.drawer;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import learnopengl.xiaobole.com.utils.GLUtils;
import learnopengl.xiaobole.com.utils.ImageMatrix;

public abstract class TextureDrawer implements IDrawer {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXCOORD_COMPONENT_COUNT = 2;

    int mProgramId;
    int mPositionLocation;
    int mTexCoordLocation;
    int mMVPMatrixLocation;
    int mTexMatrixLocation;
    int mSamplerLocation;

    private int mVAOId;
    private int mVBOId;
    private int mEBOId;

    private FloatBuffer mVertexData;
    private ShortBuffer mIndexBuffer;

    private ImageMatrix mImageMatrix = new ImageMatrix();

    private static final short[] INDICES = {
            0, 1, 2,
            0, 3, 1
    };

    abstract boolean setupShaders();
    abstract void setupLocations();
    abstract float[] getVertices();

    @Override
    public void init() {
        setupShaders();
        setupLocations();
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
        GLES20.glViewport(x, y, width, height);
    }

    @Override
    public void draw() {
        draw(-1);
    }

    public void draw(int texId) {
        draw(texId, null);
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

    private void setupBuffers() {
        mVAOId = GLUtils.createVAO();
        GLES30.glBindVertexArray(mVAOId);
        setupVBOBuffers();
        setupEBOBuffers();
        GLES30.glBindVertexArray(0);
    }

    private void setupVBOBuffers() {
        if (mVertexData == null) {
            mVertexData = GLUtils.createFloatBuffer(getVertices());
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
