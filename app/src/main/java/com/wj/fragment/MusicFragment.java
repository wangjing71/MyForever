package com.wj.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.myforever.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wj.activity.MainActivity;
import com.wj.fragment.musfrag.MusicFragment_music;
import com.wj.fragment.musfrag.MusicFragment_player;

import java.util.ArrayList;

/**
 * Created by 汪京 on 2016/7/5.
 */
public class MusicFragment extends Fragment {

    private View view;
    private Activity activity;
    private ViewPager viewPager;
    private ArrayList<Fragment> musicfrag;
    private PopupWindow window;
    private View popview;
    private TextView music,player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music, null);
        popview = inflater.inflate(R.layout.pop_music_first, null);

        initViews();
        initData();
        initEvents();
        return view;
    }

    private void initViews() {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        music = (TextView) view.findViewById(R.id.music);
        player = (TextView) view.findViewById(R.id.player);
    }

    private void initData() {
        musicfrag = new ArrayList<Fragment>();
        musicfrag.add(new MusicFragment_music());
        musicfrag.add(new MusicFragment_player());
        viewPager.setAdapter(new MyMusicViewPagerAdapter(getChildFragmentManager()));
    }

    private void initEvents() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ((MainActivity)getActivity()).menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    music.setBackgroundResource(R.drawable.background_music_select);
                    music.setTextColor(getResources().getColor(R.color.tabcolor));
                    player.setBackgroundResource(R.drawable.background_player_normal);
                    player.setTextColor(getResources().getColor(R.color.white));

                }else{
                    ((MainActivity)getActivity()).menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    player.setBackgroundResource(R.drawable.background_player_select);
                    player.setTextColor(getResources().getColor(R.color.tabcolor));
                    music.setBackgroundResource(R.drawable.background_music_normal);
                    music.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        music.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)view).setBackgroundResource(R.drawable.background_music_select);
                        ((TextView)view).setTextColor(getResources().getColor(R.color.tabcolor));
                        break;

                    case MotionEvent.ACTION_UP:
                        viewPager.setCurrentItem(0);
                        player.setBackgroundResource(R.drawable.background_player_normal);
                        player.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
                return false;
            }
        });

        player.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)view).setBackgroundResource(R.drawable.background_player_select);
                        ((TextView)view).setTextColor(getResources().getColor(R.color.tabcolor));
                        break;

                    case MotionEvent.ACTION_UP:
                        viewPager.setCurrentItem(1);
                        music.setBackgroundResource(R.drawable.background_music_normal);
                        music.setTextColor(getResources().getColor(R.color.white));

                        break;
                }
                return false;
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
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
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        Button know = (Button) popview.findViewById(R.id.know);
        know.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                window.dismiss();
                ((MainActivity)activity).pref.edit().putBoolean("isfirselmsc",false).commit();
            }
        });

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
            }
        });
    }

    class MyMusicViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyMusicViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int arg0) {
            return musicfrag.get(arg0);
        }

        public int getCount() {
            return musicfrag.size();
        }

    }
}
