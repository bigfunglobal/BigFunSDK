package com.bigfun.tm;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.text.TextUtils;

import com.bigfun.tm.encrypt.DesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bigfun.tm.Constant.PAY_TAG;

@Keep
public class BigFunSDK {

    public static Context mContext;
    public static String mChannel;
    private static BigFunSDK instance;
    /**
     * 是否是Debug模式
     */
    static boolean isDebug = false;
    private static String mSource;

    private BigFunSDK() {

    }

    @Keep
    public void init(Context context, String channel) {
        mContext = context;
        mChannel = channel;
        clipboard();
        LogUtils.log("sdk init success");
    }

    /**
     * 设置是否是Debug模式d
     *
     * @param debug
     */
    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    @Keep
    public static BigFunSDK getInstance() {
        if (instance == null) {
            synchronized (BigFunSDK.class) {
                if (instance == null) {
                    instance = new BigFunSDK();
                }
            }
        }
        return instance;
    }

    /**
     * 登录
     *
     * @param params
     */
    @Keep
    private void login(Map<String, Object> params, ResponseListener listener) {
        new Thread(() -> {
            if (!params.containsKey("loginType") || !params.containsKey("gameUserId") || params.get("gameUserId") == null) {
                throw new IllegalArgumentException(PAY_TAG + "loginType,gameUserId is required");
            }
            Map<String, Object> map = new HashMap<>(params);
            map.put("deviceType", "Android");
            map.put("deviceModel", Build.MODEL);
            map.put("deviceBrand", Build.BRAND);
            try {
                map.put("aaid", AdvertisingIdClient.getAdId(mContext));
            } catch (Exception e) {
                e.printStackTrace();
                map.put("aaid", "111111111111111111111111111");
            }
            map.put("androidId", Settings.System.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
            map.put("ip", IpUtils.getOutNetIP(mContext, 0));
            map.put("channelCode", mChannel);
            map.put("verCode", Utils.getVersionName(mContext));
            map.put("source", mSource);
            map.put("packageName", mContext.getPackageName());
            if (!map.containsKey("email")) {
                map.put("email", "");
            }
            if (!map.containsKey("sex")) {
                map.put("sex", 0);
            }
            if (!map.containsKey("age")) {
                map.put("age", 0);
            }
            if (!map.containsKey("nickName")) {
                map.put("nickName", "unknow");
            }
            if (!map.containsKey("headImg")) {
                map.put("headImg", "unknow");
            }
            if (!map.containsKey("mobile")) {
                map.put("mobile", "0");
            }
            if (!map.containsKey("authCode")) {
                map.put("authCode", "");
            }
            HttpUtils.getInstance().login(NetConstant.LOGIN, map, listener);
        }).start();
    }

    private void initLogin() {
        if (checkSdkNotInit()) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("loginType", 1);
        map.put("gameUserId", 0);
        login(map, new ResponseListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String msg) {

            }
        });
    }


    /**
     * 检查是否初始化
     */
    private boolean checkSdkNotInit() {
        if (TextUtils.isEmpty(mChannel) || mContext == null) {
            LogUtils.log("sdk not init");
            return true;
        }
        return false;
    }

    /**
     * 获取Paytm支付结果
     */
    @Keep
    public boolean getPayResult(int requestCode, Intent data) {
        if (requestCode == 100 && data != null) {
            String response = data.getStringExtra("response");
            if (TextUtils.isEmpty(response)) {
                LogUtils.log(data.getStringExtra("nativeSdkForMerchantMessage"));
                HttpUtils.getInstance().report(HttpUtils.PAY_FAIL, data.getStringExtra("nativeSdkForMerchantMessage"));
                return false;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.optString("RESPCODE");
                    if (TextUtils.isEmpty(responseCode)) {
                        HttpUtils.getInstance().report(HttpUtils.PAY_FAIL, "pay unknow error");
                        LogUtils.log("pay unknow error");
                    } else {
                        if ("01".equals(responseCode)) {
                            LogUtils.log("01--" + jsonObject.optString("STATUS"));
                            return true;
                        } else {
                            HttpUtils.getInstance().report(HttpUtils.PAY_FAIL, jsonObject.optString("STATUS"));
                        }
                        LogUtils.log(responseCode + jsonObject.optString("STATUS"));
                    }
                    return false;
                } catch (JSONException e) {
                    HttpUtils.getInstance().report(HttpUtils.PAY_FAIL, e.getMessage());
                    e.printStackTrace();
                    LogUtils.log(e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * 从剪切板获取渠道，如果存在则使用新的渠道，没有则使用传递的渠道
     */
    private void clipboard() {
        String localChannel = (String) SPUtils.getInstance().get(mContext, "channel", "");
        if (TextUtils.isEmpty(localChannel)) {
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm.hasPrimaryClip()) {
                if (cm.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData clipData = cm.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    if (item.getText() != null && !TextUtils.isEmpty(item.getText().toString())) {
                        String result = DesUtils.decode(DesUtils.getPwd(), item.getText().toString());
                        if (result.startsWith("|")) {
                            String[] resultArr = result.split("|");
                            String channel = resultArr[0];
                            String source = resultArr[1];
                            if (!TextUtils.isEmpty(channel)) {
                                mChannel = channel;
                            }
                            if (!TextUtils.isEmpty(source)) {
                                mSource = source;
                            }
                            SPUtils.getInstance().put(mContext, "channel", mChannel);
                        }
                    }
                }
            }
        } else {
            mChannel = localChannel;
            SPUtils.getInstance().put(mContext, "channel", mChannel);
        }
        initLogin();
    }

    /**
     * 充值下单
     */
    @Keep
    public void payOrder(
            Map<String, Object> params,
            Activity activity,
            ResponseListener listener
    ) {
        if (checkSdkNotInit()) {
            return;
        }
        if (!params.containsKey("orderId")) {
            throw new IllegalArgumentException(PAY_TAG + "缺少参数");
        }
        Map<String, Object> map = new HashMap<>(params);
        HttpUtils.getInstance().payOrder(NetConstant.PAY_URL, map, activity, listener);
    }
}
