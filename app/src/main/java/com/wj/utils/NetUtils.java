package com.wj.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 汪京 on 2016/9/1.
 */
public class NetUtils {

    public interface StringCallBack {
        public void getStringContent(String str);
    }

    public static void getStringFromUrl(final String url, final StringCallBack callback) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str = msg.obj.toString();
                callback.getStringContent(str);
            }
        };

        new Thread(new Runnable() {
            private String str = "";
            public void run() {
                try {
                    URL down = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) down
                            .openConnection();
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        int len = 0;
                        byte buffer[] = new byte[1024];
                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        str = new String(os.toByteArray());
                        is.close();
                        os.close();

                    } else {
                        Log.i("===", "连接失败");
                        str = "-1";
                    }
                    Message message = Message.obtain();
                    message.obj = str;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



}
