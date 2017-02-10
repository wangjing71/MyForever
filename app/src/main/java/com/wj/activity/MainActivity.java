package com.wj.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myforever.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.tencent.bugly.crashreport.CrashReport;
import com.wj.fragment.BBSFragment;
import com.wj.fragment.HomePageFragment;
import com.wj.fragment.MusicFragment;
import com.wj.fragment.PersionalFragment;
import com.wj.fragment.musfrag.Mp3Info;
import com.wj.fragment.musfrag.Mp3Utils;
import com.wj.myview.NoScrollViewPager;
import com.wj.utils.CommonUtils;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends FragmentActivity {


    private NoScrollViewPager viewPager;
    private ArrayList<Fragment> frag;
    private LinearLayout bottomtable;
    private TextView title;
    private String[] tabs;
    private int[] tabimagedefault;
    private int[] tabimageselect;
    private ImageView btn_menu;
    private View statusbar;
    private View leftstatusbar;
    public SlidingMenu menu;
    public BBSFragment bbsFragment;
    private HomePageFragment homeFragment;
    private PersionalFragment perFragment;
    private MusicFragment musicFragment;
    private RelativeLayout tabtop;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    public int mainSelectposition = -1;
    public static ArrayList<Mp3Info> mysongList;
    private RelativeLayout contextRela;
    private float action_down_X,action_move_X,action_up_X,distance_X;
    public  SharedPreferences pref;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mysongList == null || mysongList.size() == 0) {
                    mysongList = (ArrayList<Mp3Info>) Mp3Utils.getMp3Infos(MainActivity.this);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("====", "申请权限");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            Log.i("====", "权限存在");
            handler.sendEmptyMessage(0);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initViews();
        initData();
        initLeftMenu();
        initEvents();
        CrashReport.testJavaCrash();
        viewPager.setCurrentItem(1);
    }


    private void initLeftMenu() {
        View leftmenu = getLayoutInflater().from(MainActivity.this).inflate(R.layout.left_menu, null);
        leftstatusbar = leftmenu.findViewById(R.id.left_status_bar);
        contextRela = (RelativeLayout) leftmenu.findViewById(R.id.contextRela);
        menu = new SlidingMenu(MainActivity.this);
        menu.setMode(SlidingMenu.LEFT);// 设置菜单的位置
        menu.setTouchModeAbove(SlidingMenu.LEFT);// 设置菜单滑动的样式
        menu.setShadowDrawable(R.mipmap.ic_launcher);// 菜单滑动时阴影部分
        menu.setBehindWidth(CommonUtils.getScreenWidth(this) * 4 / 5);// 菜单宽带
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setMenu(leftmenu);// 添加菜单
    }

    private void initViews() {
        pref = getSharedPreferences("main.txt",MODE_PRIVATE);
        tabtop = (RelativeLayout) findViewById(R.id.toptab);
        statusbar = findViewById(R.id.status_bar);
        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        viewPager = (NoScrollViewPager) findViewById(R.id.content);
        bottomtable = (LinearLayout) findViewById(R.id.bottontable);
        title = (TextView) findViewById(R.id.title);
        frag = new ArrayList<Fragment>();
        tabs = new String[]{"首页", "社区", "音乐", "个人"};
        tabimageselect = new int[]{R.mipmap.home_press, R.mipmap.bbs_press, R.mipmap.music_press, R.mipmap.per_press};
        tabimagedefault = new int[]{R.mipmap.home, R.mipmap.bbs, R.mipmap.music, R.mipmap.per};
    }

    private void initData() {
        bbsFragment = new BBSFragment();
        homeFragment = new HomePageFragment();
        perFragment = new PersionalFragment();
        musicFragment =  new MusicFragment();

        frag.add(homeFragment);
        frag.add(bbsFragment);
        frag.add(musicFragment);
        frag.add(perFragment);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));

        //设置主页缓存加载条目
//        viewPager.setOffscreenPageLimit(4);
        showOrHideBotable();  // 设置底部导航栏条目可见
    }

    private void showOrHideBotable() {
        bottomtable.getChildAt(0).setVisibility(View.VISIBLE);  //使首页隐藏
        bottomtable.getChildAt(1).setVisibility(View.VISIBLE);  //使社区隐藏
        bottomtable.getChildAt(2).setVisibility(View.VISIBLE);  //使音乐隐藏
        bottomtable.getChildAt(3).setVisibility(View.VISIBLE);  //使个人隐藏
    }

    private void initEvents() {
        setBotTableEvent();

        btn_menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "cccc", Toast.LENGTH_LONG).show();
                menu.toggle();
            }
        });

        statusbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayout.LayoutParams parms = (LinearLayout.LayoutParams) statusbar.getLayoutParams();
                parms.height = CommonUtils.getStatusBarHeight(MainActivity.this);
                statusbar.setLayoutParams(parms);
            }
        });

        leftstatusbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) leftstatusbar.getLayoutParams();
                parms.height = CommonUtils.getStatusBarHeight(MainActivity.this);
                leftstatusbar.setLayoutParams(parms);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainSelectposition = position;
                switch (position) {
                    case 0:
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        tabtop.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tabtop.setVisibility(View.GONE);
                        if (bbsFragment.defaultposition == 0) {
                            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        } else {
                            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                        }
                        break;
                    case 2:
                        tabtop.setVisibility(View.GONE);
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//                        if(pref.getBoolean("isfirselmsc",true)){
//                            musicFragment.showPopwindow();
//                        }
                        musicFragment.showPopwindow();
                        break;
                    case 3:
                        tabtop.setVisibility(View.VISIBLE);
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        contextRela.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        action_down_X = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        action_move_X = motionEvent.getX();
                        distance_X = action_move_X - action_down_X;
                        Log.i("=====distance",distance_X+"");
                        if(distance_X<-200){
                            menu.toggle();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;

                }
                return true;
            }
        });

    }

    private void setBotTableEvent() {
        for (int i = 0; i < bottomtable.getChildCount(); i++) {
            final RelativeLayout rela = (RelativeLayout) bottomtable.getChildAt(i);
            final int finalI = i;
            rela.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                    for (int j = 0; j < bottomtable.getChildCount(); j++) {
                        if (j == finalI) {
                            TextView tv = (TextView) rela.getChildAt(0);
                            ImageView iv = (ImageView) rela.getChildAt(1);
                            tv.setTextColor(Color.parseColor("#ff0000"));
                            iv.setImageResource(tabimageselect[j]);
                        } else {
                            RelativeLayout otherrela = (RelativeLayout) bottomtable.getChildAt(j);
                            TextView tv = (TextView) otherrela.getChildAt(0);
                            ImageView iv = (ImageView) otherrela.getChildAt(1);
                            tv.setTextColor(Color.parseColor("#ffffff"));
                            iv.setImageResource(tabimagedefault[j]);
                        }
                    }


                    viewPager.setCurrentItem(finalI, false);
                    title.setText(tabs[finalI]);
                    if (finalI == 0) {
                        btn_menu.setVisibility(View.VISIBLE);
                        homeFragment.handler.sendEmptyMessage(0);
                    } else {
                        btn_menu.setVisibility(View.GONE);
                        homeFragment.handler.removeMessages(0);
                    }
                }
            });
        }


    }


    class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int arg0) {
            return frag.get(arg0);
        }

        public int getCount() {
            return frag.size();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("====", "用户同意了");
                handler.sendEmptyMessage(0);
            } else {
                SweetAlertDialog sd = new SweetAlertDialog(MainActivity.this);
                sd.setTitleText("拒绝了权限申请!");
                sd.setConfirmText("确定");
                sd.show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否退出本程序？");
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton("取消", null);
            builder.show();
        }
        return false;

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("======", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("======", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("======", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("======", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("======", "onDestroy");
    }
}
