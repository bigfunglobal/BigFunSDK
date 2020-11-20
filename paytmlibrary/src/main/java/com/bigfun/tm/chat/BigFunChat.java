package com.bigfun.tm.chat;

import android.content.Context;

import zendesk.chat.Chat;
import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.messaging.MessagingActivity;

public class BigFunChat {
    private static final String ACCOUNT_KEY = "oYHRItbGeLakwycsAUxG8R0ZmooqeuIa";
    private static Context mContext;

    private BigFunChat() {
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
        Chat.INSTANCE.init(context, ACCOUNT_KEY);
    }

    private static class InstanceHolder {
        private static BigFunChat INSTANCE = new BigFunChat();
    }

    public static BigFunChat getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void chat() {
        ChatConfiguration chatConfiguration = ChatConfiguration.builder()
                .withPreChatFormEnabled(false)
                .build();
        MessagingActivity.builder().withEngines(ChatEngine.engine())
                .show(mContext, chatConfiguration);
    }
}
