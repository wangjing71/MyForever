package com.wj.fragment.musfrag;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

/**
 * Created by 汪京 on 2016/8/3.
 */
public class PlayMusciThread extends Thread {

    private static PlayMusciThread playMusciThread;
    private MediaPlayer mediaPlayer;
    private String path;
    private Context context;
    private int duration;           //播放总长度
    private int currentTime = 0;

    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//新音乐长度更新动作

    //私有化构造方法
    private PlayMusciThread(){

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public static PlayMusciThread getInstance(){
        if(playMusciThread == null){
            synchronized (PlayMusciThread.class){
                playMusciThread = new PlayMusciThread();
            }
        }
        return  playMusciThread;
    }

    @Override
    public void run() {
        super.run();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class PreparedListener implements MediaPlayer.OnPreparedListener {
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
            context.sendBroadcast(intent);
        }
    }


}
