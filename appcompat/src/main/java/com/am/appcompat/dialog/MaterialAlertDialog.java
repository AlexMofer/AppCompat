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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * 材料设计的AlertDialog
 * Created by Alex on 2022/4/26.
 */
@Deprecated
public class MaterialAlertDialog extends AlertDialog {

    public MaterialAlertDialog(@NonNull Context context) {
        super(MaterialDialogHelper.createMaterialAlertDialogThemedContext(context));
        MaterialDialogHelper.setup(this);
    }

    public MaterialAlertDialog(@NonNull Context context, int theme) {
        super(MaterialDialogHelper.createMaterialAlertDialogThemedContext(context),
                MaterialDialogHelper.getOverridingThemeResId(context, theme));
        MaterialDialogHelper.setup(this);
    }

    public MaterialAlertDialog(@NonNull Context context, boolean cancelable,
                               @Nullable OnCancelListener cancelListener) {
        super(MaterialDialogHelper.createMaterialAlertDialogThemedContext(context),
                cancelable, cancelListener);
        MaterialDialogHelper.setup(this);
    }
}
