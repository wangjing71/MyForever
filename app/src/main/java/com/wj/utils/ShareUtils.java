package com.wj.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 汪京 on 2016/8/30.
 */
public class ShareUtils {

    //分享文字
    public void shareText(Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my Share text.");
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    //分享单张图片
    public void shareSingleImage(Context context) {
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    //分享多张图片
    public void shareMultipleImage(Context context) {

        ArrayList<Uri> uriList = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory() + File.separator;
        uriList.add(Uri.fromFile(new File(path+"australia_1.jpg")));
        uriList.add(Uri.fromFile(new File(path+"australia_2.jpg")));
        uriList.add(Uri.fromFile(new File(path+"australia_3.jpg")));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
