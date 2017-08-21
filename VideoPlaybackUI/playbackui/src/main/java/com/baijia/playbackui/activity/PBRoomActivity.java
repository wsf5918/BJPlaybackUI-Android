package com.baijia.playbackui.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baijia.playbackui.R;
import com.baijia.playbackui.base.PBBasePresenter;
import com.baijia.playbackui.base.PBBaseView;
import com.baijia.playbackui.chat.PBChatFragment;
import com.baijia.playbackui.chat.PBChatPresenter;
import com.baijia.playbackui.progressbar.PBRoomProgressPresenter;
import com.baijia.playbackui.utils.ConstantUtil;
import com.baijia.playbackui.utils.PBDisplayUtils;
import com.baijia.playbackui.viewsupport.AutoExitDrawerLayout;
import com.baijia.playbackui.viewsupport.PBDragFrameLayout;
import com.baijia.player.playback.LivePlaybackSDK;
import com.baijia.player.playback.PBRoom;
import com.baijia.player.playback.mocklive.OnPlayerListener;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.BJCenterViewPresenter;

public class PBRoomActivity extends PBBaseActivity implements LPLaunchListener, PBRouterListener {
    //view
    private MaterialDialog launchStepDlg;
    private BJPlayerView mPlayerView;
    private FrameLayout flContainerProgress, flContainerBig, flContainerSmall;
    private ImageView ivQuitRoom, ivChatSwitch;
    private PBRoomProgressPresenter progressPresenter;
    private AutoExitDrawerLayout dlChat;
    private FrameLayout flAreaSwitch;

    //fragment
    private PBChatFragment chatFragment;
    private LPPPTFragment pptFragment;

    //data
    private PBRoom mRoom;
    private String roomId, roomToken;
    private int deployType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbroom);
        initView();
        initLaunchStepDlg();
        initListeners();
        initData();
    }

    private void initView() {
        ivQuitRoom = (ImageView) findViewById(R.id.iv_pb_exit);
        mPlayerView = (BJPlayerView) findViewById(R.id.pb_pv_main);
        flContainerProgress = (FrameLayout) findViewById(R.id.fl_pb_container_progress);
        dlChat = (AutoExitDrawerLayout) findViewById(R.id.dl_pb_chat);
        flAreaSwitch = (FrameLayout) findViewById(R.id.fl_pb_container_freedom_small);
        flContainerBig = (FrameLayout) findViewById(R.id.fl_pb_container_big);
        flContainerSmall = (FrameLayout) findViewById(R.id.fl_pb_container_small);
        ivChatSwitch = (ImageView) findViewById(R.id.iv_pb_chat_switch);

        dlChat.openDrawer(Gravity.START);
        dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    private void initListeners() {
        ivQuitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        flAreaSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/8/18 switch ppt and video
                switchPPTAndVideo();
            }
        });
        ivChatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/8/18 横屏状态打开和关闭聊天fragment
            }
        });
    }

    private void initData() {
        roomId = getIntent().getStringExtra(ConstantUtil.PB_ROOM_ID);
        roomToken = getIntent().getStringExtra(ConstantUtil.PB_ROOM_TOKEN);
        deployType = getIntent().getIntExtra(ConstantUtil.PB_ROOM_DEPLOY, 2);
        doEnterRoom();
    }

    private void initLaunchStepDlg() {
        launchStepDlg = new MaterialDialog.Builder(this)
                .title("正在加载...")
                .progress(true, 100, false)
                .cancelable(true)
                .build();
    }

    private void doEnterRoom() {
        View view = LayoutInflater.from(this).inflate(R.layout.pb_player_controller_view, null, false);
        progressPresenter = new PBRoomProgressPresenter(view, mPlayerView);
        progressPresenter.setRouterListener(this);

        mPlayerView.setTopPresenter(progressPresenter);
        mPlayerView.setBottomPresenter(progressPresenter);
        mPlayerView.setCenterPresenter(new BJCenterViewPresenter(mPlayerView.getCenterView()));
        mPlayerView.enableBrightnessGesture(false);
        mPlayerView.enableSeekGesture(false);
        mPlayerView.enableVolumeGesture(false);
        mPlayerView.setForbidConfiguration(true);

        flContainerProgress.addView(view);
        //enter room action
        switch (deployType) {
            case 0:
                mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(roomId), roomToken, LPConstants.LPDeployType.Test);
                break;
            case 1:
                mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(roomId), roomToken, LPConstants.LPDeployType.Beta);
                break;
            case 2:
                mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(roomId), roomToken, LPConstants.LPDeployType.Product);
                break;
            default:
                break;
        }
        mRoom.bindPlayerView(mPlayerView);
        mRoom.setOnPlayerListener(onPlayerListener);
        mRoom.enterRoom(this);
        launchStepDlg.show();
    }

    //进入房间的三个回调
    @Override
    public void onLaunchSteps(int step, int totalStep) {
        if (launchStepDlg == null) {
            return;
        }
        launchStepDlg.getProgressBar().setMax(totalStep);
        launchStepDlg.getProgressBar().setProgress(step);
    }

    @Override
    public void onLaunchError(LPError lpError) {
        if (launchStepDlg != null) {
            launchStepDlg.dismiss();
        }
        Toast.makeText(PBRoomActivity.this, lpError.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLaunchSuccess(LiveRoom liveRoom) {
        if (launchStepDlg != null) {
            launchStepDlg.dismiss();
        }
        launchSuccess();
    }

    /**
     * 进入房间成功
     */
    private void launchSuccess() {
        chatFragment = new PBChatFragment();
        chatFragment.setRoom(mRoom);
        bindVP(chatFragment, new PBChatPresenter(chatFragment));
        addFragment(R.id.fl_pb_chat_content_container, chatFragment);

        pptFragment = new LPPPTFragment();
        pptFragment.setLiveRoom(mRoom);
        pptFragment.setFlingEnable(false);
        addFragment(R.id.fl_pb_container_big, pptFragment);
    }

    private <V extends PBBaseView, P extends PBBasePresenter> void bindVP(V view, P presenter) {
        presenter.setRouter(this);
        view.setPresenter(presenter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // TODO: 17/8/18 横竖屏切换逻辑
        doOnBigContainerConfigurationChanged(newConfig);
        doOnSmallContainerConfigurationChanged(newConfig);
        doOnChatDrawerConfigurationChanged(newConfig);
    }

    /**
     * 上方big container
     */
    private void doOnBigContainerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lpBigContainer = (RelativeLayout.LayoutParams) flContainerBig.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lpBigContainer.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lpBigContainer.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            lpBigContainer.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lpBigContainer.height = PBDisplayUtils.dip2px(this, 240);
        }
        flContainerBig.setLayoutParams(lpBigContainer);
    }

    /**
     * 下方small container
     */
    private void doOnSmallContainerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lpSmallContainer = (RelativeLayout.LayoutParams) flContainerSmall.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lpSmallContainer.width = PBDisplayUtils.dip2px(this, 150);
            lpSmallContainer.height = PBDisplayUtils.dip2px(this, 90);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        } else {
            lpSmallContainer.width = PBDisplayUtils.dip2px(this, 150);
            lpSmallContainer.height = PBDisplayUtils.dip2px(this, 90);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpSmallContainer.addRule(RelativeLayout.BELOW, R.id.fl_pb_container_big);
        }
        flContainerSmall.setLayoutParams(lpSmallContainer);
    }

    /**
     * 聊天drawer
     */
    private void doOnChatDrawerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lpChatDrawer = (RelativeLayout.LayoutParams) dlChat.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            lpChatDrawer.width = PBDisplayUtils.dip2px(this, 268);
            lpChatDrawer.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (flContainerSmall.getVisibility() == View.VISIBLE) {
                lpChatDrawer.addRule(RelativeLayout.BELOW, R.id.fl_pb_container_small);
            } else {
                lpChatDrawer.addRule(RelativeLayout.BELOW, R.id.view_pb_anchor_left_top);
            }
            lpChatDrawer.addRule(RelativeLayout.ABOVE, R.id.iv_pb_chat_switch);
            ivChatSwitch.setVisibility(View.VISIBLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            lpChatDrawer.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lpChatDrawer.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lpChatDrawer.addRule(RelativeLayout.BELOW, R.id.fl_pb_container_big);
            lpChatDrawer.addRule(RelativeLayout.ABOVE, 0);
            ivChatSwitch.setVisibility(View.GONE);
        }
        dlChat.setLayoutParams(lpChatDrawer);
    }


    @Override
    public void onBackPressed() {
        // TODO: 17/8/18 横屏返回到竖屏，竖屏走点击关闭逻辑
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRoom != null) {
            mRoom.quitRoom();
        }
    }

    /**
     * 交换ppt和video视图
     */
    private void switchPPTAndVideo() {
        View bigView = flContainerBig.getChildAt(0);
        View smallView = flContainerSmall.getChildAt(0);
        pptFragment.onPause();

        flContainerBig.removeView(bigView);
        flContainerSmall.removeView(smallView);
        flContainerBig.addView(smallView);
        flContainerSmall.addView(bigView);

        pptFragment.onResume();

        View surface;
        if (bigView instanceof BJPlayerView) {
            surface = ((BJPlayerView) bigView).getVideoView().getChildAt(0);
            ((SurfaceView) surface).setZOrderMediaOverlay(true);
            ((SurfaceView) ((FrameLayout) ((RelativeLayout) smallView).getChildAt(0)).getChildAt(0)).setZOrderMediaOverlay(false);
        } else {
            surface = ((BJPlayerView) smallView).getVideoView().getChildAt(0);
            ((SurfaceView) ((FrameLayout) ((RelativeLayout) bigView).getChildAt(0)).getChildAt(0)).setZOrderMediaOverlay(true);
            ((SurfaceView) surface).setZOrderMediaOverlay(false);
        }
    }

    //播放器回调
    private OnPlayerListener onPlayerListener = new OnPlayerListener() {
        @Override
        public void onVideoInfoInitialized(BJPlayerView playerView, long duration, HttpException exception) {

        }

        @Override
        public void onError(BJPlayerView playerView, int code) {

        }

        @Override
        public void onUpdatePosition(BJPlayerView playerView, int position) {

        }

        @Override
        public void onSeekComplete(BJPlayerView playerView, int position) {

        }

        @Override
        public void onSpeedUp(BJPlayerView playerView, float speedUp) {

        }

        @Override
        public void onVideoDefinition(BJPlayerView playerView, int definition) {

        }

        @Override
        public void onPlayCompleted(BJPlayerView playerView, VideoItem item, SectionItem nextSection) {

        }

        @Override
        public void onVideoPrepared(BJPlayerView playerView) {

        }
    };

    @Override
    public void showChoseDefinitionDlg() {

    }

    @Override
    public void showChoseRateDlg() {

    }
}