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
package com.am.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * 修复高度的ShapeableImageView
 * Created by Alex on 2022/11/23.
 */
public class ShapeableImageView extends com.google.android.material.imageview.ShapeableImageView {

    public ShapeableImageView(Context context) {
        super(context);
    }

    public ShapeableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeableImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int[] size = ImageViewHelper.fixHeight(this, widthMeasureSpec, heightMeasureSpec);
        if (size == null) {
            return;
        }
        setMeasuredDimension(size[0], size[1]);
    }
}
