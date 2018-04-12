package learnopengl.xiaobole.com.drawer;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import learnopengl.xiaobole.com.utils.GLUtils;

public class RectangleDrawer implements IDrawer {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private int mProgramId;
    private int mPositionLocation;
    private int mColorLocation;

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;

    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;\n" +
                    "void main() {\n" +
                    "   gl_Position = a_position;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform vec4 u_color;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = u_color;\n" +
                    "}";

    private static final float[] VERTICES = {
            -0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private static final short[] INDICES = {
            0, 1, 2,
            0, 3, 1
    };

    @Override
    public void init() {
        setupShaders();
        setupLocations();
        GLES20.glUseProgram(mProgramId);
    }

    @Override
    public void release() {
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

        setupBuffer();
        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        GLES20.glUniform4f(mColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionLocation);
    }

    private boolean setupShaders() {
        int vertexShaderId = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShaderId = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShaderId, fragmentShaderId);
        return mProgramId != 0;
    }

    private void setupLocations() {
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "a_position");
        mColorLocation = GLES20.glGetUniformLocation(mProgramId, "u_color");
    }

    private void setupBuffer() {
        if (mVertexBuffer == null || mIndexBuffer == null) {
            mVertexBuffer = GLUtils.createFloatBuffer(VERTICES);
            mIndexBuffer = GLUtils.createShortBuffer(INDICES);
        }
    }
}
