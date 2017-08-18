package com.baijia.playbackui.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baijia.playbackui.R;
import com.baijia.playbackui.base.PBBasePresenter;
import com.baijia.playbackui.base.PBBaseView;
import com.baijia.playbackui.chat.PBChatFragment;
import com.baijia.playbackui.chat.PBChatPresenter;
import com.baijia.playbackui.progressbar.PBRoomProgressPresenter;
import com.baijia.playbackui.utils.ConstantUtil;
import com.baijia.playbackui.viewsupport.AutoExitDrawerLayout;
import com.baijia.player.playback.LivePlaybackSDK;
import com.baijia.player.playback.PBRoom;
import com.baijia.player.playback.mocklive.OnPlayerListener;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;

public class PBRoomActivity extends PBBaseActivity implements LPLaunchListener, PBRouterListener {
    //view
    private MaterialDialog launchStepDlg;
    private BJPlayerView mPlayerView;
    private RelativeLayout rlContainerBig, rlContainerProgress;
    private ImageView ivQuitRoom;
    private PBRoomProgressPresenter progressPresenter;
    private FrameLayout dragContainerBig;
    private AutoExitDrawerLayout dlChat;
    private FrameLayout flContainerChat;

    //fragment
    private PBChatFragment chatFragment;

    //data
    private PBRoom mRoom;
    private String roomId, roomToken;
    private int deployType;

    //listener
    private OnPlayerListener onPlayerListener;

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
        rlContainerBig = (RelativeLayout) findViewById(R.id.rl_pb_container_big);
        ivQuitRoom = (ImageView) findViewById(R.id.iv_pb_exit);
        dragContainerBig = (FrameLayout) findViewById(R.id.dfl_pb_container_freedom_big);
        mPlayerView = (BJPlayerView) findViewById(R.id.pb_pv_main);
        rlContainerProgress = (RelativeLayout) findViewById(R.id.rl_pb_container_progress);
        dlChat = (AutoExitDrawerLayout) findViewById(R.id.dl_pb_chat);
        dlChat.openDrawer(Gravity.START);
    }

    private void initListeners() {
        ivQuitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerView.playVideo();
            }
        });
        onPlayerListener = new OnPlayerListener() {
            @Override
            public void onVideoInfoInitialized(BJPlayerView bjPlayerView, long l, HttpException e) {

            }

            @Override
            public void onError(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onSeekComplete(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onSpeedUp(BJPlayerView bjPlayerView, float v) {

            }

            @Override
            public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem sectionItem) {

            }

            @Override
            public void onVideoPrepared(BJPlayerView bjPlayerView) {

            }
        };
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
        //init player view
        progressPresenter = new PBRoomProgressPresenter(view, mPlayerView);
        mPlayerView.setTopPresenter(progressPresenter);
        mPlayerView.setBottomPresenter(progressPresenter);
        mPlayerView.setCenterPresenter(progressPresenter);
        mPlayerView.enableBrightnessGesture(false);
        mPlayerView.enableSeekGesture(false);
        mPlayerView.enableVolumeGesture(false);
        rlContainerProgress.addView(view);
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
    }

    private <V extends PBBaseView, P extends PBBasePresenter> void bindVP(V view, P presenter) {
        presenter.setRouter(this);
        view.setPresenter(presenter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRoom != null) {
            mRoom.quitRoom();
        }
    }
}
