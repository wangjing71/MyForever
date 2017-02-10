package com.wj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.myforever.R;
import com.google.gson.Gson;
import com.wj.AppConstant;
import com.wj.bean.JsonData;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by DaiYiqian on 2016/7/29.
 */
public class RegistActivity extends Activity {


    private TextView mTopTitile;
    private EditText mUsername;
    private EditText mPassword;
    private Button mBtnRegist;
    private Button mBtnCancle;
    private String mRegistName;
    private String mRegistPassword;
    private MyCountDownTimer mCountDownTimer;
    private SweetAlertDialog mDialog;
    private final int closeTime = 3 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
        mCountDownTimer = new MyCountDownTimer(closeTime, 1000);
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
    }

    private void initView() {
        mTopTitile = (TextView) findViewById(R.id.top_title);
        mTopTitile.setText("用户注册");
        mUsername = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_password);
        mBtnRegist = (Button) findViewById(R.id.btn_regist);
        mBtnCancle = (Button) findViewById(R.id.btn_cancle);

        mBtnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validata()) {
                    regist();
                }
            }
        });

        mBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancleDialog();
            }
        });
    }

    private void regist() {
        String url = AppConstant.HostIp + "sign";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest requst = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response:" + response);
                        if (!TextUtils.isEmpty(response)) {
                            parseResult(response);
                        }
                    }

                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistActivity.this,
                        "网络连接错误，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", mRegistName);
                params.put("password", mRegistPassword);

                return params;
            }
        };
        requestQueue.add(requst);
    }

    private void parseResult(String response) {
        Gson gson = new Gson();
        JsonData jsonData = gson.fromJson(response, JsonData.class);
        if ("success".equals(jsonData.resultcode)) {
            mCountDownTimer.start();
        }
        if ("fail".equals(jsonData.resultcode)) {
            Toast.makeText(RegistActivity.this,
                    "注册失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validata() {
        mRegistName = mUsername.getText().toString().trim();
        mRegistPassword = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mRegistName)) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mRegistPassword)) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void cancleDialog() {
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText("您确定取消注册么?");
        dialog.showCancelButton(true);
        dialog.setCancelText("取消");
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dialog.dismiss();
            }
        });
        dialog.setConfirmText("确定");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                dialog.dismiss();
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }

    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            Log.e("Regist:", l + "");
            mDialog.setTitleText("恭喜您，注册成功！");
            mDialog.setContentText((l / 1000 + "秒后自动关闭！"));
            mDialog.show();
            mDialog.showConfirmButton(false);
        }

        @Override
        public void onFinish() {
            mDialog.dismiss();
            Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
