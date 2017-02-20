package com.wj.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.myforever.R;
import com.google.gson.Gson;
import com.wj.AppConstant;
import com.wj.bean.JsonData;
import com.wj.libs.statusbar.StatusBarCompat;
import com.wj.myview.MyScrollView;
import com.wj.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.MySweetAlertDialog;

/**
 * Created by 汪京 on 2016/7/18.
 */
public class LoginActivity extends Activity {

    private View activityRootView;
    private int keyHeight = 0;
    private ImageView titleimage;
    private EditText username, password;
    private Button log_in, sign_up, back;
    private int movedistance;
    private String mUName;
    private String mPwd;
    private boolean mLightStatusBar;
    private RequestQueue mRequestQueue;
    private MyScrollView myScrollView;
    private CheckBox remembercount, automaticlogon;
    private LinearLayout rember;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.tabcolor, null));

        initViews();
        initData();
        initEvents();
    }

    private void initViews() {
        activityRootView = findViewById(R.id.rela);
        titleimage = (ImageView) findViewById(R.id.titleimage);
        keyHeight = getWindowManager().getDefaultDisplay().getHeight() / 3;
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        log_in = (Button) findViewById(R.id.log_in);
        sign_up = (Button) findViewById(R.id.sign_up);
        myScrollView = (MyScrollView) findViewById(R.id.scrollView);
        back = (Button) findViewById(R.id.back);
        remembercount = (CheckBox) findViewById(R.id.remembercount);
        automaticlogon = (CheckBox) findViewById(R.id.automaticlogon);
        rember = (LinearLayout) findViewById(R.id.rember);
        pref = getSharedPreferences("userinfo.txt", MODE_PRIVATE);
    }

    private void initData() {
        username.setText(pref.getString("username", ""));
        password.setText(pref.getString("password", ""));
        username.setSelection(username.getText().length());
        password.setSelection(password.getText().length());
        remembercount.setChecked(pref.getBoolean("remembercount_state", false));
        automaticlogon.setChecked(pref.getBoolean("automaticlogon_state", false));


    }

    private void initEvents() {
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    getResult();
                }
            }
        });


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
                finish();
            }
        });

        myScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //test
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CommonUtils.hideInputMethod(LoginActivity.this);
            }
        });

        remembercount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pref.edit().putBoolean("remembercount_state", remembercount.isChecked()).commit();
            }
        });

        automaticlogon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pref.edit().putBoolean("automaticlogon_state", automaticlogon.isChecked()).commit();
            }
        });
        password.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                Toast.makeText(LoginActivity.this, "actionId==>"+actionId, Toast.LENGTH_SHORT).show();

                if(actionId == EditorInfo.IME_ACTION_GO)  //passwordEt的布局中需添加android:imeOptions="actionGo"
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputMethodManager!=null && inputMethodManager.isActive())
                    {
                        //这里的判断并不可靠，不过为了效率，还是加上吧

                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    //出发点击事件，间接实现登录，提交功能
                    //doHttpUserLogin();
                    Toast.makeText(LoginActivity.this, "正在登录"+actionId, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void registDialog() {
        View reistView = View.inflate(this, R.layout.activity_regist, null);
        MySweetAlertDialog dialog = new MySweetAlertDialog(this);
        dialog.setTitle("用户注册");
        dialog.setView(reistView);
        dialog.show();
    }

    private void getResult() {

        String url = AppConstant.HostIp + "login";

        mRequestQueue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    System.out.println("response:" + response);
                    parseData(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this,
                        "网络连接错误，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", mUName);
                params.put("password", mPwd);
                return params;
            }
        };
        //将StringRequest对象添加进RequestQueue请求队列中
        mRequestQueue.add(request);
    }

    private void parseData(String response) {
        Gson gson = new Gson();
        JsonData jsonData = gson.fromJson(response, JsonData.class);
        System.out.println(jsonData.resultcode);
        System.out.println(jsonData.user);
        if ("0".equals(jsonData.resultcode)) {
            Toast.makeText(this, "恭喜您，登录成功！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userInfo", jsonData.user);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "用户名或密码错误，登录失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        mUName = username.getText().toString().trim();
        mPwd = password.getText().toString().trim();
        pref.edit().putString("username", mUName).commit();
        pref.edit().putString("password", remembercount.isChecked() ? mPwd : "").commit();
        if (TextUtils.isEmpty(mUName)) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mPwd)) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("translationY", -250);
                    ObjectAnimator.ofPropertyValuesHolder(username, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(password, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(log_in, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(sign_up, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(rember, pvhZ).setDuration(500).start();

                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0);
                    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0);
                    ObjectAnimator.ofPropertyValuesHolder(titleimage, pvhX, pvhY).setDuration(500).start();

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("translationY", 0);
                    ObjectAnimator.ofPropertyValuesHolder(username, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(password, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(log_in, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(sign_up, pvhZ).setDuration(500).start();
                    ObjectAnimator.ofPropertyValuesHolder(rember, pvhZ).setDuration(500).start();

                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 0, 1f);
                    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 0, 1f);
                    ObjectAnimator.ofPropertyValuesHolder(titleimage, pvhX, pvhY).setDuration(500).start();
                }
            }
        });
    }

    private void setStatusBarColor(@ColorInt int color) {
        StatusBarCompat.setStatusBarColor(this, color, mLightStatusBar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
