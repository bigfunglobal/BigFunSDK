package com.bigfun.tm.database;

import android.database.sqlite.SQLiteDatabase;

import com.bigfun.tm.BigFunSDK;
import com.bigfun.tm.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private ReportDBHelper reportDBHelper;

    private EventManager() {
        reportDBHelper = new ReportDBHelper(BigFunSDK.mContext, "report.db", null, 1);
    }

    public static EventManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static EventManager instance = new EventManager();
    }

    /**
     * 添加事件
     *
     * @param action
     * @param content
     */
    public synchronized void addEvent(String action, String content) {
        if (reportDBHelper != null) {
            SQLiteDatabase database = reportDBHelper.getWritableDatabase();
            reportDBHelper.insert(database, action, content);
            upload();
        }
    }

    /**
     * 上报事件
     */
    public synchronized void upload() {
        if (reportDBHelper != null) {
            SQLiteDatabase database = reportDBHelper.getWritableDatabase();
            List<EventBean> beanList = reportDBHelper.query(database);
            if (beanList != null && beanList.size() > 0) {
                HttpUtils.getInstance().upload(beanList);
            }
        }
    }

    /**
     * 删除数据
     */
    public synchronized void delete() {
        if (reportDBHelper != null) {
            SQLiteDatabase database = reportDBHelper.getWritableDatabase();
            reportDBHelper.delete(database);
        }
    }

    /**
     * 查询
     *
     * @return
     */
    public synchronized void query() {
        SQLiteDatabase database = reportDBHelper.getWritableDatabase();
        List<EventBean> list = reportDBHelper.query(database);
        if (list != null && list.size() > 0) {
            HttpUtils.getInstance().upload(list);
        }
    }
}
