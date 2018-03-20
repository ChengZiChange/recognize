package com.example.change.recogniaze.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.change.recogniaze.R;
import com.example.change.recogniaze.tool.AdminReceiver;
import com.example.change.recogniaze.tool.UnlockService;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    private LinearLayout linearlay;
    private Button button;
    private Button b1;
    private String value=null;
    private int delete;

    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();
        componentName = new ComponentName(this, AdminReceiver.class);
        activeManage();

        //点击声纹管理按钮输入密码，调到下一个页面
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "请务必输入正确的锁屏密码", Toast.LENGTH_SHORT).show();
                final EditText inputpassword = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请输入锁屏密码").setIcon(android.R.drawable.ic_dialog_info).setView(inputpassword)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String password=inputpassword.getText().toString();
                        Log.i("password in callback",password);
                        SharedPreferences sp=getSharedPreferences("password", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("pwd",password);
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });
        //动态布局
        linearlay = (LinearLayout) findViewById(R.id.linearlay);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        //生成命令词按钮
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("name");
            Log.i("activity1",name);
            if (name.equals("activity2")) {
                value = bundle.getString("value");
                Log.i("activity1",value);
                Log.i("activity1", Integer.toString(value.length()));
                if (!(TextUtils.isEmpty(value) || value.equals("null"))) {
                    Button b1 = new Button(MainActivity.this);
                    b1.setText(value);
                    linearlay.addView(b1, p);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                            startActivity(intent);
                        }
                    });
                    Intent regIntent = new Intent(this, UnlockService.class);
                    regIntent.putExtra("value", value);
                    startService(regIntent);
                }
            }
            else if (name.equals("activity3")) {
                delete = bundle.getInt("delete");
                Log.i("bundle3", Integer.toString(delete));
                if (delete == 1) {
                    linearlay.removeView(b1);
                    Intent intent = new Intent(this, UnlockService.class);
                    stopService(intent);
                }
            }

        }
    }

    private void activeManage() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ 其他描述 ------");
        startActivityForResult(intent, 0);
    }
    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

}