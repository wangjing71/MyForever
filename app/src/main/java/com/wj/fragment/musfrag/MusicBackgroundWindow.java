package com.wj.fragment.musfrag;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 汪京 on 2016/8/26.
 */
//后台悬浮窗口界面
public class MusicBackgroundWindow {

    public static Boolean isShown = false;
    private static Context mContext = null;
    private static WindowManager mWindowManager = null;
    private static MusicBackgroundWindow musicBackgroundWindow ;


    private MusicBackgroundWindow(){
    }

    public static MusicBackgroundWindow getInstance(){
        if(musicBackgroundWindow == null){
            synchronized (MusicBackgroundWindow.class){
                musicBackgroundWindow = new MusicBackgroundWindow();
            }
        }
        return  musicBackgroundWindow;
    }

    private View view;
    public  void showBackgroundWindow(final Context context,View view){
        this.view = view ;
        if(isShown){
            return;
        }
        isShown = true;
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.flags = flags;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        mWindowManager.addView(this.view, params);
    }

    public void removeWindow(){
        mWindowManager.removeView(view);
    }
}
