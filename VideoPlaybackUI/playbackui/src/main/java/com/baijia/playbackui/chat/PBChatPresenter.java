package com.baijia.playbackui.chat;

import com.baijia.playbackui.activity.PBRouterListener;

/**
 * Created by wangkangfei on 17/8/17.
 */

public class PBChatPresenter implements PBChatContract.Presenter {
    private PBChatContract.View view;

    public PBChatPresenter(PBChatContract.View view) {
        this.view = view;
    }

    @Override
    public void setRouter(PBRouterListener listener) {

    }

    @Override
    public void create() {

    }

    @Override
    public void destroy() {

    }
}
