package learnopengl.xiaobole.com.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import learnopengl.xiaobole.com.R;

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
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_BITMAP);
    }

    public void onClickYUV(View v) {
        jumpToRenderActivity(GLRenderActivity.RENDER_TYPE_YUV);
    }

    public void onClickCamera(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            return;
        }
        Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
        startActivity(intent);
    }

    private void jumpToRenderActivity(int renderType) {
        Intent intent = new Intent(MainActivity.this, GLRenderActivity.class);
        intent.putExtra("RenderType", renderType);
        startActivity(intent);
    }
}
