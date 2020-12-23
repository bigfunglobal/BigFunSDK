package com.bigfun.tm;

import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.bigfun.tm.database.EventBean;
import com.bigfun.tm.database.EventManager;
import com.bigfun.tm.encrypt.EncryptUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ReportTask implements Runnable {

    private List<EventBean> mEventBeanList;

    public ReportTask(List<EventBean> list) {
        mEventBeanList = list;
    }

    @Override
    public void run() {
        try {
            if (mEventBeanList != null) {
                JSONArray jsonArray = new JSONArray();
                for (EventBean bean : mEventBeanList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("channelCode", BigFunSDK.mChannel);
                    jsonObject.put("gameUserId", "0");
                    jsonObject.put("deviceNo", Build.SERIAL);
                    jsonObject.put("deviceModel", Build.MODEL);
                    try {
                        jsonObject.put("aaid", AdvertisingIdClient.getAdId(BigFunSDK.mContext));
                        jsonObject.put("gaid", AdvertisingIdClient.getAdId(BigFunSDK.mContext));
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonObject.put("aaid", "11111111111");
                        jsonObject.put("gaid", "11111111111");
                    }
                    jsonObject.put("ip", IpUtils.getOutNetIP(BigFunSDK.mContext, 0));
                    jsonObject.put("deviceType", 20);
                    jsonObject.put("action", bean.getActionType());
                    jsonObject.put("actionContent", bean.getActionContent());
                    jsonObject.put("androidId", Settings.System.getString(BigFunSDK.mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
                    jsonArray.put(jsonObject);
                }
                String json = EncryptUtil.encryptData(jsonArray.toString());
                Request request = new Request.Builder()
                        .url(NetConstant.REPORT_URL)
                        .post(RequestBody.create(HttpUtils.getInstance().mediaType, json))
                        .build();
                Response response = HttpUtils.getInstance().okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String string = body.string();
                        if (!TextUtils.isEmpty(string)) {
                            JSONObject jsonObject = new JSONObject(string);
                            String code = jsonObject.optString("code");
                            if ("0".equals(code)) {
                                EventManager.getInstance().delete();
                                //上报成功,将数据删除
                                LogUtils.log("report success");
                            }
                        }
                    }
                } else {
                    LogUtils.log(response.message());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.log(e.getMessage());
        }
    }
}
