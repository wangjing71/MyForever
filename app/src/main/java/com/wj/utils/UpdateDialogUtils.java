package com.wj.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//版本更新对话框
public class UpdateDialogUtils {
	public Context context;
	public String updateurl;
	ProgressDialog dialog;
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				dialog.show();
			} else if (msg.what == 2) {
				dialog.setProgress(msg.arg1);
			} else if (msg.what == 3) {
				dialog.dismiss();
			}
		};
	};

	public UpdateDialogUtils(Context context, String updateurl) {
		this.context = context;
		this.updateurl = updateurl;
		dialog = new ProgressDialog(context);
		dialog.setMax(100);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setMessage("正在下载最新版本，请稍后....");
		dialog.setCancelable(false);
	}

	public void updatedialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("版本更新");
		builder.setMessage("检测到新的版本是否立即更新");
		builder.setPositiveButton("更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 弹出更新进度提示
						handler.sendEmptyMessage(1);

						new Thread(new Runnable() {
							public void run() {
								Log.i("diglog接受到的更新地址为", updateurl);
								String dirName = Environment
										.getExternalStorageDirectory()
										+ "/MyDownload/";
								File f = new File(dirName);
								if (!f.exists()) {
									f.mkdir();
								}
								String newFileName = dirName
										+ "yinkoumeiqi.apk";
								File file = new File(newFileName);
								if (file.exists()) {
									file.delete();
								}
								try {
									URL url = new URL(updateurl);
									HttpURLConnection con = (HttpURLConnection) url
											.openConnection();
									con.connect();
									int total = con.getContentLength();
									Log.i("======contentLength", total + "");
									InputStream is = con.getInputStream();
									byte[] bs = new byte[1024];
									int len;
									int currentNum = 0;
									OutputStream os = new FileOutputStream(
											newFileName);

									while ((len = is.read(bs)) != -1) {
										os.write(bs, 0, len);
										currentNum += len;
										int progress = currentNum * 100 / total;
										Log.i("====", "=====progress "
												+ progress);
										Message message = Message.obtain();
										message.what = 2;
										message.arg1 = progress;
										handler.sendMessage(message);
									}
									handler.sendEmptyMessage(3);
									Log.i("=========", "文件下载完毕");
									os.close();
									is.close();
									Intent intent = new Intent(
											Intent.ACTION_VIEW);
									intent.setDataAndType(
											Uri.fromFile(new File(newFileName)),
											"application/vnd.android.package-archive");
									context.startActivity(intent);

								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
					}

				});
		builder.setNeutralButton("忽略", null); // 忽略
		builder.show();

	}

}
