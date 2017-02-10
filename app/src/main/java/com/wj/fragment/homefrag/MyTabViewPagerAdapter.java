package com.wj.fragment.homefrag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.example.administrator.myforever.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/16.
 */
public class MyTabViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> viewlist;
    private ArrayList<String> images;
    private Context context;

    public void setViewlist(ArrayList<View> viewlist) {
        this.viewlist = viewlist;
    }

    public void setimageUrl(ArrayList<String> images) {
        this.images = images;
    }

    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        position %= viewlist.size();
        if (position < 0) {
            position = viewlist.size() + position;
        }
        View view = viewlist.get(position);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        final CircularProgressView progressBar = (CircularProgressView) view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.startAnimation();
        progressBar.setCycleColor(Color.parseColor("#008000"));


        final int finalPosition = position;
        ImageLoader.getInstance().displayImage(images.get(position % viewlist.size()), iv, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                Log.i("====", "开始加载第 " + finalPosition + "  张");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Log.i("====", "加载失败");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.i("====", "完成加载第 " + finalPosition + "  张");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;

    }

    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}
