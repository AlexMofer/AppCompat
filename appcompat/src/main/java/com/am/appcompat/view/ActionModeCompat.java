/*
 * Copyright (C) 2022 AlexMofer
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
package com.am.appcompat.view;

import android.graphics.Rect;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.RequiresApi;

/**
 * ActionMode兼容器
 * Created by Alex on 2022/8/10.
 */
public class ActionModeCompat {


    /**
     * The action mode is treated as a Primary mode. This is the default.
     * Use with {@link ActionMode#setType}.
     */
    public static final int TYPE_PRIMARY = 0;
    /**
     * The action mode is treated as a Floating Toolbar.
     * Use with {@link ActionMode#setType}.
     */
    public static final int TYPE_FLOATING = 1;

    /**
     * Default value to hide the action mode for
     * {@link ViewConfiguration#getDefaultActionModeHideDuration()}.
     */
    public static final int DEFAULT_HIDE_DURATION = -1;

    /**
     * Start an action mode with the given type.
     *
     * @param callback Callback that will control the lifecycle of the action mode
     * @param type     One of {@link ActionMode#TYPE_PRIMARY} or {@link ActionMode#TYPE_FLOATING}.
     * @return The new action mode if it is started, null otherwise
     * @see ActionMode
     */
    public static ActionMode startActionMode(View view, Callback callback, int type) {
        if (type == TYPE_PRIMARY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return view.startActionMode(callback, ActionMode.TYPE_PRIMARY);
            } else {
                return view.startActionMode(callback);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return view.startActionMode(new CallbackWrapper(callback),
                        ActionMode.TYPE_FLOATING);
            } else {
                final FloatingActionMode mode = new FloatingActionMode(view, callback);
                return mode.start() ? mode : null;
            }
        }
    }

    /**
     * Invalidate the content rect associated to this ActionMode. This only makes sense for
     * action modes that support dynamic positioning on the screen, and provides a more efficient
     * way to reposition it without invalidating the whole action mode.
     *
     * @see ActionMode.Callback2#onGetContentRect(ActionMode, View, Rect) .
     */
    public static void invalidateContentRect(ActionMode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mode.invalidateContentRect();
        } else {
            if (mode instanceof FloatingActionMode) {
                ((FloatingActionMode) mode).invalidateContentRect();
            }
        }
    }

    /**
     * Hide the action mode view from obstructing the content below for a short duration.
     * This only makes sense for action modes that support dynamic positioning on the screen.
     * If this method is called again before the hide duration expires, the later hide call will
     * cancel the former and then take effect.
     * NOTE that there is an internal limit to how long the mode can be hidden for. It's typically
     * about a few seconds.
     *
     * @param duration The number of milliseconds to hide for.
     * @see #DEFAULT_HIDE_DURATION
     */
    public static void hide(ActionMode mode, long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mode.hide(duration);
        } else {
            if (mode instanceof FloatingActionMode) {
                ((FloatingActionMode) mode).hide(duration);
            }
        }
    }

    /**
     * Extension of {@link ActionMode.Callback} to provide content rect information. This is
     * required for ActionModes with dynamic positioning such as the ones with type
     * {@link ActionMode#TYPE_FLOATING} to ensure the positioning doesn't obscure app content. If
     * an app fails to provide a subclass of this class, a default implementation will be used.
     */
    public interface Callback extends ActionMode.Callback {

        /**
         * Called when an ActionMode needs to be positioned on screen, potentially occluding view
         * content. Note this may be called on a per-frame basis.
         *
         * @param mode    The ActionMode that requires positioning.
         * @param view    The View that originated the ActionMode, in whose coordinates the Rect should
         *                be provided.
         * @param outRect The Rect to be populated with the content position. Use this to specify
         *                where the content in your app lives within the given view. This will be used
         *                to avoid occluding the given content Rect with the created ActionMode.
         */
        default void onGetContentRect(ActionMode mode, View view, Rect outRect) {
            if (view != null) {
                outRect.set(0, 0, view.getWidth(), view.getHeight());
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static class CallbackWrapper extends ActionMode.Callback2 {
        private final Callback mWrapped;

        CallbackWrapper(Callback wrapped) {
            mWrapped = wrapped;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onCreateActionMode(mode, menu);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onPrepareActionMode(mode, menu);
        }

        @Override
        public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
            mWrapped.onGetContentRect(mode, view, outRect);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            mWrapped.onDestroyActionMode(mode);
        }
    }
}
