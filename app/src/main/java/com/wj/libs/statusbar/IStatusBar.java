package com.wj.libs.statusbar;

import android.view.Window;

/**
 * 状态栏接口
 * Created by 汪京 on 2016/8/8.
 */

interface IStatusBar {
    void setStatusBarColor(Window window, int color, boolean lightStatusBar);
}
