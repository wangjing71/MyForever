package com.wj.fragment.musfrag;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.wj.AppConstant;

import java.util.ArrayList;

/**
 * Created by 汪京 on 2016/7/25.
 */
public class PlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private String path;
    private boolean isPause;
    private ArrayList<Mp3Info> mysongList;
    private int status = 2;         //播放状态，默认为顺序播放
    private MyReceiver myReceiver;  //自定义广播接收器
    private int currentTime;        //当前播放进度
    private int duration;           //播放长度
    private int current = 0; // 记录当前正在播放的音乐

    //服务要发送的一些Action
    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//新音乐长度更新动作

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                handler.removeMessages(1);
                if (mediaPlayer != null) {
                    currentTime = mediaPlayer.getCurrentPosition();// 获取当前音乐播放的位置
                    Intent intent = new Intent();
                    intent.setAction(MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentTime);
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service", "service created");
        mediaPlayer = new MediaPlayer();
        //获取歌曲列表媒体库
        if (mysongList == null) {
            mysongList = (ArrayList<Mp3Info>) Mp3Utils.getMp3Infos(PlayerService.this);
        }

        /**
         * 设置音乐播放完成时的监听器
         * 此处根据播放状态对音乐进行操作
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (status == 1) {         //单曲循环
                    mediaPlayer.start();
                } else if (status == 2) {   //全部循环
                    current++;
                    if (current > mysongList.size() - 1) {  //变为第一首的位置继续播放
                        current = 0;
                    }
                    Intent sendIntent = new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current", current);
                    // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                    sendBroadcast(sendIntent);
                    path = mysongList.get(current).getUrl();
                    play(0);
                } else if (status == 3) {     // 顺序播放
                    current++;  //下一首位置
                    if (current <= mysongList.size() - 1) {
                        Intent sendIntent = new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                        path = mysongList.get(current).getUrl();
                        play(0);
                    } else {
                        mediaPlayer.seekTo(0);
                        current = 0;
                        Intent sendIntent = new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                    }

                } else if (status == 4) { //随机播放
                    current = getRandomIndex(mysongList.size() - 1);
                    Log.i("=====currentIndex:", current + "");
                    Intent sendIntent = new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current", current);
                    // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                    sendBroadcast(sendIntent);
                    path = mysongList.get(current).getUrl();
                    play(0);
                }
            }
        });

        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicFragment_music.CTL_ACTION);
        registerReceiver(myReceiver, filter);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mediaPlayer.isPlaying()) {
            stop();
        }

        int msg = intent.getIntExtra("MSG", 0);
        current = intent.getIntExtra("currentposition", -1);
        path = mysongList.get(current).getUrl();

        if (msg == AppConstant.PlayerMsg.PLAY_MSG) {    //直接播放音乐
            play(0);
        } else if (msg == AppConstant.PlayerMsg.PAUSE_MSG) {    //暂停
            pause();
        } else if (msg == AppConstant.PlayerMsg.STOP_MSG) {     //停止
            stop();
        } else if (msg == AppConstant.PlayerMsg.CONTINUE_MSG) { //继续播放
            resume();
        } else if (msg == AppConstant.PlayerMsg.PRIVIOUS_MSG) { //上一首
            previous();
        } else if (msg == AppConstant.PlayerMsg.NEXT_MSG) {     //下一首
            next();
        } else if (msg == AppConstant.PlayerMsg.PROGRESS_CHANGE) {  //进度更新
            currentTime = intent.getIntExtra("progress", -1);
            play(currentTime);
        } else if (msg == AppConstant.PlayerMsg.PLAYING_MSG) {
            handler.sendEmptyMessage(1);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 上一首
     */
    private void previous() {
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }

    /**
     * 下一首
     */
    private void next() {
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }

    /**
     * 播放音乐
     *
     * @param position
     */
    private void play(int position) {

        PlayMusciThread playMusciThread = PlayMusciThread.getInstance();
        playMusciThread.setMediaPlayer(mediaPlayer);
        playMusciThread.setPath(path);
        playMusciThread.setContext(PlayerService.this);
        playMusciThread.setCurrentTime(position);
        playMusciThread.run();
        handler.sendEmptyMessage(1);

//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepare();
//            mediaPlayer.setOnPreparedListener(new PreparedListener(position));
//            handler.sendEmptyMessage(1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    private void resume() {
        if (isPause) {
            mediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 停止音乐
     */
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    private final class PreparedListener implements OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();    //开始播放
            if (currentTime > 0) {    //如果音乐不是从头播放
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(MUSIC_DURATION);
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度
            sendBroadcast(intent);
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case 1:
                    status = 1; // 将播放状态置为1表示：单曲循环
                    break;
                case 2:
                    status = 2; //将播放状态置为2表示：全部循环
                    break;
                case 3:
                    status = 3; //将播放状态置为3表示：顺序播放
                    break;
                case 4:
                    status = 4; //将播放状态置为4表示：随机播放
                    break;
            }
        }
    }

    protected int getRandomIndex(int end) {
        int index = (int) (Math.random() * end);
        return index;
    }

}