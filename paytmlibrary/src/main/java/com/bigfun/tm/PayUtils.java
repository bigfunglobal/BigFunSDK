package com.bigfun.tm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bigfun.tm.model.PaymentOrderBean;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.util.HashMap;
import java.util.Map;

public class PayUtils {

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
                    int requestCode) {
        if (Integer.parseInt(bean.getPaymentChannel()) == 1) {
            if (Integer.parseInt(bean.getOpenType()) == 5) {
                paytm(bean, activity, requestCode);
            } else {
                activity.runOnUiThread(() -> {
                    Intent intent = new Intent(activity, PayActivity.class);
                    intent.putExtra(Constant.EXTRA_KEY_PAY_URL, bean.getJumpUrl());
                    activity.startActivity(intent);
                });
            }
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
            activity.runOnUiThread(() -> {
                Intent intent = new Intent(activity, PayActivity.class);
                String url = bean.getJumpUrl();
                intent.putExtra(Constant.EXTRA_KEY_PAY_URL, url);
                activity.startActivity(intent);
            });
        }
    }

    private void paytm(PaymentOrderBean.DataBean bean, Activity activity, int requestCode) {
        try {
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
                            LogUtils.log("onTransactionResponse");
                        }

                        @Override
                        public void networkNotAvailable() {
                            LogUtils.log("networkNotAvailable");
                        }

                        @Override
                        public void clientAuthenticationFailed(String s) {
                            LogUtils.log("clientAuthenticationFailed:" + s);
                        }

                        @Override
                        public void someUIErrorOccurred(String s) {
                            LogUtils.log("someUIErrorOccurred" + s);
                        }

                        @Override
                        public void onErrorLoadingWebPage(int i, String s, String s1) {
                            LogUtils.log("onErrorLoadingWebPage" + s);
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            LogUtils.log("onBackPressedCancelTransaction");
                        }

                        @Override
                        public void onTransactionCancel(String s, Bundle bundle) {
                            LogUtils.log("onTransactionCancel" + s);
                        }
                    });
            transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage");
            transactionManager.startTransaction(activity, requestCode);
        } catch (Exception e) {
            LogUtils.log(e.getMessage());
            e.printStackTrace();
        }
    }
}
