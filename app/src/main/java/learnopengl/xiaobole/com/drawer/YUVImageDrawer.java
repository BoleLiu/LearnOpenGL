package learnopengl.xiaobole.com.drawer;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import learnopengl.xiaobole.com.utils.GLUtils;

public class YUVImageDrawer implements IDrawer {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXCOORD_COMPONENT_COUNT = 2;

    private int mPositionLocation;
    private int mTexCoordinateLocation;
    private int mMVPMatrixLocation;
    private int mProgramId;

    private int mVAOId;
    private int mVBOId;
    private int mEBOId;

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mCameraMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private int[] mTextures = {-1, -1, -1};

    private ByteBuffer[] mYUVFrame;

    private int mFrameWidth;
    private int mFrameHeight;

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTexCoordinate;\n" +
                    "varying vec2 vTexCoordinate;\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "void main() {\n" +
                    "   vTexCoordinate = aTexCoordinate;\n" +
                    "   gl_Position = uMVPMatrix * aPosition;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec2 vTexCoordinate;\n" +
                    "uniform sampler2D yTexture;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "uniform sampler2D vTexture;\n" +
                    "void main() {\n" +
                    "   float y = texture2D(yTexture, vTexCoordinate).r;\n" +
                    "   float u = texture2D(uTexture, vTexCoordinate).r - 0.5;\n" +
                    "   float v = texture2D(vTexture, vTexCoordinate).r - 0.5;\n" +
                    "   gl_FragColor = vec4(y + 1.403 * v, " +
                    "                       y - 0.344 * u - 0.714 * v, " +
                    "                       y + 1.77 * u, 1);\n" +
                    "}";

    /**
     * OpenGL 顶点坐标系统
     * (-1,  1) (0,  1) (1, 1)
     * (-1,  0) (0,  0) (1, 0)
     * (-1, -1) (0, -1) (1, -1)
     *
     * OpenGL 纹理坐标系统
     * (0, 1) (1, 1)
     * (0, 0) (1, 0)
     * 纹理坐标必须与顶点坐标一一对应
     * 由于 Android 的图片系统，y 轴是向下的，而纹理坐标的 y 轴是向上的
     * 因此，定点坐标的 bottom 对应纹理坐标的 top
     */
    private static final float[] VERTICES = {
            -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, // bottom, left
            1.0f, 1.0f, 0.0f, 1.0f, 0.0f,   // top, right
            -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,  // top, left
            1.0f, -1.0f, 0.0f, 1.0f, 1.0f   // bottom, right
    };

    private final short[] INDICES = {
            0, 1, 2,
            0, 3, 1
    };

    public YUVImageDrawer() {}

    public YUVImageDrawer setI420Frame(byte[] i420, int width, int height) {
        if (i420 == null || i420.length < 3 || width < 0 || height < 0) {
            return null;
        }
        int ySize = width * height;
        if (mYUVFrame == null) {
            mYUVFrame = new ByteBuffer[3];
            mYUVFrame[0] = ByteBuffer.allocateDirect(ySize);
            mYUVFrame[1] = ByteBuffer.allocateDirect(ySize / 4);
            mYUVFrame[2] = ByteBuffer.allocateDirect(ySize / 4);
        }

        int offset = 0;
        mYUVFrame[0].put(i420, offset, ySize);
        offset += ySize;
        mYUVFrame[1].put(i420, offset, ySize / 4);
        offset += ySize / 4;
        mYUVFrame[2].put(i420, offset, ySize / 4);
        for (int i = 0; i < 3; i++) {
            mYUVFrame[i].position(0);
        }
        mFrameWidth = width;
        mFrameHeight = height;
        return this;
    }

    @Override
    public void init() {
        setupShaders();
        setupLocations();
        generateTextures();
    }

    @Override
    public void release() {
        if (mVBOId != 0) {
            GLES20.glDeleteBuffers(1, new int[]{mVBOId}, 0);
            mVBOId = 0;
        }
        if (mEBOId != 0) {
            GLES20.glDeleteBuffers(1, new int[]{mEBOId}, 0);
            mEBOId = 0;
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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glViewport(0, 0, width, height);
        float imageRatio = mFrameWidth / (float) mFrameHeight;
        float screenRatio = width / (float) height;
        if (width > height) {
            if (imageRatio > screenRatio) {
                Matrix.orthoM(mProjectionMatrix, 0, -screenRatio * imageRatio, screenRatio * imageRatio, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -screenRatio / imageRatio, screenRatio / imageRatio, -1, 1, 3, 7);
            }
        } else {
            if (imageRatio > screenRatio) {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1 / screenRatio * imageRatio, 1 / screenRatio * imageRatio, 3, 7);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -imageRatio / screenRatio, imageRatio / screenRatio, 3, 7);
            }
        }
        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);
    }

    @Override
    public void draw() {
        if (mProgramId == 0) {
            return;
        }
        GLES20.glUseProgram(mProgramId);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniformMatrix4fv(mMVPMatrixLocation, 1, false, mMVPMatrix, 0);

        /**
         * 启用 2D 纹理功能
         */
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        for (int i = 0; i < 3; i++)  {
            /**
             * 决定 YUV 分量的宽高
             */
            int w = (i == 0) ? mFrameWidth : mFrameWidth / 2;
            int h = (i == 0) ? mFrameHeight : mFrameHeight / 2;

            /**
             * 设置当前操作的纹理对象
             */
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[i]);

            /**
             * 设置如何把纹理像素映射成像素
             * @param GL_TEXTURE_2D 操作 2D 的纹理
             * @param GL_TEXTURE_MIN_FILTER 设置缩小的过滤方式
             * @param GL_TEXTURE_MAG_FILTER 设置放大的过滤方式
             * @param GL_TEXTURE_WRAP_S S 方向上的贴图模式
             * @param GL_TEXTURE_WRAP_T T 方向上的贴图模式
             */
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            /**
             * 纹理贴图
             * @param target 纹理类型, GLES20.GL_TEXTURE_2D 代表 2D 的纹理
             * @param level  代表图像的详细程度, 默认为 0 即可
             * @param internalformat 指定了纹理存储在显存中的内部格式，GL_LUMINANCE 代表灰度值
             * @param width 纹理的宽度，必须是 2 的整数次幂
             * @param height 纹理的高度，必须是 2 的整数次幂
             * @param border 无边框取值为 0，有边框取值为 1，边框的颜色由 GL_TEXTURE_BORDER_COLOR 选项设置
             * @param format 图像数组中的像素格式，GL_RGB 代表 RGB，GL_LUMINANCE 代表 YUV
             * @param type 图像数组中的数据类型，GL_UNSIGNED_BYTE 代表无符号整型
             * @param pixels 图像数组
             */
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
                    w, h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mYUVFrame[i]);
        }

        setupBuffers();
        GLES30.glBindVertexArray(mVAOId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
    }

    private boolean setupShaders() {
        int vertexShader = GLUtils.compileVertexShader(VERTEX_SHADER);
        int fragmentShader = GLUtils.compileFragmentShader(FRAGMENT_SHADER);
        mProgramId = GLUtils.linkProgram(vertexShader, fragmentShader);
        return mProgramId != 0;
    }

    private void setupLocations() {
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "aPosition");
        mTexCoordinateLocation = GLES20.glGetAttribLocation(mProgramId, "aTexCoordinate");
        mMVPMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uMVPMatrix");
    }

    private void setupBuffers() {
        mVAOId = GLUtils.createVAO();
        GLES30.glBindVertexArray(mVAOId);
        setupVBOBuffers();
        setupEBOBuffers();
        GLES30.glBindVertexArray(0);
    }

    private void setupVBOBuffers() {
        if (mVertexBuffer == null) {
            mVertexBuffer = GLUtils.createFloatBuffer(VERTICES);
        }
        mVBOId = GLUtils.createVBO();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 5 * 4, 0);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        GLES20.glVertexAttribPointer(mTexCoordinateLocation, TEXCOORD_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);
        GLES20.glEnableVertexAttribArray(mTexCoordinateLocation);

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

    private void generateTextures() {
        /**
         * 创建 3 个纹理对象
         */
        mTextures = GLUtils.createTextures(3);

        /**
         * 设置 shader 中的 sampler2D 变量从哪个纹理单元采样，
         * 即对应 GLES20.GL_TEXTURE0 + x
         */
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramId, "yTexture"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramId, "uTexture"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgramId, "vTexture"), 2);
    }
}
