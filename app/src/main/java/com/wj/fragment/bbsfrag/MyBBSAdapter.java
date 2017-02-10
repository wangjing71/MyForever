package com.wj.fragment.bbsfrag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myforever.R;


/**
 * Created by 汪京 on 2016/7/7.
 */
public class MyBBSAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    public MyBBSAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 20;
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
            view = inflater.inflate(R.layout.fragment_bbs_item, null);
            viewHolder = new ViewHolder();
            viewHolder.touxiang = (ImageView) view.findViewById(R.id.touxiang);
            viewHolder.pin = (ImageView) view.findViewById(R.id.pin);
            viewHolder.zan = (ImageView) view.findViewById(R.id.zan);
            viewHolder.author = (TextView) view.findViewById(R.id.author);
            viewHolder.past = (TextView) view.findViewById(R.id.past);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }



        return view;
    }


    class ViewHolder {
        ImageView touxiang;
        ImageView zan;
        ImageView pin;
        TextView author;
        TextView past;
        TextView title;
        TextView content;
    }
}
