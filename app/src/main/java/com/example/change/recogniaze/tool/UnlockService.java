package com.example.change.recogniaze.tool;


import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.change.recogniaze.tool.ScreenObserver.ScreenStateListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UnlockService extends Service {
    private static final String TAG="UnlockService";
    private String TAG1 = "ScreenObserverActivity";
    private ScreenObserver mScreenObserver;
    private EventManager asr;
    private EventListener myListener;
    private String s=null;
    private String json=null;

    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private String TAG2 ="setPassword";

    private String logTxt = "";
    private String meaningResult = "";
    private String value;
    private String pwd;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mScreenObserver = new ScreenObserver(this);
        //获取设备管理服务
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        //AdminReceiver 继承自 DeviceAdminReceiver
        componentName = new ComponentName(this, AdminReceiver.class);

        asr = EventManagerFactory.create(this, "asr");
        myListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                    if (params != null && !params.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(params);
                            int error = jsonObject.getInt("error");
                            if(error == 0){
                                JSONArray array = jsonObject.getJSONArray("results_recognition");
                                if(array.length() > 0){
                                    String result = (String)array.get(0);
                                    logTxt = result;
                                    printLog(logTxt);
                                }
                            }else if(error == 7){
                                if(jsonObject.getInt("sub_error") == 7001){
                                    logTxt = "结果没有匹配成功";
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        asr.registerListener(myListener);
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags,startId);
        value=intent.getStringExtra("value");
        Log.i("命令词",value);
        //取出密码
        SharedPreferences sp1=getSharedPreferences("password", Context.MODE_PRIVATE);
        pwd=sp1.getString("pwd","null");
        Log.i(TAG,pwd);

        mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
            @Override
            public void onScreenOn() {
                doSomethingOnScreenOn();
            }
            @Override
            public void onScreenOff() {
                doSomethingOnScreenOff();
            }
        });

        Log.i(TAG, "onStart");
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mScreenObserver.stopScreenStateUpdate();

        asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0);
//        reco.cancel();

        Log.i(TAG, "onDestroy");
    }
    private void doSomethingOnScreenOn() {
        s=null;
        json = "{\"accept-audio-volume\":false,\"pid\":1536}";
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
//        reco.start();

        Log.i(TAG1, "Screen is on");
    }
    private void doSomethingOnScreenOff() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
//        reco.stop();
        setPassword(pwd);
        Log.i(TAG1, "Screen is off");
    }

    private void printLog(String text) {
        s = text;
        Log.i("识别结果", s);
        if (s.indexOf(value) != -1) {
            Log.i("判断", "正确");
            removePassword();
        }else{
            Log.i("判断", "错误");
        }
    }

    public void setPassword(String password) {
        if (policyManager.isAdminActive(componentName)) {
            policyManager.resetPassword(password, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            Log.i(TAG2,"密码重置");
        }else {
            Log.i(TAG2,"请先激活设备");
        }
    }

    public void removePassword() {
        if (policyManager.isAdminActive(componentName)) {
            policyManager.resetPassword(null, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            Log.i(TAG2,"密码被清除");
        }else {
            Log.i(TAG2,"请先激活设备");
        }
    }

}

