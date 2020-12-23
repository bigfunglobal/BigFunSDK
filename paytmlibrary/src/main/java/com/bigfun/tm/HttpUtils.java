package com.bigfun.tm;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.bigfun.tm.database.EventBean;
import com.bigfun.tm.database.EventManager;
import com.bigfun.tm.encrypt.EncryptUtil;
import com.bigfun.tm.model.LoginBean;
import com.bigfun.tm.model.PaymentOrderBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    private HttpUtils() {
        okHttpClient = getUnsafeOkHttpClient();
    }

    private static class InstanceHolder {
        private static HttpUtils instance = new HttpUtils();
    }

    public static HttpUtils getInstance() {
        return InstanceHolder.instance;
    }

    public MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private static final long TIME_OUT = 30L;
    public Gson gson = new Gson();
    public OkHttpClient okHttpClient;
    //    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
//            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
//            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
//            .build();
    private ExecutorService mExecutors = Executors.newFixedThreadPool(2);
    public static final int REQUEST_CODE = 100;
    public static final String ORDER_FAIL = "ORDER_FAIL";
    public static final String ORDER_EXCEPTION = "ORDER_EXCEPTION";
    public static final String PAY_FAIL = "PAY_FAIL";
    public static Handler mHandler = new Handler(Looper.getMainLooper());

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    private static String mOrderId = "";

    /**
     * 预充值下单
     *
     * @param url
     * @param params
     * @param activity
     * @param listener
     */
    public void payOrder(String url, Map<String, Object> params, Activity activity, ResponseListener listener) {
        String orderId = (String) params.get("orderId");
        if (!TextUtils.isEmpty(mOrderId) && mOrderId.equals(orderId)) {
            return;
        }
        if (NetworkUtils.getNetworkState(BigFunSDK.mContext) == 0) {
            return;
        }
        if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("url.length() == 0");
        if (params.isEmpty()) throw new IllegalArgumentException("params.size == 0");
        mExecutors.execute(new Task(listener, params, activity, url));
        mOrderId = orderId;
    }

    public class Task implements Runnable {

        /**
         * 最大请求次数
         */
        private static final int MAX_TIMES = 3;
        private int mRequestTimes = 0;
        private ResponseListener listener;
        private Map<String, Object> params;
        private Activity activity;

        public Task(ResponseListener listener, Map<String, Object> params, Activity activity, String url) {
            this.listener = listener;
            this.params = params;
            this.activity = activity;
            this.url = url;
        }

        private String url;

        @Override
        public void run() {
            String json = null;
            mRequestTimes = 0;
            while (mRequestTimes < MAX_TIMES) {
                mRequestTimes++;
                try {
                    json = EncryptUtil.encryptData(gson.toJson(params));
                    Request request = new Request.Builder()
                            .url(url)
                            .post(RequestBody.create(mediaType, json))
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                String responseStr = response.body().string();
                                if (!TextUtils.isEmpty(responseStr)) {
                                    PaymentOrderBean bean =
                                            gson.fromJson(
                                                    responseStr,
                                                    PaymentOrderBean.class
                                            );
                                    if (Integer.parseInt(bean.getCode()) == 0) {
                                        if (bean.getData() != null) {
                                            listener.onSuccess();
                                            PayUtils.getInstance().pay(
                                                    bean.getData(),
                                                    activity,
                                                    REQUEST_CODE
                                            );
                                        } else {
                                            report(ORDER_FAIL, bean.getMsg());
                                            listener.onFail(bean.getMsg());
                                        }
                                    } else {
                                        report(ORDER_FAIL, bean.getMsg());
                                        listener.onFail(bean.getMsg());
                                    }
                                } else {
                                    report(ORDER_FAIL, response.message());
                                    listener.onFail(response.message());
                                }
                            } else {
                                report(ORDER_FAIL, response.message());
                                listener.onFail(response.message());
                            }
                        } else {
                            report(ORDER_FAIL, response.message());
                            listener.onFail(response.message());
                        }
                        break;
                    } else {
                        report(ORDER_FAIL, response.message());
                        listener.onFail(response.message());
                    }
                } catch (Exception e) {
                    LogUtils.log("order exception " + e.getMessage());
                    e.printStackTrace();
                    report(ORDER_EXCEPTION, e.getMessage());
                    SystemClock.sleep(100);
                }
            }
        }
    }

    public synchronized void report(String action, String content) {
        mHandler.post(() -> EventManager.getInstance().addEvent(action, content));
    }

    public synchronized void upload(List<EventBean> list) {
        mExecutors.execute(new ReportTask(list));
    }

    public synchronized void query() {
        mHandler.post(() -> EventManager.getInstance().query());
    }
}
