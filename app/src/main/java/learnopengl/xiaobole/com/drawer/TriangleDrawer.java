package learnopengl.xiaobole.com.drawer;

import java.nio.FloatBuffer;

import learnopengl.xiaobole.com.utils.GLUtils;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class TriangleDrawer implements IDrawer {
    private static final int POSITION_COMPONENT_COUNT = 3;

    private int mProgramId;
    private int mPositionLocation;
    private int mColorLocation;

    private FloatBuffer mVertexData;

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
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

    @Override
    public void init() {
        if (setupShaders()) {
            glUseProgram(mProgramId);
        }
        setupLocations();
    }

    @Override
    public void release() {
        if (mProgramId != 0) {
            glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }

    @Override
    public void setViewPort(int x, int y, int width, int height) {
        glViewport(x, y, width, height);
    }

    @Override
    public void draw() {
        if (mProgramId == 0) {
            return;
        }

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        setupBuffer();
        glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, mVertexData);
        glEnableVertexAttribArray(mPositionLocation);

        glUniform4f(mColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glDisableVertexAttribArray(mPositionLocation);
    }

    private boolean setupShaders() {
        int vertexShaderId = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShaderId = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShaderId, fragmentShaderId);
        return mProgramId != 0;
    }

    private void setupLocations() {
        mPositionLocation = glGetAttribLocation(mProgramId, "a_position");
        mColorLocation = glGetUniformLocation(mProgramId, "u_color");
    }

    private void setupBuffer() {
        if (mVertexData == null) {
            mVertexData = GLUtils.createFloatBuffer(VERTICES);
        }
    }
}
