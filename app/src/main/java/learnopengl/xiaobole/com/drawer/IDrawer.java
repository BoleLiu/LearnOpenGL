package learnopengl.xiaobole.com.drawer;

public interface IDrawer {

    /**
     * Init
     */
    void init();

    /**
     * Release
     */
    void release();

    /**
     * set view port
     *
     * @param x the x
     * @param y the y
     * @param width the width
     * @param height the height
     */
    void setViewPort(int x, int y, int width, int height);

    /**
     * Do draw
     */
    void draw();
}
