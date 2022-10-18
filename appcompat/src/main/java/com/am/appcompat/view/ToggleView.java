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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import com.am.appcompat.R;

/**
 * 切换按钮
 * Created by Alex on 2022/8/17.
 */
class ToggleView extends AppCompatImageButton {

    private final Drawable mOverflow;
    private final Drawable mBack;
    private final Drawable mOverflowToBack;
    private final Drawable mBackToOverflow;

    private final String mOverflowContentDescription;
    private final String mBackContentDescription;

    public ToggleView(@NonNull Context context) {
        super(context);

        mOverflowContentDescription = context.getString(R.string.fam_cd_overflow);
        mBackContentDescription = context.getString(R.string.fam_cd_back);

        final TypedArray a = context.obtainStyledAttributes(
                new int[]{android.R.attr.actionBarItemBackground});
        setBackgroundResource(a.getResourceId(0, 0));
        a.recycle();

        mOverflow = ContextCompat.getDrawable(context, R.drawable.fam_ic_back);
        mBack = ContextCompat.getDrawable(context, R.drawable.fam_ic_overflow);
        mOverflowToBack = ContextCompat.getDrawable(context, R.drawable.fam_ic_overflow_to_back);
        mBackToOverflow = ContextCompat.getDrawable(context, R.drawable.fam_ic_back_to_overflow);

        setScaleType(ScaleType.CENTER_INSIDE);

        setOverflow(false);
    }

    void setOverflow(boolean animate) {
        if (animate && mBackToOverflow != null) {
            setImageDrawable(mBackToOverflow);
            if (mBackToOverflow instanceof Animatable)
                ((Animatable) mBackToOverflow).start();
        } else {
            setImageDrawable(mOverflow);
        }
        setContentDescription(mOverflowContentDescription);
    }

    void setBack(@SuppressWarnings("SameParameterValue") boolean animate) {
        if (animate && mOverflowToBack != null) {
            setImageDrawable(mOverflowToBack);
            if (mOverflowToBack instanceof Animatable)
                ((Animatable) mOverflowToBack).start();
        } else {
            setImageDrawable(mBack);
        }
        setContentDescription(mBackContentDescription);
    }
}
