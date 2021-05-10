package com.am.appcompat.ui;

import com.am.mvp.core.MVPModel;

class MainModel extends MVPModel<MainPresenter> implements MainDataAdapter {

    private String mText;

    @Override
    public void load() {
        mText = "Text from model.";
        final MainPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onDataLoaded();
        }
    }

    @Override
    public String getText() {
        return mText;
    }
}
