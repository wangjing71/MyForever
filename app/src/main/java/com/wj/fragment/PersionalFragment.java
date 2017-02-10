package com.wj.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.administrator.myforever.R;
import com.wj.activity.LoginActivity;


/**
 * Created by 汪京 on 2016/7/5.
 */
public class PersionalFragment extends Fragment {

    private View view;
    private View popview;
    private Button text, popwindow;
    private int count = 0;
    private PopupWindow window;
    private String haha;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            count = count + 2;
            if (count >= 100) {
                count = 1;
            }
            handler.sendEmptyMessageDelayed(0, 1000);
            Log.i("=====", count + "");
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_persional, null);
        popview = inflater.inflate(R.layout.pop_menu, null);

        text = (Button) view.findViewById(R.id.text);
        popwindow = (Button) view.findViewById(R.id.popwindow);


        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        popwindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopwindow();
            }
        });

        //测试 APPcrashHandler

        try {
            if(haha.equals("s"));
        }catch (Exception e){

        }

        return view;
    }

    public void showPopwindow() {

        window = new PopupWindow(popview, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(view,Gravity.BOTTOM, 0, 0);

        // 这里检验popWindow里的button是否可以点击
        Button first = (Button) popview.findViewById(R.id.seven);
        first.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                System.out.println("第一个按钮被点击了");
                window.dismiss();
            }
        });

        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
            }
        });
    }

}
