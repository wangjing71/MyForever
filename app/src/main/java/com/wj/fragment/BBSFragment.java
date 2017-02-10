package com.wj.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myforever.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wj.activity.MainActivity;
import com.wj.fragment.bbsfrag.BbsDetailFragment;
import com.wj.myview.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * Created by 汪京 on 2016/7/5.
 */
public class BBSFragment extends Fragment {

    private ViewPager mViewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private View view;
    private ArrayList<String> titles;
    private ArrayList<Fragment> bbsfrag;
    public int defaultposition;
    private RelativeLayout bbstoptab;
    public SwipeRefreshLayout swipeRefreshLayout;
    private TextView title;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){  //刷新社区数据
                swipeRefreshLayout.setRefreshing(false);
            }else if(msg.what == 1){

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bbs, null);
        initViews();
        initData();
        initEvents();
        return view;
    }

    private void initViews() {
        mViewPager = (ViewPager) view.findViewById(R.id.bbsviewpager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        title = (TextView) view.findViewById(R.id.title);
//        bbstoptab = (RelativeLayout) view.findViewById(R.id.bbstoptab);
        titles = new ArrayList<String>();
        bbsfrag = new ArrayList<Fragment>();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
    }

    private void initData() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setProgressViewEndTarget(true, 500);
        defaultposition = 0;
            titles.add("条目 " + 1);
            titles.add("条目条目 " + 2);
            titles.add("条目条目条目 " + 3);
            titles.add("条目条目条目条目 " + 4);
            titles.add("条目条目条目条目条目 " + 5);
            titles.add("条目条目条目条目条目条目 " + 6);
            titles.add("条目 " + 7);
            titles.add("条目条目 " + 8);
            titles.add("条目条目条目 " + 9);

        for(int i = 0;i<9;i++){
            bbsfrag.add(new BbsDetailFragment());
        }
        mViewPager.setAdapter(new MyBbsViewPagerAdapter(getChildFragmentManager()));
//        pagerSlidingTabStrip.setDividerPadding(200);
        pagerSlidingTabStrip.setTextColor(Color.parseColor("#FDBCCE"));
        pagerSlidingTabStrip.setIndicatorColor(Color.WHITE);
        pagerSlidingTabStrip.setUnderlineHeight(0);
        pagerSlidingTabStrip.setIndicatorHeight(14);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setTitleSelectColor(Color.WHITE);
        pagerSlidingTabStrip.setDividerColorResource(R.color.tabcolor);
        pagerSlidingTabStrip.setViewPager(mViewPager);

    }

    private void initEvents() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ((MainActivity)getActivity()).menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else{
                    ((MainActivity)getActivity()).menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
                defaultposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });

        swipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }


                return false;
            }
        });


    }

    class MyBbsViewPagerAdapter extends FragmentPagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public MyBbsViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return bbsfrag.get(position);
        }

        @Override
        public int getCount() {
            return bbsfrag.size();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(0);
        Log.i("==BBSFragment","onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("==BBSFragment","onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("==BBSFragment","onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("==BBSFragment","onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("==BBSFragment","onDestroy");
    }

}
