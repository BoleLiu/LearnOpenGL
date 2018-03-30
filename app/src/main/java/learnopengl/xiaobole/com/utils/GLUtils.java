package learnopengl.xiaobole.com.utils;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public class GLUtils {
    public static final String TAG = "GLUtils";

    private static final int BYTES_PER_FLOAT = 4;

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    public static int compileShader(int type, String shaderCode) {
        int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            Log.i(TAG, "Could not create new shader !");
            return 0;
        }

        // upload the source code
        glShaderSource(shaderObjectId, shaderCode);
        // compile the shader
        glCompileShader(shaderObjectId);
        Log.i(TAG, "Result of compiling source: " + glGetShaderInfoLog(shaderObjectId));
        // check compile status
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            Log.i(TAG, "Compilation of shader failed !");
            // if it failed, delete the shader object
            glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            Log.i(TAG, "Could not create new program !");
            return 0;
        }

        // attach vertex shader
        glAttachShader(programObjectId, vertexShaderId);
        // attach fragment shader
        glAttachShader(programObjectId, fragmentShaderId);
        // link program
        glLinkProgram(programObjectId);

        Log.i(TAG, "Result of linking program: " + glGetProgramInfoLog(programObjectId));
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            Log.i(TAG, "Linking of program failed !");
            return 0;
        }

        if (!validateProgram(programObjectId)) {
            return -1;
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    public static FloatBuffer createBuffer(float[] vertices) {
        FloatBuffer vertexData = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(vertices).position(0);
        return vertexData;
    }
}
