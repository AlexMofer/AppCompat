package com.am.appcompat.app;

/**
 * 对话框持有者
 * Created by Alex on 2023/6/21.
 */
public interface DialogHolder {

    /**
     * 添加对话框
     *
     * @param dialog 对话框
     */
    void addDialog(AppCompatDialog dialog);

    /**
     * 移除对话框
     *
     * @param dialog 对话框
     */
    void removeDialog(AppCompatDialog dialog);
}
