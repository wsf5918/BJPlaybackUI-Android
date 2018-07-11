package com.baijia.videoplaybackui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baijia.playbackui.PBRoomUI;
import com.baijiahulian.common.permission.AppPermissions;
import com.baijiahulian.livecore.context.LPConstants;

import java.io.File;

import rx.functions.Action1;


public class LauncherActivity extends AppCompatActivity {
    private Button btnEnterRoomOnline;
    private EditText etRoomId, etRoomToken, etSessionId;
    private RadioGroup rgEnv;
    private TextView tvCurEnv;
    private EditText etVideoFile, etSignalFile;
    private LPConstants.LPDeployType deployType = LPConstants.LPDeployType.Product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initView();
        initListener();
    }

    private void initView() {
        btnEnterRoomOnline = (Button) findViewById(R.id.btn_demo_enter_pb_room_online);
        etRoomId = (EditText) findViewById(R.id.et_demo_room_id);
        etRoomToken = (EditText) findViewById(R.id.et_demo_room_token);
        rgEnv = (RadioGroup) findViewById(R.id.rg_demo_env);
        tvCurEnv = (TextView) findViewById(R.id.tv_demo_cur_env);
        etSessionId = (EditText) findViewById(R.id.et_demo_session);
        etVideoFile = findViewById(R.id.et_video_file);
        etSignalFile = findViewById(R.id.et_signal_file);
        getPrevParams();
    }

    private void initListener() {
        btnEnterRoomOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = etRoomId.getText().toString().trim();
                String roomToken = etRoomToken.getText().toString().trim();
                String session = etSessionId.getText().toString().trim();
                if(TextUtils.isEmpty(session)){
                    session = "-1";
                }

                PBRoomUI.enterPBRoom(LauncherActivity.this, roomId, roomToken, session, deployType, new PBRoomUI.OnEnterPBRoomFailedListener() {
                    @Override
                    public void onEnterPBRoomFailed(String msg) {
                        Toast.makeText(LauncherActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                updateParams();
            }
        });
        rgEnv.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.demo_rb_test:
                        deployType = LPConstants.LPDeployType.Test;
                        tvCurEnv.setText("当前环境：test");
                        break;
                    case R.id.demo_rb_beta:
                        deployType = LPConstants.LPDeployType.Beta;
                        tvCurEnv.setText("当前环境：beta");
                        break;
                    case R.id.demo_rb_online:
                        deployType = LPConstants.LPDeployType.Product;
                        tvCurEnv.setText("当前环境：online");
                        break;
                    default:
                        break;
                }
            }
        });

        findViewById(R.id.btn_demo_enter_pb_room_offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPermissions.newPermissions(LauncherActivity.this)
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    String fileStr = etVideoFile.getText().toString().trim();
                                    String signalStr = etSignalFile.getText().toString().trim();
                                    String videoFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bb_video_downloaded/" + fileStr;
                                    String signalFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bb_video_downloaded/" + signalStr;
                                    if (!new File(videoFilePath).exists() || !new File(signalFilePath).exists()) {
                                        Toast.makeText(LauncherActivity.this, "视频或信令文件不存在！请检查", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    PBRoomUI.enterLocalPBRoom(LauncherActivity.this, "123456", videoFilePath, signalFilePath, deployType, null);
                                } else {
                                    Toast.makeText(LauncherActivity.this, "无法操作", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void getPrevParams() {
        SharedPreferences sp = getSharedPreferences("bj_playback_ui_common", Context.MODE_PRIVATE);
        String roomId = sp.getString("bj_playback_room_id", "");
        String roomToken = sp.getString("bj_playback_room_token", "");
        String sessionId = sp.getString("bj_playback_room_session", "");
        etRoomId.setText(roomId);
        etRoomToken.setText(roomToken);
        etSessionId.setText(sessionId);
    }

    private void updateParams() {
        SharedPreferences sp = getSharedPreferences("bj_playback_ui_common", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bj_playback_room_id", etRoomId.getText().toString().trim());
        editor.putString("bj_playback_room_token", etRoomToken.getText().toString().trim());
        editor.putString("bj_playback_room_session", etSessionId.getText().toString().trim());
        editor.apply();
    }
}
