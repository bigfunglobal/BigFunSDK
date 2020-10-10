package com.bigfun.tm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bigfun.tm.model.PaymentOrderBean;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.util.HashMap;
import java.util.Map;

public class PayUtils {
    private static final String TAG = "PayUtils2";

    private PayUtils() {
    }

    private static class InstanceHolder {
        private static PayUtils instance = new PayUtils();
    }

    public static PayUtils getInstance() {
        return InstanceHolder.instance;
    }

    public void pay(PaymentOrderBean.DataBean bean,
                    Activity activity,
                    int requestCode,
                    String email,
                    String phone) {
        if (Integer.parseInt(bean.getPaymentChannel()) == 1) {
            paytm(bean, activity, requestCode);
        } else if (Integer.parseInt(bean.getPaymentChannel()) == 0) {
            if (Integer.parseInt(bean.getOpenType()) == 1) {
                activity.runOnUiThread(() -> {
                    Intent intent = new Intent(activity, PayActivity.class);
                    intent.putExtra(Constant.EXTRA_KEY_PAY_URL, bean.getJumpUrl());
                    activity.startActivity(intent);
                });
            } else {
                activity.runOnUiThread(() -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getJumpUrl()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                });
            }
        } else {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone)) {
                SPUtils.getInstance().put(BigFunSDK.mContext, Constant.KEY_EMAIL, email);
                SPUtils.getInstance().put(BigFunSDK.mContext, Constant.KEY_PHONE, phone);
                activity.runOnUiThread(() -> {
                    Intent intent = new Intent(activity, PayActivity.class);
                    String url = bean.getJumpUrl().replace("email=null", "email=" + email);
                    url = url.replace("mobile=null", "mobile=" + phone);
                    intent.putExtra(Constant.EXTRA_KEY_PAY_URL, url);
                    activity.startActivity(intent);
                });
            } else {
                String saveEmail =
                        (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_EMAIL, "");
                String savePhone =
                        (String) SPUtils.getInstance().get(BigFunSDK.mContext, Constant.KEY_PHONE, "");
                if (!TextUtils.isEmpty(saveEmail) && !TextUtils.isEmpty(savePhone)) {
                    activity.runOnUiThread(() -> {
                        Intent intent = new Intent(activity, PayActivity.class);
                        String url = bean.getJumpUrl().replace("email=null", "email=" + saveEmail);
                        url = url.replace("mobile=null", "mobile=" + savePhone);
                        intent.putExtra(Constant.EXTRA_KEY_PAY_URL, url);
                        activity.startActivity(intent);
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        CustomDialog dialog = new CustomDialog(activity);
                        dialog.setOnClickListener((email1, phone1) -> {
                            SPUtils.getInstance().put(BigFunSDK.mContext, Constant.KEY_EMAIL, email1);
                            SPUtils.getInstance().put(BigFunSDK.mContext, Constant.KEY_PHONE, phone1);
                            Intent intent = new Intent(activity, PayActivity.class);
                            String url = bean.getJumpUrl().replace("email=null", "email=" + email1);
                            url = url.replace("mobile=null", "mobile=" + phone1);
                            intent.putExtra(Constant.EXTRA_KEY_PAY_URL, url);
                            activity.startActivity(intent);
                        });
                        dialog.show();
                    });
                }
            }
        }
    }

    private void paytm(PaymentOrderBean.DataBean bean, Activity activity, int requestCode) {
        String[] arr = bean.getJumpUrl().split("\\?")[1].split("&");
        Map<String, String> map = new HashMap<>();
        for (String s : arr) {
            String[] split = s.split("=");
            map.put(split[0], split[1]);
        }
        PaytmOrder paytmOrder = new PaytmOrder(
                map.get("orderId"),
                map.get("mid"),
                map.get("txnToken"),
                String.valueOf(bean.getOutPayAmount()),
                "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + map.get("orderId")
        );
        TransactionManager transactionManager =
                new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
                    @Override
                    public void onTransactionResponse(Bundle bundle) {
                        Log.d(TAG, "onTransactionResponse: ");
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.d(TAG, "networkNotAvailable: ");
                    }

                    @Override
                    public void clientAuthenticationFailed(String s) {
                        Log.d(TAG, "clientAuthenticationFailed: ");
                    }

                    @Override
                    public void someUIErrorOccurred(String s) {
                        Log.d(TAG, "someUIErrorOccurred: ");
                    }

                    @Override
                    public void onErrorLoadingWebPage(int i, String s, String s1) {
                        Log.d(TAG, "onErrorLoadingWebPage: ");
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.d(TAG, "onBackPressedCancelTransaction: ");
                    }

                    @Override
                    public void onTransactionCancel(String s, Bundle bundle) {
                        Log.d(TAG, "onTransactionCancel: ");
                    }
                });
        transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(activity, requestCode);
    }
}
