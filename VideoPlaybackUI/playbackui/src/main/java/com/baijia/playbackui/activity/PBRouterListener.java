package com.baijia.playbackui.activity;

/**
 * Created by wangkangfei on 17/8/17.
 */

public interface PBRouterListener {
    /**
     * 选择清晰度
     */
    void showChoseDefinitionDlg();

    /**
     * 选择播放倍速
     */
    void showChoseRateDlg();

    /**
     * 切换视频和站位图
     */
    void changeZhanweiAndVideo();

    /**
     * 切换横屏竖屏
     */
    boolean changeOrientation();

    void selectDefinition(String type, int position);


}
