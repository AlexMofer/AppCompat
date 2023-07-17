package com.am.appcompat.view;

import android.annotation.SuppressLint;
import android.view.Menu;

import androidx.appcompat.view.menu.MenuBuilder;

/**
 * 菜单工具
 * Created by Alex on 2023/7/17.
 */
public class MenuUtils {

    private MenuUtils() {
        //no instance
    }

    /**
     * 是否显示可选图标
     *
     * @param menu    菜单
     * @param visible 是否显示
     */
    @SuppressLint("RestrictedApi")
    public static boolean setOptionalIconsVisible(Menu menu, boolean visible) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(visible);
            return true;
        }
        try {
            menu.getClass().getMethod("setOptionalIconsVisible", Boolean.TYPE)
                    .invoke(menu, visible);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
