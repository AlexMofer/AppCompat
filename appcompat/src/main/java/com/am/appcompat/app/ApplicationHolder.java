/*
 * Copyright (C) 2020 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.am.appcompat.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.collection.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Application 持有者
 * Created by Alex on 2020/3/7.
 */
public final class ApplicationHolder {

    private static ApplicationHolder mInstance;
    private final ArrayList<ApplicationStateCallback> mCallbacks = new ArrayList<>();
    private final ArrayList<Intent> mAutoStartActivities = new ArrayList<>();
    private final ArrayMap<String, ApplicationData> mData = new ArrayMap<>();
    private final Application mApplication;
    private int mActivityStartedCount = 0;
    private boolean mIgnoreForegroundOnce = false;
    private WeakReference<Activity> mStartedActivity;
    private WeakReference<Activity> mResumedActivity;

    private ApplicationHolder(Application application) {
        mApplication = application;
        application.registerActivityLifecycleCallbacks(new InnerActivityLifecycleCallbacks());
        application.registerComponentCallbacks(new InnerComponentCallbacks());
    }

    /**
     * 创建
     *
     * @param application Application
     */
    public static void create(Application application) {
        mInstance = new ApplicationHolder(application);
    }

    /**
     * Return the application.
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <T extends Application> T getApplication() {
        //noinspection unchecked
        return (T) mInstance.mApplication;
    }

    /**
     * 获取 Application级别 Context
     *
     * @return Context
     */
    public static Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    /**
     * 发送Toast
     *
     * @param text     文本
     * @param duration 时长
     */
    public static void toast(CharSequence text, int duration) {
        Toast.makeText(ApplicationHolder.getApplicationContext(), text, duration).show();
    }

    /**
     * 发送Toast
     *
     * @param text 文本
     */
    public static void toast(CharSequence text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    /**
     * 发送Toast
     *
     * @param resId    文本资源
     * @param duration 时长
     */
    public static void toast(@StringRes int resId, int duration) {
        Toast.makeText(ApplicationHolder.getApplicationContext(), resId, duration).show();
    }

    /**
     * 发送Toast
     *
     * @param resId 文本资源
     */
    public static void toast(@StringRes int resId) {
        toast(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 注册应用状态回调
     *
     * @param callback 应用状态回调
     */
    public static void registerApplicationStateCallback(ApplicationStateCallback callback) {
        if (callback == null) {
            return;
        }
        mInstance.mCallbacks.add(callback);
    }

    /**
     * 取消注册应用状态回调
     *
     * @param callback 应用状态回调
     */
    public static void unregisterApplicationStateCallback(ApplicationStateCallback callback) {
        if (callback == null) {
            return;
        }
        mInstance.mCallbacks.remove(callback);
    }

    /**
     * 判断应用是否处于前台
     *
     * @return 处于前台时返回true
     */
    public static boolean isForeground() {
        return mInstance.mActivityStartedCount > 0;
    }

    /**
     * 获取已开始运行的 Activity
     *
     * @return Activity
     */
    @Nullable
    public static Activity getStartedActivity() {
        return mInstance.mStartedActivity == null ? null : mInstance.mStartedActivity.get();
    }

    /**
     * 获取正在运行的 Activity
     *
     * @return Activity
     */
    @Nullable
    public static Activity getResumedActivity() {
        return mInstance.mResumedActivity == null ? null : mInstance.mResumedActivity.get();
    }

    /**
     * 添加自启动Activity
     *
     * @param intent 意图
     */
    public static void addAutoStartActivity(Intent intent) {
        if (intent == null) {
            return;
        }
        final Activity activity = getResumedActivity();
        if (activity != null && !(activity instanceof NoAllowedStartActivity)) {
            activity.startActivity(intent);
            return;
        }
        mInstance.add(intent);
    }

    @Nullable
    static <T extends ApplicationData> T getData(String key) {
        //noinspection unchecked
        return (T) mInstance.mData.get(key);
    }

    static void addData(String key, @NonNull ApplicationData value) {
        mInstance.mData.put(key, value);
    }

    static void removeData(String key) {
        mInstance.mData.remove(key);
    }

    private void add(Intent intent) {
        if (intent == null) {
            return;
        }
        synchronized (mAutoStartActivities) {
            final int count = mAutoStartActivities.size();
            for (int i = 0; i < count; i++) {
                final Intent activity = mAutoStartActivities.get(i);
                if (Objects.equals(activity.getComponent(), intent.getComponent())) {
                    mAutoStartActivities.add(i, intent);
                    mAutoStartActivities.remove(i + 1);
                    return;
                }
            }
            mAutoStartActivities.add(intent);
        }
    }

    /**
     * 应用状态回调
     */
    public interface ApplicationStateCallback {

        /**
         * 应用进入前台
         *
         * @param application Application
         * @param activity    Activity
         */
        void onForeground(@NonNull Application application, @NonNull Activity activity);

        /**
         * 应用进入后台
         *
         * @param application Application
         */
        void onBackground(@NonNull Application application);
    }

    /**
     * 不允许用于启动Activity
     */
    public interface NoAllowedStartActivity {
    }

    private class InnerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity,
                                      @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            mStartedActivity = new WeakReference<>(activity);
            if (mActivityStartedCount == 0) {
                if (mIgnoreForegroundOnce) {
                    mIgnoreForegroundOnce = false;
                } else {
                    for (ApplicationStateCallback callback : mCallbacks) {
                        callback.onForeground(mApplication, activity);
                    }
                }
            }
            mActivityStartedCount++;
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            mResumedActivity = new WeakReference<>(activity);
            if (activity instanceof NoAllowedStartActivity) {
                return;
            }
            final ArrayList<Intent> intents;
            synchronized (mAutoStartActivities) {
                intents = new ArrayList<>(mAutoStartActivities);
                mAutoStartActivities.clear();

            }
            for (Intent intent : intents) {
                activity.startActivity(intent);
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (mResumedActivity != null) {
                if (activity == mResumedActivity.get()) {
                    mResumedActivity = null;
                }
            }
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            if (mStartedActivity != null) {
                if (activity == mStartedActivity.get()) {
                    mStartedActivity = null;
                }
            }
            mActivityStartedCount--;
            if (mActivityStartedCount == 0) {
                if (activity.isChangingConfigurations()) {
                    mIgnoreForegroundOnce = true;
                } else {
                    for (ApplicationStateCallback callback : mCallbacks) {
                        callback.onBackground(mApplication);
                    }
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity,
                                                @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    }

    private class InnerComponentCallbacks implements ComponentCallbacks2 {

        @Override
        public void onConfigurationChanged(@NonNull Configuration newConfig) {
            final int count = mData.size();
            for (int i = 0; i < count; i++) {
                mData.valueAt(i).onConfigurationChanged(newConfig);
            }
        }

        @Override
        public void onLowMemory() {
            // ignore
        }

        @Override
        public void onTrimMemory(int level) {
            final int count = mData.size();
            for (int i = 0; i < count; i++) {
                mData.valueAt(i).onTrimMemory(level);
            }
        }
    }
}