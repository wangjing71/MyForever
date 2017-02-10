package com.wj.fragment.musfrag;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myforever.R;
import com.wj.myview.MarqueeTextView;

import java.util.ArrayList;

/**
 * Created by 汪京 on 2016/7/18.
 */
public class MySongListAdapter extends BaseAdapter {

    private ArrayList<Mp3Info> list;
    private LayoutInflater inflater;
    private Context context;
    private ListView mlistView;
    private int currentMusic ;

    public MySongListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        currentMusic = context.getSharedPreferences("playmusic.txt", Context.MODE_PRIVATE).getInt("currentMusic",-1);
    }

    public void setCurrentMusic(int currentMusic) {
        this.currentMusic = currentMusic;
    }

    public void setlist(ArrayList<Mp3Info> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int i, View view, final ViewGroup viewGroup) {
        if(mlistView == null){
            mlistView = (ListView) viewGroup;
        }

        final ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.music_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (MarqueeTextView) view.findViewById(R.id.music_title);
            viewHolder.count = (TextView) view.findViewById(R.id.count);
            viewHolder.singer = (TextView) view.findViewById(R.id.music_singer);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(list.get(i).getTitle());
        viewHolder.singer.setText(list.get(i).getArtist());
        viewHolder.count.setText((i + 1) + "");
        if(i == currentMusic){
            viewHolder.title.setTextColor(ContextCompat.getColor(context,R.color.tabcolor));
            viewHolder.singer.setTextColor(ContextCompat.getColor(context,R.color.tabcolor));
            viewHolder.count.setTextColor(ContextCompat.getColor(context,R.color.tabcolor));
        }else{
            viewHolder.title.setTextColor(ContextCompat.getColor(context,R.color.music_list));
            viewHolder.singer.setTextColor(ContextCompat.getColor(context,R.color.music_list));
            viewHolder.count.setTextColor(ContextCompat.getColor(context,R.color.music_list));
        }
        return view;
    }


    class ViewHolder {
        TextView count;
        TextView singer;
        MarqueeTextView title;
    }
}
