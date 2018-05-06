package learnopengl.xiaobole.com.activity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import learnopengl.xiaobole.com.R;
import learnopengl.xiaobole.com.drawer.IDrawer;
import learnopengl.xiaobole.com.drawer.TriangleDrawer;
import learnopengl.xiaobole.com.render.OpenGLRender;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickTriangle(View v) {
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_TRIANGLE);
    }

    public void onClickRectangle(View v) {
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_RECTANGLE);
    }

    public void onClickVBOTriangle(View v) {
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_VBO_TRIANGLE);
    }

    public void onClickVAOTriangle(View v) {
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_VAO_TRIANGLE);
    }

    public void onClickTexture(View v) {
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_TEXTURE);
    }

    private void jumpToRenderActivity(int renderType) {
        Intent intent = new Intent(MainActivity.this, GLRenderActivity.class);
        intent.putExtra("RenderType", renderType);
        startActivity(intent);
    }
}
