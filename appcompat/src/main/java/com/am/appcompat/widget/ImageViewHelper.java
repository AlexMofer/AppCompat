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

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * ImageView辅助
 * Created by Alex on 2022/11/23.
 */
class ImageViewHelper {

    private ImageViewHelper() {
        //no instance
    }

    @Nullable
    public static int[] fixHeight(ImageView view, int widthMeasureSpec, int heightMeasureSpec) {
        final Drawable image = view.getDrawable();
        if (image == null) {
            return null;
        }
        final int imageWidth = image.getIntrinsicWidth();
        final int imageHeight = image.getIntrinsicHeight();
        if (imageWidth <= 0 || imageHeight <= 0) {
            return null;
        }
        int width = view.getMeasuredWidth(), height = view.getMeasuredHeight();
        final int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        final int paddingLeft = view.getPaddingLeft();
        final int paddingTop = view.getPaddingTop();
        final int paddingRight = view.getPaddingRight();
        final int paddingBottom = view.getPaddingBottom();
        if (widthSpecMode == View.MeasureSpec.UNSPECIFIED) {
            width = paddingLeft + imageWidth + paddingRight;
            if (heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
                height = paddingTop + imageHeight + paddingBottom;
            } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
                height = paddingTop + imageHeight + paddingBottom;
                if (height > heightSpecSize) {
                    height = heightSpecSize;
                    final int imageHeightControlByHeight = height - paddingTop - paddingBottom;
                    width = paddingLeft + Math.round(
                            (float) imageWidth / imageHeight * imageHeightControlByHeight)
                            + paddingRight;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                final int imageHeightControlByHeight = height - paddingTop - paddingBottom;
                width = paddingLeft +
                        Math.round((float) imageWidth / imageHeight * imageHeightControlByHeight)
                        + paddingRight;
            }
        } else if (widthSpecMode == View.MeasureSpec.AT_MOST) {
            width = Math.min(paddingLeft + imageWidth + paddingRight, widthSpecSize);
            final int imageWidthControlByWidth = width - paddingLeft - paddingRight;
            if (heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
                height = paddingTop + Math.round(
                        (float) imageHeight / imageWidth * imageWidthControlByWidth)
                        + paddingBottom;
            } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
                height = paddingTop + Math.round(
                        (float) imageHeight / imageWidth * imageWidthControlByWidth)
                        + paddingBottom;
                if (height > heightSpecSize) {
                    height = heightSpecSize;
                    final int imageHeightControlByHeight = height - paddingTop - paddingBottom;
                    width = paddingLeft + Math.round(
                            (float) imageWidth / imageHeight * imageHeightControlByHeight)
                            + paddingRight;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                final int imageHeightControlByHeight = height - paddingTop - paddingBottom;
                width = paddingLeft +
                        Math.round((float) imageWidth / imageHeight * imageHeightControlByHeight)
                        + paddingRight;
            }
        } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
            width = widthSpecSize;
            final int imageWidthControlByWidth = width - paddingLeft - paddingRight;
            if (heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
                height = paddingTop + Math.round(
                        (float) imageHeight / imageWidth * imageWidthControlByWidth)
                        + paddingBottom;
            } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
                height = paddingTop + Math.round(
                        (float) imageHeight / imageWidth * imageWidthControlByWidth)
                        + paddingBottom;
                if (height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                height = heightSpecSize;
            }
        }
        return new int[]{width, height};
    }
}
