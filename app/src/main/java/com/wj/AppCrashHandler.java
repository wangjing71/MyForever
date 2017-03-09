package com.wj;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wj.activity.MainActivity;

import java.lang.Thread.UncaughtExceptionHandler;

public class AppCrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "AppCrashHandler";
	private Context context;
	
	private Thread.UncaughtExceptionHandler defaultCrashHandler;
	
	public AppCrashHandler(Context context){
		defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.context = context;
	}
	//1111111111111111
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.i(TAG, "App crashed by uncaughtException", ex);
		//发生未知错误 重启APP
		restartApp(context);
	}

	public void restartApp(Context context){
		Intent intent = new Intent(context,MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}

}
