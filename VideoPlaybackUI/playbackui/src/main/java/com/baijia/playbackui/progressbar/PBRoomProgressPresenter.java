package com.baijia.playbackui.progressbar;

import android.view.View;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.IPlayerBottomContact;
import com.baijiahulian.player.playerview.IPlayerCenterContact;
import com.baijiahulian.player.playerview.IPlayerTopContact;

/**
 * Created by wangkangfei on 17/8/16.
 * 播放器控制条
 */

public class PBRoomProgressPresenter implements IPlayerTopContact.TopView, IPlayerBottomContact.BottomView, IPlayerCenterContact.CenterView {
    private View view;
    private BJPlayerView mPlayerView;

    public PBRoomProgressPresenter(View view, BJPlayerView playerView) {
        this.view = view;
        this.mPlayerView = playerView;
    }

    @Override
    public void onBind(IPlayerTopContact.IPlayer player) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setOrientation(int orientation) {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void showProgressSlide(int delta) {

    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showVolumeSlide(int volume, int maxVolume) {

    }

    @Override
    public void showBrightnessSlide(int brightness) {

    }

    @Override
    public void showError(int what, int extra) {

    }

    @Override
    public void showError(int code, String message) {

    }

    @Override
    public void showWarning(String warn) {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void onVideoInfoLoaded(VideoItem videoItem) {

    }

    @Override
    public boolean isDialogShowing() {
        return false;
    }

    @Override
    public void updateDefinition() {

    }

    @Override
    public void setOnBackClickListener(View.OnClickListener listener) {

    }

    @Override
    public void onBind(IPlayerCenterContact.IPlayer player) {

    }

    @Override
    public boolean onBackTouch() {
        return false;
    }

    @Override
    public void onBind(IPlayerBottomContact.IPlayer player) {

    }

    @Override
    public void setDuration(int duration) {

    }

    @Override
    public void setCurrentPosition(int position) {

    }

    @Override
    public void setIsPlaying(boolean isPlaying) {

    }
}
