package com.am.appcompat.app;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;

/**
 * 应用数据
 * Created by Alex on 2023/12/15.
 */
public abstract class ApplicationData {
    private final String mKey;

    public ApplicationData(String key) {
        mKey = key;
        ApplicationHolder.addData(mKey, this);
    }

    /**
     * 获取数据
     *
     * @param clazz 数据实现
     * @param key   键
     * @return 数据
     */
    @NonNull
    public static <T extends ApplicationData> T get(@NonNull Class<T> clazz,
                                                    @Nullable String key) {
        if (key == null) {
            key = clazz.getName();
        }
        T data = ApplicationHolder.getData(key);
        if (data != null) {
            return data;
        }
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println(constructor);
        }
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor(String.class);
            if (constructor.isAccessible()) {
                data = constructor.newInstance(key);
            } else {
                constructor.setAccessible(true);
                data = constructor.newInstance(key);
                constructor.setAccessible(false);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return data;
    }

    /**
     * 获取数据
     *
     * @param clazz 数据实现
     * @return 数据
     */
    @NonNull
    public static <T extends ApplicationData> T get(@NonNull Class<T> clazz) {
        return get(clazz, (String) null);
    }

    protected final String getKey() {
        return mKey;
    }

    /**
     * 配置变化
     *
     * @param newConfig 新的配置
     */
    protected void onConfigurationChanged(@NonNull Configuration newConfig) {
    }

    /**
     * 减少内存占用
     *
     * @param level 等级
     */
    protected void onTrimMemory(int level) {
    }

    /**
     * 销毁
     */
    public void destroy() {
        ApplicationHolder.removeData(mKey);
        onDestroy();
    }

    /**
     * 已销毁
     */
    protected void onDestroy() {
    }
}
