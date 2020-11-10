package com.bigfun.tm;

import android.app.Activity;
import android.text.TextUtils;

import com.bigfun.tm.encrypt.EncryptUtil;
import com.bigfun.tm.model.LoginBean;
import com.bigfun.tm.model.PaymentOrderBean;
import com.bigfun.tm.model.SendSmsBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    private HttpUtils() {
    }

    private static class InstanceHolder {
        private static HttpUtils instance = new HttpUtils();
    }

    public static HttpUtils getInstance() {
        return InstanceHolder.instance;
    }

    private MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private String token = (String) SPUtils.getInstance().get(BigFunSDK.mContext, "accessToken", "");
    private static final long TIME_OUT = 10L;
    public static String mCode = "";
    public static String mPhone = "";
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build();

    /**
     * post请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 请求回调
     */
    public <T> void post(String url, Map<String, Object> params, com.bigfun.tm.login.Callback<T> callback) {
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        String json = null;
        try {
            json = EncryptUtil.encryptData(gson.toJson(params));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(
                            "accessToken",
                            (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_TOKEN, "")
                    )
                    .post(RequestBody.create(mediaType, json))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    callback.onResult((T) response.body().string());
                                } else {
                                    callback.onFail(response.code() + "--" + response.message());
                                }
                            } else {
                                callback.onFail(response.code() + "--" + response.message());
                            }
                        } else {
                            callback.onFail(response.code() + "--" + response.message());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFail(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param url
     * @param params
     * @param listener
     */
    public void login(String url, Map<String, Object> params, ResponseListener listener) {
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        String json = null;
        try {
            json = EncryptUtil.encryptData(gson.toJson(params));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(
                            "accessToken",
                            (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_TOKEN, "")
                    )
                    .post(RequestBody.create(mediaType, json))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    LoginBean loginBean =
                                            gson.fromJson(response.body().string(), LoginBean.class);
                                    if (Integer.parseInt(loginBean.getCode()) == 0) {
                                        int loginType = (int) params.get("loginType");
                                        if (loginType == 2) {
                                            String mobile = (String) params.get("mobile");
                                            if (!TextUtils.isEmpty(mobile)) {
                                                SPUtils.getInstance().put(BigFunSDK.mContext, Constant.KEY_LOGIN_PHONE, mobile);
                                            }
                                        }
                                        SPUtils.getInstance().put(
                                                BigFunSDK.mContext,
                                                Constant.KEY_TOKEN,
                                                loginBean.getData().getAccessToken()
                                        );
                                        listener.onSuccess();
                                    } else {
                                        SPUtils.getInstance().remove(BigFunSDK.mContext, "channel");
                                        listener.onFail(loginBean.getMsg());
                                    }
                                } else {
                                    listener.onFail(response.message());
                                }
                            } else {
                                SPUtils.getInstance().remove(BigFunSDK.mContext, "channel");
                                listener.onFail(response.message());
                            }
                        } else {
                            listener.onFail(response.code() + "--" + response.message());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送验证码
     */
    public void sendSms(String url, Map<String, Object> params, ResponseListener listener) {
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        mPhone = params.get("mobile").toString();
        String json = null;
        try {
            json = EncryptUtil.encryptData(gson.toJson(params));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(
                            "accessToken",
                            (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_TOKEN, "")
                    )
                    .post(RequestBody.create(mediaType, json))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    SendSmsBean bean =
                                            gson.fromJson(
                                                    response.body().string(),
                                                    SendSmsBean.class
                                            );
                                    if (Integer.parseInt(bean.getCode()) == 0) {
                                        mCode = bean.getData();
                                        listener.onSuccess();
                                    } else {
                                        listener.onFail(bean.getMsg());
                                    }
                                } else {
                                    listener.onFail(response.message());
                                }
                            } else {
                                listener.onFail(response.message());
                            }
                        } else {
                            listener.onFail(response.code() + "--" + response.message());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onFail(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(e.getMessage());
        }
    }

    /**
     * get请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param listener 请求回调
     */
    public <T> void get(String url, Map<String, Object> params, ResponseListener listener) {
        try {
            if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
            StringBuffer requestUrl = new StringBuffer(url);
            boolean isFirst = true;
            for (String key : params.keySet()) {
                if (isFirst) {
                    isFirst = false;
                    requestUrl.append("?");
                } else {
                    requestUrl.append("&");
                }
                requestUrl.append(key).append(params.get(key));
            }
            Request request = new Request.Builder()
                    .url(requestUrl.toString())
                    .addHeader("accessToken", token)
                    .get()
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 充值下单
     */
    public void paymentOrder(String url, Map<String, Object> params, Activity activity, int requestCode, ResponseListener listener) {
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        String json = null;
        try {
            json = EncryptUtil.encryptData(gson.toJson(params));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(
                            "accessToken",
                            (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_TOKEN, "")
                    )
                    .post(RequestBody.create(mediaType, json))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.code() == 200) {
                                PaymentOrderBean bean =
                                        gson.fromJson(
                                                response.body().string(),
                                                PaymentOrderBean.class
                                        );
                                if (Integer.parseInt(bean.getCode()) == 0) {
                                    if (bean.getData() != null) {
                                        listener.onSuccess();
                                        PayUtils.getInstance().pay(
                                                bean.getData(),
                                                activity,
                                                requestCode
                                        );
                                    } else {
                                        listener.onFail(bean.getMsg());
                                    }
                                } else {
                                    listener.onFail(bean.getMsg());
                                }
                            } else {
                                listener.onFail(response.message());
                            }
                        } else {
                            listener.onFail(response.message());
                        }
                    } else {
                        listener.onFail(response.message());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(e.getMessage());
        }
    }

    /**
     * 获取渠道配置
     */
    public <T> void getChannelConfig(String url, Map<String, Object> params, com.bigfun.tm.login.Callback<T> callback) {
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        String json = null;
        try {
            json = EncryptUtil.encryptData(gson.toJson(params));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(
                            "accessToken",
                            (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_TOKEN, "")
                    )
                    .post(RequestBody.create(mediaType, json))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                callback.onResult((T) response.body().string());
                            } else {
                                callback.onFail(response.message());
                            }
                        } else {
                            callback.onFail(response.message());
                        }
                    } else {
                        callback.onFail(response.message());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFail(e.getMessage());
        }
    }

    /**
     * 预充值下单
     *
     * @param url
     * @param params
     * @param activity
     * @param requestCode
     * @param listener
     */
    public void payOrder(String url, Map<String, Object> params, Activity activity, int requestCode, ResponseListener listener) {
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        String json = null;
        try {
            json = EncryptUtil.encryptData(gson.toJson(params));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(
                            "accessToken",
                            (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_TOKEN, "")
                    )
                    .post(RequestBody.create(mediaType, json))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.code() == 200) {
                                PaymentOrderBean bean =
                                        gson.fromJson(
                                                response.body().string(),
                                                PaymentOrderBean.class
                                        );
                                if (Integer.parseInt(bean.getCode()) == 0) {
                                    if (bean.getData() != null) {
                                        listener.onSuccess();
                                        PayUtils.getInstance().pay(
                                                bean.getData(),
                                                activity,
                                                requestCode
                                        );
                                    } else {
                                        listener.onFail(bean.getMsg());
                                    }
                                } else {
                                    listener.onFail(bean.getMsg());
                                }
                            } else {
                                listener.onFail(response.message());
                            }
                        } else {
                            listener.onFail(response.message());
                        }
                    } else {
                        listener.onFail(response.message());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(e.getMessage());
        }
    }
}
