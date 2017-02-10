package com.wj.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.myforever.R;
import com.google.gson.Gson;
import com.wj.activity.ImageZoomActivity;
import com.wj.activity.MainActivity;
import com.wj.bean.HomePageResult;
import com.wj.fragment.homefrag.MyHomePageListViewAdapter;
import com.wj.fragment.homefrag.MyTabViewPagerAdapter;
import com.wj.myview.ViewPagerScroller;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 汪京 on 2016/7/5.
 */
public class HomePageFragment extends Fragment {

    private ViewPager viewPagerlunbo;
    private ListView listView;
    private View view,lunbo;
    private ArrayList<String> images;
    private ArrayList<View> tabview;
    private MyTabViewPagerAdapter adapter;
    private LayoutInflater inflater;
    private int defaultpositin;
    private int count = 0;
    private ViewPagerScroller scroller;
    private LinearLayout llPointGrouplunbo;
    private View viewSelectPointlunbo;
    private int mpointwidth;
    private int pointMarginLeft;
    private HomePageResult homePageResult;
    private RequestQueue queue;
    private MyHomePageListViewAdapter mylistadapter;
    private SharedPreferences pref;
    private RelativeLayout error,content;
    private TextView errorinfo;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                handler.removeMessages(0);
                viewPagerlunbo.setCurrentItem(count++);
                handler.sendEmptyMessageDelayed(0, 3500);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage, null);
        lunbo = inflater.inflate(R.layout.lunbo_home,null);
        this.inflater = inflater;
        initViews();
        initData();
        initEvents();
        return view;
    }

    private void initViews() {
        queue = Volley.newRequestQueue(getContext());
        viewPagerlunbo = (ViewPager) lunbo.findViewById(R.id.viewpager);
        error = (RelativeLayout) view.findViewById(R.id.error);
        content = (RelativeLayout) view.findViewById(R.id.content);
        errorinfo = (TextView) view.findViewById(R.id.errorinfo);
        listView = (ListView) view.findViewById(R.id.listView);
        scroller = new ViewPagerScroller(getContext());
        llPointGrouplunbo = (LinearLayout) lunbo.findViewById(R.id.ll_point_group);
        viewSelectPointlunbo = lunbo.findViewById(R.id.view_red_point);
        images = new ArrayList<String>();
        tabview = new ArrayList<View>();
        adapter = new MyTabViewPagerAdapter();
        mylistadapter = new MyHomePageListViewAdapter(getContext());
        homePageResult = new HomePageResult();
        defaultpositin = 0;
        pref = getContext().getSharedPreferences("netcache.txt", Context.MODE_PRIVATE);
    }

    private void initData() {
        images.add("http://i0.hdslb.com/bfs/archive/e7853d9ff1058e0be47a17b7118668c79f92c3fb.jpg");
        images.add("http://html.5173cdn.com/operation/indexbanner/2016/tw0930_730300_.jpg");
        images.add("http://html.5173cdn.com/operation/indexbanner/2016/cycj1009_730300.jpg");
        images.add("http://html.5173.com/yunyin/201610/730300.jpg");
        images.add("http://i0.hdslb.com/bfs/archive/c485c179a4b40a653a9310a9c1fd54662c4608a3.jpg");
        images.add("http://i0.hdslb.com/bfs/archive/517e199de263ac4b98b856a927a15a42b63f1a80.jpg");
        images.add("http://i0.hdslb.com/bfs/archive/442f3135b166e4f17c19e4d554184ba70502b45d.jpg");
        images.add("http://i0.hdslb.com/bfs/archive/c28353328a532be10b181b973cc3c6b1a49bcbd9.jpg");
        images.add("http://i0.hdslb.com/bfs/archive/ce56141335cc4961d8cbf996dcdbd8c5fd8a4cfa.jpg");
        images.add("http://i0.hdslb.com/bfs/archive/a5afe5508419326fbb95374fc81e3863d2e0614c.jpg");


        for (int i = 0; i < images.size(); i++) {
            View childtabView = inflater.inflate(R.layout.table_item, null);
            tabview.add(childtabView);
        }
        // 初始化引导页的小圆点
        for (int i = 0; i < images.size(); i++) {
            View point = new View(getContext());
            point.setBackgroundResource(R.drawable.shape_point_default);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(30, 30);
            if (i > 0) {
                parms.leftMargin = 15;
            }
            point.setLayoutParams(parms);
        }

        for (int i = 0; i < images.size(); i++) {
            View point = new View(getContext());
            point.setBackgroundResource(R.drawable.shape_point_default);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(30, 30);
            if (i > 0) {
                parms.leftMargin = 15;
            }
            point.setLayoutParams(parms);
            llPointGrouplunbo.addView(point);
        }



        llPointGrouplunbo.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        pointMarginLeft = llPointGrouplunbo.getLeft();
                        mpointwidth = llPointGrouplunbo.getChildAt(1).getLeft()
                                - llPointGrouplunbo.getChildAt(0).getLeft();

                        //初始化红点
                        RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) viewSelectPointlunbo.getLayoutParams();
                        parms.width = 30;
                        parms.height = 30;
                        parms.leftMargin = pointMarginLeft;
                        viewSelectPointlunbo.setLayoutParams(parms);
                        llPointGrouplunbo.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });

        scroller.setScrollDuration(1500);
        scroller.initViewPagerScroll(viewPagerlunbo);
        adapter.setViewlist(tabview);
        adapter.setimageUrl(images);
        viewPagerlunbo.setAdapter(adapter);
        handler.sendEmptyMessage(0);

        StringRequest request = new StringRequest(
                StringRequest.Method.GET, "http://119.29.35.65:8080/MyForeverServer/home?sign=content",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String s) {
                        try {
                            content.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                            JSONObject object = new JSONObject(s);
                            Gson gson = new Gson();
                            homePageResult = gson.fromJson(s, HomePageResult.class);
                            mylistadapter.setHomePageResult(homePageResult);
                            listView.setAdapter(mylistadapter);
                            listView.addHeaderView(lunbo);

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "数据出错", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(
                   VolleyError volleyError) {
                //此处测试没有网络的情况下轮播图是否出现·
                listView.setAdapter(mylistadapter);
                listView.addHeaderView(lunbo);
                mylistadapter.notifyDataSetChanged();

                Toast.makeText(getContext(), "糟糕，服务器没有任何响应。", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                homePageResult = gson.fromJson(pref.getString("cache",""), HomePageResult.class);
                if(homePageResult != null){
                    mylistadapter.setHomePageResult(homePageResult);
                    //以下代码可以考虑放在初始化过程中
                    listView.setAdapter(mylistadapter);
                    listView.addHeaderView(lunbo);
                    mylistadapter.notifyDataSetChanged();
                }

//                else{
//                    content.setVisibility(View.GONE);
//                    error.setVisibility(View.VISIBLE);
//                }
            }
        });

        request.setTag("get");
        queue.add(request);

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initEvents() {
        viewPagerlunbo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int len = pointMarginLeft + (int) (mpointwidth * positionOffset) + (position % images.size()) * mpointwidth;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewSelectPointlunbo
                        .getLayoutParams();
                params.leftMargin = len;
                viewSelectPointlunbo.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                count = position;
                defaultpositin = position;
                if (defaultpositin == 0) {
                    ((MainActivity) getActivity()).menu.removeIgnoredView(viewPagerlunbo);
                } else {
                    ((MainActivity) getActivity()).menu.addIgnoredView(viewPagerlunbo);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        loadmore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ImageZoomActivity.class);
                intent.putExtra("imageurl",homePageResult.getHomepage().get(i-1));
                getContext().startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }


    private void loadmore() {
        StringRequest request = new StringRequest(
                StringRequest.Method.GET, "http://119.29.35.65:8080/MyForeverServer/home?sign=content",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            Log.i("=====", "返回的是json");
                            Gson gson = new Gson();
                            HomePageResult moreinfo = gson.fromJson(s, HomePageResult.class);
                            for (int i = 0; i < moreinfo.getHomepage().size(); i++) {
                                homePageResult.getHomepage().add(moreinfo.getHomepage().get(i));
                            }
                            String cache = gson.toJson(homePageResult);
                            pref.edit().putString("cache",cache).commit();

                            mylistadapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "数据出错", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(
                    VolleyError volleyError) {
                Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
            }
        });

        request.setTag("get");
        queue.add(request);
    }


    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(0);
        Log.i("======frag", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("======frag", "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("======frag", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("======frag", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("======frag", "onDestroy");
    }
}
