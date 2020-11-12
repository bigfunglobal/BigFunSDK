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
import android.widget.Toast;

import com.bigfun.tm.encrypt.DesUtils;
import com.bigfun.tm.login.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.bigfun.tm.Constant.PAY_TAG;

@Keep
public class BigFunSDK {

    public String mPhone = "";
    public static Context mContext;
    private static String mChannel;
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
    public void login(Map<String, Object> params, ResponseListener listener) {
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

    /**
     * 游客登录
     */
    @Keep
    public void guestLogin(Map<String, Object> params, ResponseListener listener) {
        if (checkSdkNotInit()) {
            return;
        }
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 1);
        login(map, listener);
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
     * 手机号登录
     */
    @Keep
    public void phoneLogin(Map<String, Object> params, ResponseListener listener) {
        if (checkSdkNotInit()) {
            return;
        }
        if (!params.containsKey("mobile")
        ) {
            throw new IllegalArgumentException(PAY_TAG + "mobile is required");
        }
        if (params.get("mobile").toString().length() != 12 && params.get("mobile").toString().length() != 10) {
            Toast.makeText(
                    mContext,
                    "Please fill in the correct phone number",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 2);
        login(map, listener);
    }

    /**
     * 发送短信
     */
    @Keep
    public void sendSms(Map<String, Object> params, ResponseListener listener) {
        if (checkSdkNotInit()) {
            return;
        }
        if (!params.containsKey("mobile")) {
            throw new IllegalArgumentException(PAY_TAG + "mobile is required");
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
        map.putAll(params);
        map.put("codeType", 2);
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().sendSms(NetConstant.SEND_SMS, map, listener);
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
        if (checkSdkNotInit()) {
            return;
        }
        if (!params.containsKey("outUserId") || !params.containsKey("outOrderNo") || !params.containsKey(
                "commodityId"
        )
        ) {
            throw new IllegalArgumentException(PAY_TAG + "缺少参数");
        }
        Map<String, Object> map = new HashMap<>();
        map.putAll(params);
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().paymentOrder(NetConstant.RECHARGE_ORDER, map, activity, 100, listener);
    }

    /**
     * 获取渠道配置
     */
    @Keep
    public <T> void getChannelConfig(Callback<T> callback) {
        if (checkSdkNotInit()) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ip", IpUtils.getOutNetIP(mContext, 0));
        map.put("gameUserId", "0");
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().getChannelConfig(NetConstant.GET_CHANNEL_CONFIG, map, callback);
    }

    /**
     * token登录
     */
    @Keep
    private static void loginByToken() {
        new Thread(() -> {
            if (TextUtils.isEmpty(mChannel) || mContext == null) {
                LogUtils.log("sdk not init");
            }
            String phone = (String) SPUtils.getInstance().get(mContext, Constant.KEY_LOGIN_PHONE, "");
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
            map.put("ip", IpUtils.getOutNetIP(mContext, 0));
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
            HttpUtils.getInstance().login(NetConstant.LOGIN, map, new ResponseListener() {
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
        if (checkSdkNotInit()) {
            return;
        }
        if (!params.containsKey("nickName") || TextUtils.isEmpty(params.get("nickName").toString())
                || !params.containsKey("headImg") || TextUtils.isEmpty(params.get("headImg").toString())) {
            throw new IllegalArgumentException("nickName and headImg is required");
        }
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 4);
        login(map, listener);
    }

    /**
     * facebook登录
     *
     * @param listener
     */
    @Keep
    public void fbLogin(Map<String, Object> params, ResponseListener listener) {
        if (checkSdkNotInit()) {
            return;
        }
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 3);
        login(map, listener);
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
            return data.getStringExtra("nativeSdkForMerchantMessage").isEmpty();
        }
        return false;
    }

    /**
     * 获取可用的支付通道
     */
    @Keep
    public <T> void getRechargeChannel(Callback<T> callback) {
        if (checkSdkNotInit()) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().post(NetConstant.GET_RECHARGE_CHANNEL, map, callback);
    }

    /**
     * 获取可用提现通道
     *
     * @param callback
     * @param <T>
     */
    @Keep
    public <T> void getWithdrawChannel(Callback<T> callback) {
        if (checkSdkNotInit()) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("channelCode", mChannel);
        HttpUtils.getInstance().post(NetConstant.GET_WITHDRAW_CHANNEL, map, callback);
    }

    /**
     * 手机号+验证码登录
     *
     * @param params
     * @param listener
     */
    @Keep
    public void loginWithCode(Map<String, Object> params, ResponseListener listener) {
        if (checkSdkNotInit()) {
            return;
        }
        if (!params.containsKey("mobile") || !params.containsKey("code") ||
                TextUtils.isEmpty(params.get("mobile").toString()) ||
                TextUtils.isEmpty(params.get("code").toString())) {
            listener.onFail("缺少参数");
            return;
        }
        if (TextUtils.isEmpty(HttpUtils.mCode)) {
            listener.onFail("请先获取验证码");
            return;
        }
        if (!HttpUtils.mCode.equals(params.get("code")) || !HttpUtils.mPhone.equals(params.get("mobile"))) {
            listener.onFail("验证码错误或者验证码与上一次获取的手机号不一致");
            return;
        }
        Map<String, Object> map = new HashMap<>(params);
        map.put("loginType", 2);
        login(map, listener);
    }

    /**
     * 是否已登录
     *
     * @return
     */
    @Keep
    public boolean isLogin() {
        if (checkSdkNotInit()) {
            return false;
        }
        return !TextUtils.isEmpty((String) SPUtils.getInstance().get(mContext, Constant.KEY_TOKEN, ""));
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
        HttpUtils.getInstance().payOrder(NetConstant.PAY_URL, map, activity, 100, listener);
    }
}
