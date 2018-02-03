package com.baijia.playbackui.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baijia.playbackui.R;
import com.baijia.playbackui.adapters.DefinitionAdapter;
import com.baijia.playbackui.base.PBBasePresenter;
import com.baijia.playbackui.base.PBBaseView;
import com.baijia.playbackui.chat.PBChatFragment;
import com.baijia.playbackui.chat.PBChatPresenter;
import com.baijia.playbackui.progressbar.PBRoomProgressPresenter;
import com.baijia.playbackui.utils.ConstantUtil;
import com.baijia.playbackui.utils.PBDisplayUtils;
import com.baijia.playbackui.viewsupport.AutoExitDrawerLayout;
import com.baijia.playbackui.viewsupport.DragTextView;
import com.baijia.player.playback.LivePlaybackSDK;
import com.baijia.player.playback.PBRoom;
import com.baijia.player.playback.mocklive.OnPlayerListener;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.BJCenterViewPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;


import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class PBRoomActivity extends PBBaseActivity implements LPLaunchListener, PBRouterListener {
    //view
    private MaterialDialog launchStepDlg;
    private BJPlayerView mPlayerView;
    private FrameLayout flContainerProgress, flContainerBig;
    private DragTextView flContainerSmall;
    private ImageView ivQuitRoom, ivChatSwitch;
    private PBRoomProgressPresenter progressPresenter;
    private AutoExitDrawerLayout dlChat;
    private FrameLayout flAreaSwitch;
    private LinearLayout rateView;
    private RelativeLayout nameMask;
    private RecyclerView definitionContainer;
    private RelativeLayout definitionRl;
    private TextView rateLow;
    private TextView rateMiddle;
    private TextView rateHigh;
    private TextView markNameTv;
    //fragment
    private PBChatFragment chatFragment;
    private LPPPTFragment pptFragment;

    //data
    private PBRoom mRoom;
    private String roomId, roomToken, sessionId, videoFilePath, signalFilePath;
    private int deployType;
    private boolean isSmallView = true;//用来判断当前视频view是否在smallView上
    private boolean isOrientation = true;//用来判断当前是横屏还是竖屏(初始话为竖屏)
    private List<VideoItem.DefinitionItem> definitionItems = new ArrayList<>();
    private DefinitionAdapter definitionAdapter;
    private int selectPositon = -1;

    private boolean videoLunchSuccess = false;
    private static final String CHAT_FRAGMENT_TAG = "CHAT_FRAGMENT_TAG";
    private static final String PPT_FRAGMENT_TAG = "PPT_FRAGMENT_TAG";
    private boolean isVideoInfoInitialized = false;

    private ImageView smallPlaceHolder;
    private ImageView bigPlaceHolder;
    private boolean isVideoOn;

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
        bigPlaceHolder = (ImageView) findViewById(R.id.pb_player_big_placeholder);
        flContainerSmall = (DragTextView) findViewById(R.id.fl_pb_container_small);
        smallPlaceHolder = (ImageView) findViewById(R.id.pb_player_small_placeholder);
        ivChatSwitch = (ImageView) findViewById(R.id.iv_pb_chat_switch);
        rateView = (LinearLayout) findViewById(R.id.fl_pb_container_rate);
        rateLow = (TextView) findViewById(R.id.fragment_definition_1_0x);
        rateMiddle = (TextView) findViewById(R.id.fragment_definition_2_0x);
        rateHigh = (TextView) findViewById(R.id.fragment_definition_3_0x);
        definitionRl = (RelativeLayout) findViewById(R.id.fl_pb_container_definition_rl);
        nameMask = (RelativeLayout) findViewById(R.id.rl_pb_name_mask);
        markNameTv = (TextView) findViewById(R.id.pb_name_mask_tv);
        definitionContainer = (RecyclerView) findViewById(R.id.fl_pb_container_definition);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        definitionContainer.setLayoutManager(linearLayoutManager);

        Picasso.with(this)
                .load(ConstantUtil.AUDIO_ON_PICTURE)
                .fit()
                .error(R.drawable.ic_video_back_zhanwei)
                .into(smallPlaceHolder);

        Picasso.with(this)
                .load(ConstantUtil.AUDIO_ON_PICTURE)
                .fit()
                .error(R.color.lp_ppt_white)
                .into(bigPlaceHolder);


        dlChat.openDrawer(Gravity.START);
        dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        dlChat.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ivChatSwitch.setImageResource(R.drawable.ic_video_back_sentmsg_no);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                ivChatSwitch.setImageResource(R.drawable.ic_video_back_sentmsg_no_on);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        initSmallLayoutParams();
        //修复某些机器上surfaceView导致的闪黑屏的bug
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    private void initSmallLayoutParams() {
        Configuration configuration = getResources().getConfiguration();
        doOnSmallContainerConfigurationChanged(configuration);
        doOnBigContainerConfigurationChanged(configuration);
        doOnChatDrawerConfigurationChanged(configuration);
    }

    private void setSurfaceZOrderMediaOverlay(View view, boolean isZOrder) {
        if (view instanceof SurfaceView) {
            ((SurfaceView) view).setZOrderMediaOverlay(isZOrder);
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setSurfaceZOrderMediaOverlay(((ViewGroup) view).getChildAt(i), isZOrder);
            }
        }
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
                flContainerSmall.setVisibility(View.VISIBLE);
                if (isSmallView) {
                    flContainerSmall.addView(mPlayerView, 0);
                    setSurfaceZOrderMediaOverlay(mPlayerView, true);
                } else {
                    pptFragment.onStart();
                    setSurfaceZOrderMediaOverlay(pptFragment.getView(), true);
                }
                flAreaSwitch.setVisibility(View.GONE);
            }
        });
        ivChatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/8/18 横屏状态打开和关闭聊天fragment
                if (dlChat.isDrawerOpen(GravityCompat.START)) {
                    dlChat.closeDrawers();
                    ivChatSwitch.setImageResource(R.drawable.ic_video_back_sentmsg_no_on);
                } else {
                    dlChat.openDrawer(Gravity.LEFT);
                    ivChatSwitch.setImageResource(R.drawable.ic_video_back_sentmsg_no);
                }
            }
        });
        flContainerSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoLunchSuccess) {
                    return;
                }
                List<String> options = new ArrayList<>();
                options.add(getString(R.string.full_screen));
                options.add(getString(R.string.close));
                options.add(getString(R.string.cancel));

                new MaterialDialog.Builder(PBRoomActivity.this)
                        .items(options)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                if (i == 0) {
                                    switchPPTAndVideo();
                                } else if (i == 1) {
                                    flContainerSmall.setVisibility(View.INVISIBLE);
                                    if (isSmallView) {
                                        if (flContainerSmall.getChildAt(0) != null) {
                                            flContainerSmall.removeViewAt(0);
                                        }
                                        flAreaSwitch.setBackgroundResource(R.drawable.ic_video_back_stopvideo);
                                        flAreaSwitch.setVisibility(View.VISIBLE);
                                    } else {
                                        pptFragment.onStop();
                                        flAreaSwitch.setBackgroundResource(R.drawable.ic_video_back_ppt);
                                        flAreaSwitch.setVisibility(View.VISIBLE);
                                    }
                                } else {

                                }
                                materialDialog.dismiss();
                            }
                        })
                        .show();
            }
        });


        /**
         * 清晰度和倍速切换
         */
        rateLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateMiddle.setBackgroundResource(0);
                rateHigh.setBackgroundResource(0);
                rateLow.setBackgroundResource(R.drawable.shape_definition_bg);
                rateLow.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_blue));
                rateMiddle.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_white));
                rateHigh.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_white));
                flContainerProgress.setVisibility(View.VISIBLE);
                rateView.setVisibility(View.GONE);
                mPlayerView.setVideoRate(BJPlayerView.VIDEO_RATE_1_1_X);
                setRate(rateLow.getText().toString());
            }
        });
        rateMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateLow.setBackgroundResource(0);
                rateHigh.setBackgroundResource(0);
                rateMiddle.setBackgroundResource(R.drawable.shape_definition_bg);
                rateMiddle.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_blue));
                rateLow.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_white));
                rateHigh.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_white));
                flContainerProgress.setVisibility(View.VISIBLE);
                rateView.setVisibility(View.GONE);
                mPlayerView.setVideoRate(BJPlayerView.VIDEO_RATE_1_5_X);
                setRate(rateMiddle.getText().toString());
            }
        });
        rateHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateLow.setBackgroundResource(0);
                rateMiddle.setBackgroundResource(0);
                rateHigh.setBackgroundResource(R.drawable.shape_definition_bg);
                rateHigh.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_blue));
                rateMiddle.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_white));
                rateLow.setTextColor(ContextCompat.getColor(PBRoomActivity.this, R.color.pb_live_white));
                flContainerProgress.setVisibility(View.VISIBLE);
                rateView.setVisibility(View.GONE);
                mPlayerView.setVideoRate(BJPlayerView.VIDEO_RATE_2_X);
                setRate(rateHigh.getText().toString());
            }
        });

        flContainerBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flContainerProgress.setVisibility(flContainerProgress.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }

    public void setRate(String rate) {
        progressPresenter.setRate(rate);
    }

    public void setDefinition(String definition) {
        progressPresenter.setDefinition(definition);
    }

    private void initData() {
        if (getIntent().getStringExtra(ConstantUtil.PB_ROOM_SESSION_ID) == null) {
            roomId = getIntent().getStringExtra(ConstantUtil.PB_ROOM_ID);
            videoFilePath = getIntent().getStringExtra(ConstantUtil.PB_ROOM_VIDEOFILE_PATH);
            signalFilePath = getIntent().getStringExtra(ConstantUtil.PB_ROOM_SIGNALFILE_PATH);
            deployType = getIntent().getIntExtra(ConstantUtil.PB_ROOM_DEPLOY, 2);

            //进入离线回放教室
            doEnterRoom(true);

        } else {
            roomId = getIntent().getStringExtra(ConstantUtil.PB_ROOM_ID);
            roomToken = getIntent().getStringExtra(ConstantUtil.PB_ROOM_TOKEN);
            sessionId = getIntent().getStringExtra(ConstantUtil.PB_ROOM_SESSION_ID);
            deployType = getIntent().getIntExtra(ConstantUtil.PB_ROOM_DEPLOY, 2);

            //进入在线回放教室
            doEnterRoom(false);
        }
    }

    private void initLaunchStepDlg() {
        launchStepDlg = new MaterialDialog.Builder(this)
                .content("正在加载...")
                .progress(true, 100, false)
                .cancelable(true)
                .build();
    }

    /**
     * 进入房间
     *
     * @param isOfflineRoom 是否是离线播放  false:在线播放  true:本地播放
     */
    private void doEnterRoom(boolean isOfflineRoom) {
        View view = LayoutInflater.from(this).inflate(R.layout.pb_player_controller_view, null, false);
        progressPresenter = new PBRoomProgressPresenter(view, mPlayerView);
        progressPresenter.setRouterListener(this);

        mPlayerView.setTopPresenter(progressPresenter);
        mPlayerView.setBottomPresenter(progressPresenter);
        BJCenterViewPresenter bjCenterViewPresenter = new BJCenterViewPresenter(mPlayerView.getCenterView());
        bjCenterViewPresenter.setRightMenuHidden(true);
        mPlayerView.setCenterPresenter(bjCenterViewPresenter);
        mPlayerView.setGestureEnable(false);
//        mPlayerView.setForbidConfiguration(true);

        flContainerProgress.addView(view);
        //enter room action
        switch (deployType) {
            case 0:
                LivePlaybackSDK.deployType = LPConstants.LPDeployType.Test;
                break;
            case 1:
                LivePlaybackSDK.deployType = LPConstants.LPDeployType.Beta;
                break;
            case 2:
                LivePlaybackSDK.deployType = LPConstants.LPDeployType.Product;
                break;
            default:
                break;
        }

        if (isOfflineRoom) {
            mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(roomId), videoFilePath, signalFilePath);
        } else {
            mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(roomId), Long.parseLong(sessionId), roomToken);
        }
        mRoom.bindPlayerView(mPlayerView);
        mRoom.setOnPlayerListener(onPlayerListener);
        mRoom.getObservableOfVideoStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        isVideoOn = aBoolean;
                        if (isSmallView) {
                            if (!aBoolean) {
                                progressPresenter.forbidDefinitionChange();
                                smallPlaceHolder.setVisibility(View.VISIBLE);
                                nameMask.setVisibility(View.INVISIBLE);
                            } else {
                                progressPresenter.openDefinitionChange();
                                smallPlaceHolder.setVisibility(View.GONE);
                                nameMask.setVisibility(View.VISIBLE);
                            }
                        }
                        if (!isSmallView) {
                            if (!aBoolean) {
                                progressPresenter.forbidDefinitionChange();
                                bigPlaceHolder.setVisibility(View.VISIBLE);
                                nameMask.setVisibility(View.INVISIBLE);
                            } else {
                                progressPresenter.openDefinitionChange();
                                bigPlaceHolder.setVisibility(View.GONE);
                                nameMask.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
        launchStepDlg.show();
        if (mRoom.isPlayBackOffline()) {
            progressPresenter.setDefinitionVisible(false);
        }
        addFragment();
        mRoom.enterRoom(this);
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
        videoLunchSuccess = true;
        launchSuccess();
    }


    private void addFragment() {
        chatFragment = new PBChatFragment();
        chatFragment.setRoom(mRoom);
        bindVP(chatFragment, new PBChatPresenter(chatFragment));
        addFragment(R.id.fl_pb_chat_content_container, chatFragment, false, CHAT_FRAGMENT_TAG);

        pptFragment = new LPPPTFragment();
        pptFragment.setAnimPPTEnable(false);
        pptFragment.setLiveRoom(mRoom);
        pptFragment.setFlingEnable(false);
        //设置白板不消费事件
        pptFragment.changePPTTouchAble(false);
        addFragment(R.id.ppt_container, pptFragment, false, PPT_FRAGMENT_TAG);
    }

    /**
     * 进入房间成功
     */
    private void launchSuccess() {
        if (mPlayerView != null) {
            if (mPlayerView.isPlaying()) {
                mPlayerView.pauseVideo();
            } else {
                mPlayerView.playVideo();
            }
        }
        markNameTv.setText(mRoom.getTeacherUser().getName());
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
        flContainerSmall.configurationChanged();
        isOrientation = newConfig.orientation == ORIENTATION_PORTRAIT;
        progressPresenter.onOrientationChanged(isOrientation);
        if (videoLunchSuccess) {
            if (chatFragment == null) {
                chatFragment = (PBChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
            }
            if (pptFragment == null) {
                pptFragment = (LPPPTFragment) getSupportFragmentManager().findFragmentByTag(PPT_FRAGMENT_TAG);
            }
            if (chatFragment != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    chatFragment.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
                } else {
                    chatFragment.setOrientation(ORIENTATION_PORTRAIT);
                }
            }
        }
    }

    /**
     * 上方big container
     */
    private void doOnBigContainerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lpBigContainer = (RelativeLayout.LayoutParams) flContainerBig.getLayoutParams();
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
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
        RelativeLayout.LayoutParams lpSmallContainer = new RelativeLayout.LayoutParams(PBDisplayUtils.dip2px(this, 150), PBDisplayUtils.dip2px(this, 90));
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lpSmallContainer.addRule(RelativeLayout.BELOW, R.id.view_pb_anchor_left_more_top);
        } else {
            lpSmallContainer.width = PBDisplayUtils.dip2px(this, 150);
            lpSmallContainer.height = PBDisplayUtils.dip2px(this, 90);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            lpSmallContainer.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpSmallContainer.addRule(RelativeLayout.BELOW, R.id.fl_pb_container_big);
            flContainerSmall.setLayoutParams(lpSmallContainer);
        }
        flContainerSmall.setLayoutParams(lpSmallContainer);
    }

    /**
     * 聊天drawer
     */
    private void doOnChatDrawerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lpChatDrawer = (RelativeLayout.LayoutParams) dlChat.getLayoutParams();
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            lpChatDrawer.width = PBDisplayUtils.dip2px(this, 300);
            lpChatDrawer.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (flContainerSmall.getVisibility() == View.VISIBLE) {
                lpChatDrawer.addRule(RelativeLayout.BELOW, R.id.view_pb_anchor_left_top);
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
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            definitionContainer.setLayoutManager(manager);
            definitionContainer.setAdapter(definitionAdapter);
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
        isSmallView = !isSmallView;
        View bigView = flContainerBig.getChildAt(0);
        View smallView = flContainerSmall.getChildAt(0);
        if (bigView == null || smallView == null) {
            return;
        }
        pptFragment.onStop();

        flContainerBig.removeView(bigView);
        flContainerSmall.removeView(smallView);
        flContainerBig.addView(smallView, 0);
        flContainerSmall.addView(bigView, 0);

        pptFragment.onStart();

        if (isSmallView) {
            if (mPlayerView.isPlaying() && isVideoOn) {
                smallPlaceHolder.setVisibility(View.GONE);
            }
            if (!isVideoOn || !mPlayerView.isPlaying()) {
                smallPlaceHolder.setVisibility(View.VISIBLE);
            }
            bigPlaceHolder.setVisibility(View.GONE);
            nameMask.setVisibility(smallPlaceHolder.getVisibility() == View.GONE ? View.VISIBLE : View.INVISIBLE);
        } else {
            if (mPlayerView.isPlaying() && isVideoOn) {
                bigPlaceHolder.setVisibility(View.GONE);
            }
            if (!isVideoOn || !mPlayerView.isPlaying()) {
                bigPlaceHolder.setVisibility(View.VISIBLE);
            }
            smallPlaceHolder.setVisibility(View.GONE);
            nameMask.setVisibility(View.INVISIBLE);
        }

        if (bigView instanceof BJPlayerView) {
            flAreaSwitch.setBackgroundResource(R.drawable.ic_video_back_stopvideo);
        } else {
            flAreaSwitch.setBackgroundResource(R.drawable.ic_video_back_ppt);
        }
        setSurfaceZOrderMediaOverlay(bigView, true);
        setSurfaceZOrderMediaOverlay(smallView, false);
        updateWaterMark();
    }


    //播放器回调
    private OnPlayerListener onPlayerListener = new OnPlayerListener() {
        @Override
        public void onVideoInfoInitialized(BJPlayerView playerView, long duration, HttpException exception) {
            definitionItems = mPlayerView.getVideoItem().definition;
            definitionAdapter = new DefinitionAdapter(PBRoomActivity.this, definitionItems);
            definitionAdapter.setRouterListener(PBRoomActivity.this);
            definitionContainer.setAdapter(definitionAdapter);
            //隐藏loading
            mPlayerView.getCenterView().setVisibility(View.GONE);

            updateWaterMark();
            isVideoInfoInitialized = true;

            //默认设置最高清晰度(只针对在线视频)
            if (!mRoom.isPlayBackOffline() && selectPositon == -1 && definitionItems != null && definitionItems.size() > 0) {
                int position = definitionItems.size() - 1;
                VideoItem.DefinitionItem definitionItem = definitionItems.get(position);
                selectDefinition(definitionItem.type, position);
            }
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
            if (isSmallView) {
                nameMask.setVisibility(View.INVISIBLE);
                smallPlaceHolder.setVisibility(View.VISIBLE);
            } else {
                bigPlaceHolder.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onVideoPrepared(BJPlayerView playerView) {

        }

        @Override
        public void onPause(BJPlayerView playerView) {
        }

        @Override
        public void onPlay(BJPlayerView playerView) {
            if(isVideoOn){
                if(isSmallView){
                    smallPlaceHolder.setVisibility(View.GONE);
                } else{
                    bigPlaceHolder.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public void showChoseDefinitionDlg() {
        if (isVideoInfoInitialized) {
            definitionAdapter.setSelectPosition(selectPositon);
            definitionRl.setVisibility(View.VISIBLE);
            //definitionView.setVisibility(View.VISIBLE);
            flContainerProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showChoseRateDlg() {
        rateView.setVisibility(View.VISIBLE);
        flContainerProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void changeZhanweiAndVideo() {

    }

    @Override
    public boolean changeOrientation() {
        if (isOrientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            rateView.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            definitionContainer.setLayoutManager(manager);
            definitionContainer.setAdapter(definitionAdapter);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            rateView.setOrientation(LinearLayout.VERTICAL);
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            definitionContainer.setLayoutManager(manager);
            definitionContainer.setAdapter(definitionAdapter);
        }

        return isOrientation;
    }

    @Override
    public void selectDefinition(String type, int position) {
        this.selectPositon = position;
        definitionAdapter.notifyDataSetChanged();
        switch (type) {
            case "low":
                mPlayerView.setVideoDefinition(BJPlayerView.VIDEO_DEFINITION_STD);
                progressPresenter.setDefinition("标清");
                break;
            case "high":
                mPlayerView.setVideoDefinition(BJPlayerView.VIDEO_DEFINITION_HIGH);
                progressPresenter.setDefinition("高清");
                break;
            case "superHD":
                mPlayerView.setVideoDefinition(BJPlayerView.VIDEO_DEFINITION_SUPER);
                progressPresenter.setDefinition("超清");
                break;
            case "720p":
                mPlayerView.setVideoDefinition(BJPlayerView.VIDEO_DEFINITION_720p);
                progressPresenter.setDefinition("720P");
                break;
            case "1080p":
                mPlayerView.setVideoDefinition(BJPlayerView.VIDEO_DEFINITION_1080p);
                progressPresenter.setDefinition("1080P");
                break;


        }
        flContainerProgress.setVisibility(View.VISIBLE);
        definitionRl.setVisibility(View.GONE);
    }

    private void updateWaterMark() {
        //移除水印
        View waterMark = mPlayerView.findViewById(R.id.water_mark_iv_id);
        if (waterMark != null) {
            mPlayerView.removeView(waterMark);
        }
        //重新添加
        if (mPlayerView.getVideoItem() != null) {
            mPlayerView.showWaterMark(mPlayerView.getVideoItem().waterMark);
        }
    }
}