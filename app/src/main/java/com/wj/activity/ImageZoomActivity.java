package com.wj.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.myforever.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wj.libs.statusbar.StatusBarCompat;
import com.wj.myview.ZoomImageView;
import com.wj.utils.CommonUtils;


/**
 * Created by 汪京 on 2016/8/11.
 */
public class ImageZoomActivity extends Activity {

    private ZoomImageView zoomImageView;
    private long currentMS;
    private boolean mLightStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagezoom);
        setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageurl");


        zoomImageView = (ZoomImageView) findViewById(R.id.ZoomImageView);
        zoomImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));

        ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                final int W = CommonUtils.getScreenWidth(ImageZoomActivity.this);
                zoomImageView.setImageBitmap(zoomImage(loadedImage,W,loadedImage.getHeight() * W / loadedImage.getWidth()));
            }

        });

        zoomImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentMS = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        long moveTime = System.currentTimeMillis() - currentMS;
                        if(moveTime<200){
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    private void setStatusBarColor(@ColorInt int color) {
        StatusBarCompat.setStatusBarColor(this, color, mLightStatusBar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return false;
    }
}
