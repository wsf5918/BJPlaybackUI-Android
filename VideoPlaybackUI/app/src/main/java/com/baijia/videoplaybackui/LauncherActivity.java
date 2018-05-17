package com.baijia.videoplaybackui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.baijiahulian.livecore.context.LPConstants;


public class LauncherActivity extends AppCompatActivity {
    private Button btnEnterRoomOnline;
    private EditText etRoomId, etRoomToken, etSessionId;
    private RadioGroup rgEnv;
    private TextView tvCurEnv;
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
