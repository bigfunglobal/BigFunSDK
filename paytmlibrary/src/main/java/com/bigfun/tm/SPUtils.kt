package com.bigfun.tm

import android.content.Context

private const val FILE_NAME = "share_data_sp"

class SPUtils {

    companion object {
        val instance: SPUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SPUtils()
        }
    }

    /**
     * 存储值
     */
    fun put(context: Context, key: String, value: Any) {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit()
        when (value) {
            is String -> {
                sp.putString(key, value)
            }
            is Int -> {
                sp.putInt(key, value)
            }
            is Float -> {
                sp.putFloat(key, value)
            }
            is Boolean -> {
                sp.putBoolean(key, value)
            }
            is Long -> {
                sp.putLong(key, value)
            }
            else -> {
                throw IllegalArgumentException("error value")
            }
        }
        sp.apply()
    }

    /**
     * 获取存储的值
     */
    fun get(context: Context, key: String, defaultValue: Any): Any? {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        when (defaultValue) {
            is String -> {
                return sp.getString(key, defaultValue)
            }
            is Int -> {
                return sp.getInt(key, defaultValue)
            }
            is Float -> {
                return sp.getFloat(key, defaultValue)
            }
            is Boolean -> {
                return sp.getBoolean(key, defaultValue)
            }
            is Long -> {
                return sp.getLong(key, defaultValue)
            }
            else -> {
                return null
            }
        }
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    fun remove(context: Context, key: String) {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.remove(key)
        editor.apply()
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    fun clear(context: Context) {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }

}