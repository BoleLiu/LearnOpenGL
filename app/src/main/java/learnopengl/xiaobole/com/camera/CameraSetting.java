package learnopengl.xiaobole.com.camera;

public class CameraSetting {

    /**
     * the enum camera facing id
     */
    public enum CAMERA_FACING_ID {
        CAMERA_FACING_BACK,
        CAMERA_FACING_FRONT,
        CAMERA_FACING_3RD
    }

    /**
     * the enum camera preview ratio
     */
    public enum CAMERA_PREVIEW_RATIO {
        RATIO_4_3,
        RATIO_16_9
    }

    /**
     * the enum preview size level
     */
    public enum CAMERA_PREVIEW_SIZE_LEVEL {
        /**
         * Preview size level 120 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_120P,
        /**
         * Preview size level 240 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_240P,
        /**
         * Preview size level 360 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_360P,
        /**
         * Preview size level 480 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_480P,
        /**
         * Preview size level 720 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_720P,
        /**
         * Preview size level 960 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_960P,
        /**
         * Preview size level 1080 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_1080P,
        /**
         * Preview size level 1200 p camera preview size level.
         */
        PREVIEW_SIZE_LEVEL_1200P,
    }

    private static final int[] PREVIEW_SIZE_LEVEL_ARRAY = {
            120, 240, 360, 480, 720, 960, 1080, 1200
    };

    private CAMERA_FACING_ID mCameraFacingId = CAMERA_FACING_ID.CAMERA_FACING_BACK;
    private CAMERA_PREVIEW_RATIO mPreviewSizeRatio = CAMERA_PREVIEW_RATIO.RATIO_16_9;
    private CAMERA_PREVIEW_SIZE_LEVEL mPreviewSizeLevel = CAMERA_PREVIEW_SIZE_LEVEL.PREVIEW_SIZE_LEVEL_480P;

    /**
     * set the request camera id
     *
     * @param reqCamId the request camera id
     * @return the CameraSetting object
     */
    public CameraSetting setCameraId(CAMERA_FACING_ID reqCamId) {
        mCameraFacingId = reqCamId;
        return this;
    }

    /**
     * set the request preview size ratio
     *
     * @param ratio the target ratio
     * @return the CameraSetting object
     */
    public CameraSetting setCameraPreviewSizeRatio(CAMERA_PREVIEW_RATIO ratio) {
        mPreviewSizeRatio = ratio;
        return this;
    }

    /**
     * set the request preview size level
     *
     * @param level the request level
     * @return the CameraSetting object
     */
    public CameraSetting setCameraPreviewSizeLevel(CAMERA_PREVIEW_SIZE_LEVEL level) {
        mPreviewSizeLevel = level;
        return this;
    }

    /**
     * get the request camera facing id
     *
     * @return the request camera facing id
     */
    public int getCameraId() {
        return mCameraFacingId.ordinal();
    }

    /**
     * get the preview size ratio
     *
     * @return the preview size ratio
     */
    public CAMERA_PREVIEW_RATIO getPreviewSizeRatio() {
         return mPreviewSizeRatio;
    }

    /**
     * get the preview size level
     *
     * @return the preview size level
     */
    public CAMERA_PREVIEW_SIZE_LEVEL getPreviewSizeLevel() {
        return mPreviewSizeLevel;
    }

    public static double calcCameraPreviewSizeRatio(CAMERA_PREVIEW_RATIO ratio) {
        double targetRatio;
        switch (ratio) {
            case RATIO_4_3:
                targetRatio = (double) 4 / 3;
                break;
            case RATIO_16_9:
                targetRatio = (double) 16 / 9;
                break;
            default:
                throw new IllegalArgumentException("cannot support ratio:" + ratio);
        }
        return targetRatio;
    }

    public static int calcCameraPreviewSizeLevel(CAMERA_PREVIEW_SIZE_LEVEL level) {
        return PREVIEW_SIZE_LEVEL_ARRAY[level.ordinal()];
    }
}
