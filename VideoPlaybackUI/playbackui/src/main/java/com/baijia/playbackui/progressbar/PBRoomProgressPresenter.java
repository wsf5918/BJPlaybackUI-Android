package com.baijia.playbackui.progressbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baijia.playbackui.R;
import com.baijia.playbackui.activity.PBRouterListener;
import com.baijia.playbackui.utils.StringUtils;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.playerview.IPlayerBottomContact;
import com.baijiahulian.player.playerview.IPlayerTopContact;

/**
 * Created by wangkangfei on 17/8/16.
 * 播放器控制条,整合所有控制视图
 */

public class PBRoomProgressPresenter implements IPlayerTopContact.TopView, IPlayerBottomContact.BottomView {
    //view
    private BJPlayerView mPlayerView;
    private ImageView ivStartPause, ivSwitchScreen;
    private TextView tvCurrent, tvTotal, tvDefinition, tvRate, tvDivider;
    private SeekBar sbMain;

    //data
    private int totalDuration; //总时长
    private int curDuration;  //当前时长

    //listener
    private PBRouterListener routerListener;

    public PBRoomProgressPresenter(View view, BJPlayerView playerView) {
        this.mPlayerView = playerView;
        initView(view);
        initListener();
    }

    private void initView(View view) {
        ivStartPause = (ImageView) view.findViewById(R.id.iv_pb_progress_start_pause);
        tvDefinition = (TextView) view.findViewById(R.id.tv_pb_progress_definition);
        tvRate = (TextView) view.findViewById(R.id.tv_pb_progress_rate);
        ivSwitchScreen = (ImageView) view.findViewById(R.id.iv_pb_progress_switch_screen);
        tvCurrent = (TextView) view.findViewById(R.id.tv_pb_progress_current_time);
        tvDivider = (TextView) view.findViewById(R.id.tv_pb_progress_separator);
        tvTotal = (TextView) view.findViewById(R.id.tv_pb_progress_total_time);
        sbMain = (SeekBar) view.findViewById(R.id.sb_pb_progress_main);
    }

    private void initListener() {
        ivStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerView != null) {
                    routerListener.changeZhanweiAndVideo();
                    if (mPlayerView.isPlaying()) {
                        mPlayerView.pauseVideo();
                    } else {
                        mPlayerView.playVideo();
                    }

                }
            }
        });
        ivSwitchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerView != null) {
                    routerListener.changeOrientation();
                }

            }
        });
        sbMain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean userTouch;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (userTouch) {
                    int pos = seekBar.getProgress() * totalDuration / 100;
                    mPlayerView.seekVideo(pos);
                }
                userTouch = false;
            }
        });
        tvDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/8/18 弹出清晰度弹窗
                if (routerListener != null) {
                    routerListener.showChoseDefinitionDlg();
                }
            }
        });
        tvRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/8/18 弹出倍速选择弹窗
                if (routerListener != null) {
                    routerListener.showChoseRateDlg();
                }
            }
        });
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
    public void setOnBackClickListener(View.OnClickListener listener) {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void setSeekBarDraggable(boolean canDrag) {

    }


    @Override
    public void onBind(IPlayerBottomContact.IPlayer player) {

    }

    @Override
    public void setDuration(int duration) {
        totalDuration = duration;
        updateVideoPosition();
    }

    @Override
    public void setCurrentPosition(int position) {
        curDuration = position;
        updateVideoPosition();
    }

    @Override
    public void setIsPlaying(boolean isPlaying) {
        if (isPlaying) {
            ivStartPause.setBackgroundResource(R.drawable.ic_video_back_pause);
        } else {
            ivStartPause.setBackgroundResource(R.drawable.ic_video_back_play);
        }
    }

    private void updateVideoPosition() {
        String durationText = StringUtils.formatDurationPB(totalDuration);
        String positionText = StringUtils.formatDurationPB(curDuration, totalDuration >= 3600);
        tvCurrent.setText(positionText);
        tvDivider.setVisibility(View.VISIBLE);
        tvTotal.setText(durationText);
        sbMain.setProgress(totalDuration == 0 ? 0 : curDuration * 100 / totalDuration);
    }

    public void setRouterListener(PBRouterListener routerListener) {
        this.routerListener = routerListener;
    }

    public void setDefinition(String definition) {
        tvDefinition.setText(definition.substring(0, 2));
    }

    public void forbidDefinitionChange() {
        tvDefinition.setEnabled(false);
    }

    public void openDefinitionChange() {
        tvDefinition.setEnabled(true);
    }

    public void setRate(String rate) {
        tvRate.setText(rate);
    }

    public void onOrientationChanged(boolean isOrientation) {
        if (isOrientation) {
            ivSwitchScreen.setImageResource(R.drawable.ic_video_back_fullscreen);
        } else {
            ivSwitchScreen.setImageResource(R.drawable.ic_video_back_huanyuan);
        }
    }
}
