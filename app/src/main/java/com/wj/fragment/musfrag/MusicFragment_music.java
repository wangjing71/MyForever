package com.wj.fragment.musfrag;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myforever.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wj.AppConstant;
import com.wj.activity.MainActivity;
import com.wj.myview.MarqueeTextView;
import com.wj.utils.CommonUtils;

import cn.pedant.SweetAlert.MySweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 汪京 on 2016/7/5.
 */
public class MusicFragment_music extends Fragment {

    private View view;
    private LayoutInflater inflater;
    private ListView mysongListView;
    private View diglogview;
    private MySongListAdapter adapter;
    private ImageView songlist;
    private SharedPreferences pref;
    private MarqueeTextView art, name;
    private int currentTime = -1;
    private playerReceiver playerReceiver;
    private int currentMusic = -1;
    private Activity activity;
    private IntentFilter filter;
    private int count = 0;
    private CircularProgressView circularProgressView;
    private int progress;
    private View musicBackgroundWindow;
    private TextView cancel;

    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//新音乐长度更新动作

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                pref.edit().putInt("currentMusic", currentMusic).commit();
                art.setText(((MainActivity) activity).mysongList.get(currentMusic).getArtist());
                name.setText(((MainActivity) activity).mysongList.get(currentMusic).getTitle());
            } else if (msg.what == 1) {
                count = count + 20;
                if (count > 100) {
                    count = 0;
                }
                progress = (int) (currentTime * 100 / ((MainActivity) activity).mysongList.get(currentMusic).getDuration());
                Log.i("=====", progress + "");
                circularProgressView.setProgress(progress);
                handler.removeMessages(1);
                handler.sendEmptyMessageDelayed(1, 1000);
            } else if (msg.what == 2) {
                MusicBackgroundWindow.getInstance().showBackgroundWindow(getContext(), musicBackgroundWindow);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_music_music1, null);
        musicBackgroundWindow = inflater.inflate(R.layout.music_background_window, null);

        initViews();
        initData();
        initEvents();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
    }

    private void initViews() {
        circularProgressView = (CircularProgressView) view.findViewById(R.id.CircularProgressView);
        songlist = (ImageView) view.findViewById(R.id.music_list);
        diglogview = inflater.inflate(R.layout.music_list, null);
        mysongListView = (ListView) diglogview.findViewById(R.id.mysonglist);
        adapter = new MySongListAdapter(getContext());
        pref = getContext().getSharedPreferences("playmusic.txt", Context.MODE_PRIVATE);
        art = (MarqueeTextView) view.findViewById(R.id.art);
        name = (MarqueeTextView) view.findViewById(R.id.name);
        playerReceiver = new playerReceiver();
        cancel = (TextView) musicBackgroundWindow.findViewById(R.id.content);
    }

    private void initData() {

        diglogview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , CommonUtils.getScreenHeight(getContext()) * 3 / 5));
        currentMusic = pref.getInt("currentMusic", -1);
        if (currentMusic != -1) {
            handler.sendEmptyMessage(0);
        }

        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction(UPDATE_ACTION);
            filter.addAction(MUSIC_CURRENT);
            filter.addAction(MUSIC_DURATION);
            getContext().registerReceiver(playerReceiver, filter);
        }

    }

    private void initEvents() {

        mysongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                currentMusic = i;
                adapter.setCurrentMusic(currentMusic);
                pref.edit().putInt("currentMusic", currentMusic).commit();

                handler.sendEmptyMessage(0);
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
                intent.putExtra("currentposition", i);
                getContext().startService(intent);

                MarqueeTextView title = (MarqueeTextView) view.findViewById(R.id.music_title);
                TextView count = (TextView) view.findViewById(R.id.count);
                TextView singer = (TextView) view.findViewById(R.id.music_singer);

                title.setTextColor(getResources().getColor(R.color.tabcolor));
                count.setTextColor(getResources().getColor(R.color.black));
                singer.setTextColor(getResources().getColor(R.color.black));
                adapter.notifyDataSetChanged();

                handler.sendEmptyMessageDelayed(2, 10000);

            }
        });


        songlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MySweetAlertDialog sd = new MySweetAlertDialog(getContext());
                sd.setCancelable(true);
                sd.setTitleText("");
                sd.setConfirmText("");
                sd.setCanceledOnTouchOutside(true);
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    SweetAlertDialog inf = new SweetAlertDialog(getContext());
                    inf.setTitleText("请先开启相关权限");
                    inf.setConfirmText("确定");
                    inf.show();
                } else {
                    adapter.setlist(((MainActivity) getActivity()).mysongList);
                    mysongListView.setAdapter(adapter);
                    sd.setView(diglogview);
                    sd.show();
                }

                mysongListView.setSelection(currentMusic);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicBackgroundWindow.getInstance().removeWindow();
            }
        });
    }


    public class playerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                handler.sendEmptyMessage(1);
                Log.i("===currentTime", Mp3Utils.formatTime(currentTime) + "");
            } else if (action.equals(UPDATE_ACTION)) {
                currentMusic = intent.getIntExtra("current", -1);
                handler.sendEmptyMessage(0);
                Log.i("===currentMusic", currentMusic + "");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(0);
        Log.i("==MusicFragment", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("==MusicFragment", "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("==MusicFragment", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("==MusicFragment", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("==MusicFragment", "onDestroy");
    }

}
