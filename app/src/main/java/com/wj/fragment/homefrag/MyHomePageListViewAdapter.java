package com.wj.fragment.homefrag;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.myforever.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wj.bean.HomePageResult;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MyHomePageListViewAdapter extends BaseAdapter {

    private HomePageResult homePageResult;
    private LayoutInflater inflater;

    public MyHomePageListViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setHomePageResult(HomePageResult homePageResult) {
        this.homePageResult = homePageResult;
    }

    @Override
    public int getCount() {
        if(homePageResult == null){
            return 0;
        }else{
            return homePageResult.getHomepage().size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.homepage_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        ImageLoader.getInstance().displayImage(homePageResult.getHomepage().get(i),  viewHolder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        return view;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
