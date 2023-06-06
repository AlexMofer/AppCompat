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
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Application 持有者
 * Created by Alex on 2020/3/7.
 */
public final class ApplicationHolder {

    private static ApplicationHolder mInstance;
    private final Application.ActivityLifecycleCallbacks mCallback =
            new InnerActivityLifecycleCallbacks();
    private final ArrayList<ApplicationStateCallback> mCallbacks = new ArrayList<>();
    private final Application mApplication;
    private WeakReference<Activity> mResumedActivity;
    private int mActivityCount = 0;
    private WeakReference<Activity> mStartedActivity;

    private ApplicationHolder(Application application) {
        mApplication = application;
        application.registerActivityLifecycleCallbacks(mCallback);
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
     * 销毁
     *
     * @param application Application
     */
    public static void destroy(Application application) {
        mInstance.destroy();
        mInstance = null;
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
     * 获取正在运行的 Activity
     *
     * @return Activity
     */
    @Nullable
    public static Activity getResumedActivity() {
        return mInstance.mResumedActivity == null ? null : mInstance.mResumedActivity.get();
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
        return mInstance.mActivityCount > 0;
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

    private void destroy() {
        mApplication.unregisterActivityLifecycleCallbacks(mCallback);
        mCallbacks.clear();
        mResumedActivity = null;
        mStartedActivity = null;
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

    private class InnerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            mStartedActivity = new WeakReference<>(activity);
            if (mActivityCount == 0) {
                for (ApplicationStateCallback callback : mCallbacks) {
                    callback.onForeground(mApplication, activity);
                }
            }
            mActivityCount++;
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            mResumedActivity = new WeakReference<>(activity);
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
            mActivityCount--;
            if (mActivityCount == 0) {
                for (ApplicationStateCallback callback : mCallbacks) {
                    callback.onBackground(mApplication);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

}