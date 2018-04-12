package learnopengl.xiaobole.com.drawer;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.FloatBuffer;

import learnopengl.xiaobole.com.utils.GLUtils;

public class VAOTriangleDrawer implements IDrawer {
    private static final int POSITION_COMPONENT_COUNT = 3;

    private int mProgramId;
    private int mPositionLocation;
    private int mColorLocation;
    private int mVAOId;
    private int mVBOId;

    private FloatBuffer mVertexData;

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "void main() {\n" +
                    "   gl_Position = aPosition;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform vec4 uColor;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = uColor;\n" +
                    "}";

    private static final float[] VERTICES = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

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
        if (mVAOId != 0) {
            GLES30.glDeleteVertexArrays(1, new int[]{mVAOId}, 0);
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
        if (mProgramId == 0) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniform4f(mColorLocation, 1.0f, 0.0f, 1.0f, 1.0f);

        setupBuffers();

        GLES20.glUseProgram(mProgramId);
        // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
        GLES30.glBindVertexArray(mVAOId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // GLES30.glBindVertexArray(0); // no need to unbind it every time
    }

    private boolean setupShaders() {
        int vertexShaderId = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShaderId = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShaderId, fragmentShaderId);
        return mProgramId != 0;
    }

    private void setupLocations() {
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "aPosition");
        mColorLocation = GLES20.glGetUniformLocation(mProgramId, "uColor");
    }

    private void setupVBOBuffers() {
        mVertexData = GLUtils.createFloatBuffer(VERTICES);
        mVBOId = GLUtils.createVBO();

        // copy the vertex data into the buffer's memory
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexData.capacity() * 4, mVertexData, GLES20.GL_STATIC_DRAW);

        // config the attribute pointer to tell OpenGL how it should interpret the vertex data
        // and enable the vertex attribute
        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's
        // bound vertex buffer object so afterwards we can safely unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void setupBuffers() {
        mVAOId = GLUtils.createVAO();
        GLES30.glBindVertexArray(mVAOId);
        setupVBOBuffers();
        // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO,
        // but this rarely happens. Modifying other VAOs requires a call to glBindVertexArray anyways
        // so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        GLES30.glBindVertexArray(0);
    }
}
