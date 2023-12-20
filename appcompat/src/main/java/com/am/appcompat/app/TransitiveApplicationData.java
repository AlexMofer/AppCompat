package com.am.appcompat.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 可传递的
 * Created by Alex on 2023/12/18.
 */
public abstract class TransitiveApplicationData extends ApplicationData {

    private static final String EXTRA_DATA_KEY = "com.am.appcompat.app.tad.extra.DATA_KEY";
    private int mHoldCount = 0;

    public TransitiveApplicationData(String key) {
        super(key);
    }

    /**
     * 传递
     *
     * @param intent 意图
     * @param data   数据
     */
    public static Intent transmit(@NonNull Intent intent, @NonNull ApplicationData data) {
        return intent.putExtra(EXTRA_DATA_KEY, data.getKey());
    }

    /**
     * 保存
     *
     * @param outState 状态
     * @param data     数据
     */
    public static Bundle save(@NonNull Bundle outState, @NonNull ApplicationData data) {
        outState.putString(EXTRA_DATA_KEY, data.getKey());
        return outState;
    }

    /**
     * 获取数据
     *
     * @param clazz  数据实现
     * @param intent 意图
     * @param key    键
     * @return 数据
     */
    @NonNull
    public static <T extends ApplicationData> T get(@NonNull Class<T> clazz,
                                                    @Nullable Intent intent,
                                                    @Nullable String key) {
        if (intent != null && intent.hasExtra(EXTRA_DATA_KEY)) {
            return get(clazz, intent.getStringExtra(EXTRA_DATA_KEY));
        } else {
            return get(clazz, key);
        }
    }

    /**
     * 获取数据
     *
     * @param clazz              数据实现
     * @param savedInstanceState 状态
     * @param key                键
     * @return 数据
     */
    @NonNull
    public static <T extends ApplicationData> T get(@NonNull Class<T> clazz,
                                                    @Nullable Bundle savedInstanceState,
                                                    @Nullable String key) {
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_DATA_KEY)) {
            return get(clazz, savedInstanceState.getString(EXTRA_DATA_KEY));
        } else {
            return get(clazz, key);
        }
    }

    @Override
    public void destroy() {
        if (mHoldCount <= 0) {
            super.destroy();
        } else {
            mHoldCount--;
        }
    }

    /**
     * 持有
     */
    public void hold() {
        mHoldCount++;
    }
}
