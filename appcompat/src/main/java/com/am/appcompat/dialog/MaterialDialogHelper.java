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
package com.am.appcompat.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.view.ViewCompat;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.InsetDialogOnTouchListener;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.resources.MaterialAttributes;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

/**
 * 材料设计对话框辅助
 * Created by Alex on 2022/4/26.
 */
@SuppressLint({"RestrictedApi", "PrivateResource"})
class MaterialDialogHelper {

    private static final int DEF_STYLE_ATTR = com.google.android.material.R.attr.alertDialogStyle;
    private static final int DEF_STYLE_RES = com.google.android.material.R.style.MaterialAlertDialog_MaterialComponents;
    private static final int MATERIAL_ALERT_DIALOG_THEME_OVERLAY =
            com.google.android.material.R.attr.materialAlertDialogTheme;

    private MaterialDialogHelper() {
        //no instance
    }

    private static int getMaterialAlertDialogThemeOverlay(@NonNull Context context) {
        TypedValue materialAlertDialogThemeOverlay =
                MaterialAttributes.resolve(context, MATERIAL_ALERT_DIALOG_THEME_OVERLAY);
        if (materialAlertDialogThemeOverlay == null) {
            return 0;
        }
        return materialAlertDialogThemeOverlay.data;
    }

    public static Context createMaterialAlertDialogThemedContext(@NonNull Context context) {
        int themeOverlayId = getMaterialAlertDialogThemeOverlay(context);
        Context themedContext =
                MaterialThemeOverlay.wrap(context, null, DEF_STYLE_ATTR, DEF_STYLE_RES);
        if (themeOverlayId == 0) {
            return themedContext;
        }
        return new ContextThemeWrapper(themedContext, themeOverlayId);
    }

    public static int getOverridingThemeResId(@NonNull Context context, int overrideThemeResId) {
        return overrideThemeResId == 0
                ? getMaterialAlertDialogThemeOverlay(context)
                : overrideThemeResId;
    }

    public static void setup(final AppCompatDialog dialog) {
        // Ensure we are using the correctly themed context rather than the context that was passed in.
        final Context context = dialog.getContext();
        Resources.Theme theme = context.getTheme();

        Rect backgroundInsets = MaterialDialogs.getDialogBackgroundInsets(context, DEF_STYLE_ATTR, DEF_STYLE_RES);

        int surfaceColor =
                MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurface,
                        dialog.getClass().getCanonicalName());
        MaterialShapeDrawable materialShapeDrawable =
                new MaterialShapeDrawable(context, null, DEF_STYLE_ATTR, DEF_STYLE_RES);
        materialShapeDrawable.initializeElevationOverlay(context);
        materialShapeDrawable.setFillColor(ColorStateList.valueOf(surfaceColor));

        // dialogCornerRadius first appeared in Android Pie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TypedValue dialogCornerRadiusValue = new TypedValue();
            theme.resolveAttribute(android.R.attr.dialogCornerRadius, dialogCornerRadiusValue, true);
            float dialogCornerRadius =
                    dialogCornerRadiusValue.getDimension(context.getResources().getDisplayMetrics());
            if (dialogCornerRadiusValue.type == TypedValue.TYPE_DIMENSION && dialogCornerRadius >= 0) {
                materialShapeDrawable.setCornerSize(dialogCornerRadius);
            }
        }
        Window window = dialog.getWindow();
        /* {@link Window#getDecorView()} should be called before any changes are made to the Window
         * as it locks in attributes and affects layout. */
        View decorView = window.getDecorView();
        ((MaterialShapeDrawable) materialShapeDrawable).setElevation(ViewCompat.getElevation(decorView));

        Drawable insetDrawable = MaterialDialogs.insetDrawable(materialShapeDrawable, backgroundInsets);
        window.setBackgroundDrawable(insetDrawable);
        decorView.setOnTouchListener(new InsetDialogOnTouchListener(dialog, backgroundInsets));
    }
}
