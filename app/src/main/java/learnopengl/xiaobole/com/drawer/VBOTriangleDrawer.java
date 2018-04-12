package learnopengl.xiaobole.com.drawer;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import learnopengl.xiaobole.com.utils.GLUtils;

public class VBOTriangleDrawer implements IDrawer {
    private static final int POSITION_COMPONENT_COUNT = 3;

    private int mProgramId;
    private int mPositionLocation;
    private int mColorLocation;
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
        setupBuffers();
    }

    @Override
    public void release() {
        if (mVBOId != 0) {
            GLES20.glDeleteBuffers(1, new int[]{mVBOId}, 0);
            mVBOId = 0;
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

        GLES20.glUseProgram(mProgramId);

        // Clear the rendering surface
        // This will wipe out all colors on the screen and fill the screen with
        // the color previously defined by our call to glClearColor().
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniform4f(mColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);

        // Enable & use the VBO which will improve the rendering efficiency
        GLES20.glEnableVertexAttribArray(mPositionLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOId);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // Reset the OpenGL state
        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
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

    private void setupBuffers() {
        mVertexData = GLUtils.createFloatBuffer(VERTICES);
        mVBOId = GLUtils.createVBO();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexData.capacity() * 4, mVertexData, GLES20.GL_STATIC_DRAW);

        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
