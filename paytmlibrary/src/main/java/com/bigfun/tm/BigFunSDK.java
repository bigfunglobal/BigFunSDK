package com.bigfun.tm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.widget.Toast;

import com.bigfun.tm.encrypt.MD5Utils;
import com.bigfun.tm.login.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Keep
public class BigFunSDK {

    private TreeMap<String, Object> treeMap = new TreeMap<>(String::compareTo);
    private StringBuilder sb = new StringBuilder();
    public String mPhone = "";
    public static Context mContext;
    private static String mChannel;
    private static String mKey;
    private static BigFunSDK instance;

    private BigFunSDK() {

    }

    @Keep
    public static void init(Context context, String channel, String key) {
        mContext = context;
        mChannel = channel;
        mKey = key;
        loginByToken();
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
    public void login(Map<String, Object> params, ResponseListener listener) {
        new Thread(() -> {
            check();
            if (!params.containsKey("loginType")) {
                throw new IllegalArgumentException(ConstantKt.PAY_TAG + "缺少参数");
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
            map.put("ip", UtilsKt.getIp(mContext));
            map.put("channelCode", mChannel);
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
            if (!map.containsKey("gameUserId")) {
                map.put("gameUserId", 0);
            }
            if (!map.containsKey("authCode")) {
                map.put("authCode", "");
            }
            treeMap.clear();
            sb.delete(0, sb.length());
            treeMap.put("loginType", map.get("loginType"));
            treeMap.put("channelCode", mChannel);
            treeMap.put("gameUserId", map.get("gameUserId"));
            treeMap.put("mobile", map.get("mobile"));
            treeMap.put("androidId", map.get("androidId"));
            treeMap.put("authCode", map.get("authCode"));
            for (String key : treeMap.keySet()) {
                sb.append(key).append("=").append(treeMap.get(key)).append("&");
            }
            sb.append("key=").append(mKey);
            String sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase();
            map.put("sign", sign);
            HttpUtils.getInstance().login(NetConstantKt.LOGIN, map, listener);
        }).start();
    }

    /**
     * 游客登录
     */
    @Keep
    public void guestLogin(ResponseListener listener) {
        Map<String, Object> map = new HashMap<>();
        map.put("loginType", 1);
        login(map, listener);
    }

    /**
     * 手机号登录
     */
    @Keep
    public void phoneLogin(Map<String, Object> params, ResponseListener listener) {
        if (!params.containsKey("mobile")
        ) {
            throw new IllegalArgumentException(ConstantKt.PAY_TAG + "缺少参数");
        }
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 2);
        login(map, listener);
    }

    /**
     * 发送短信
     */
    @Keep
    private void sendSms(Map<String, Object> params, ResponseListener listener) {
        check();
        if (!params.containsKey("mobile")) {
            throw new IllegalArgumentException(ConstantKt.PAY_TAG + "缺少参数");
        }
        mPhone = params.get("mobile").toString();
        if (mPhone.length() != 12 && mPhone.length() != 10) {
            Toast.makeText(
                    mContext,
                    "Please fill in the correct phone number",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        String phone;
        if (mPhone.startsWith("91")) {
            phone = mPhone;
        } else {
            phone = "91" + mPhone;
        }
        Map<String, Object> map = new HashMap<>();
        treeMap.clear();
        sb.delete(0, sb.length());
        treeMap.put("mobile", phone);
        treeMap.put("channelCode", mChannel);
        for (String key : treeMap.keySet()) {
            sb.append(key).append("=").append(treeMap.get("key")).append("&");
        }
        sb.append("key=").append(mKey);
        String sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase();
        map.putAll(params);
        map.put("codeType", 2);
        map.put("channelCode", mChannel);
        map.put("sign", sign);
        HttpUtils.getInstance().sendSms(NetConstantKt.SEND_SMS, map, listener);
    }

    /**
     * 充值下单
     */
    @Keep
    public void rechargeOrder(
            Map<String, Object> params,
            Activity activity,
            ResponseListener listener
    ) {
        check();
        if (!params.containsKey("outUserId") || !params.containsKey("outOrderNo") || !params.containsKey(
                "commodityId"
        )
        ) {
            throw new IllegalArgumentException(ConstantKt.PAY_TAG + "缺少参数");
        }
        Map<String, Object> map = new HashMap<>();
        treeMap.clear();
        sb.delete(0, sb.length());
        treeMap.put("channelCode", mChannel);
        treeMap.put("outUserId", params.get("outUserId"));
        treeMap.put("outOrderNo", params.get("outOrderNo"));
        treeMap.put("commodityId", params.get("commodityId"));
        for (String key : treeMap.keySet()) {
            sb.append(key).append("=").append(treeMap.get(key)).append("&");
        }
        sb.append("key=").append(mKey);
        String sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase();
        map.putAll(params);
        map.put("sign", sign);
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().paymentOrder(NetConstantKt.RECHARGE_ORDER, map, activity, 100, listener);
    }

    /**
     * 获取渠道配置
     */
    @Keep
    public <T> void getChannelConfig(Callback<T> callback) {
        check();
        treeMap.clear();
        treeMap.put("channelCode", mChannel);
        sb.delete(0, sb.length());
        for (String key : treeMap.keySet()) {
            sb.append(key).append("=").append(treeMap.get(key)).append("&");
        }
        sb.append("key=").append(mKey);
        String sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase();
        Map<String, Object> map = new HashMap<>();
        map.put("sign", sign);
        map.put("ip", UtilsKt.getIp(mContext));
        map.put("gameUserId", "0");
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().getChannelConfig(NetConstantKt.GET_CHANNEL_CONFIG, map, callback);
    }

    /**
     * token登录
     */
    @Keep
    private static void loginByToken() {
        new Thread(() -> {
            if (TextUtils.isEmpty(mChannel) || mContext == null) {
                throw new IllegalArgumentException(ConstantKt.PAY_TAG + "not init,please init sdk");
            }
            String phone = (String) SPUtils.getInstance().get(mContext, ConstantKt.KEY_LOGIN_PHONE, "");
            if (TextUtils.isEmpty(phone)) {
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("mobile", phone);
            params.put("loginType", 2);
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
            map.put("ip", UtilsKt.getIp(mContext));
            map.put("channelCode", mChannel);
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
            if (!map.containsKey("gameUserId")) {
                map.put("gameUserId", 0);
            }
            if (!map.containsKey("authCode")) {
                map.put("authCode", "");
            }
            TreeMap<String, Object> treeMap = new TreeMap<>(String::compareTo);
            StringBuilder sb = new StringBuilder();
            treeMap.put("loginType", map.get("loginType"));
            treeMap.put("channelCode", mChannel);
            treeMap.put("gameUserId", map.get("gameUserId"));
            treeMap.put("mobile", map.get("mobile"));
            treeMap.put("androidId", map.get("androidId"));
            treeMap.put("authCode", map.get("authCode"));
            for (String key : treeMap.keySet()) {
                sb.append(key).append("=").append(treeMap.get(key)).append("&");
            }
            sb.append("key=").append(mKey);
            String sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase();
            map.put("sign", sign);
            HttpUtils.getInstance().login(NetConstantKt.LOGIN, map, new ResponseListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(String msg) {

                }
            });
        }).start();
    }

    /**
     * google登录
     *
     * @param params
     * @param listener
     */
    @Keep
    public void googleLogin(Map<String, Object> params, ResponseListener listener) {
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 4);
        login(map, listener);
    }

    /**
     * 检查是否初始化
     */
    private void check() {
        if (TextUtils.isEmpty(mChannel) || mContext == null) {
            throw new IllegalArgumentException(ConstantKt.PAY_TAG + "not init,please init sdk");
        }
    }

    /**
     * 获取Paytm支付结果
     */
    @Keep
    public boolean getPayResult(int requestCode, Intent data) {
        if (requestCode == 100 && data != null) {
            return data.getStringExtra("nativeSdkForMerchantMessage").isEmpty();
        }
        return false;
    }
}
