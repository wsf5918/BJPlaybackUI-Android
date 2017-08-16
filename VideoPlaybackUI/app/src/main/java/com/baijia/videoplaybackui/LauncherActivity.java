package com.baijia.videoplaybackui;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baijia.playbackui.PBRoomUI;
import com.baijia.playbackui.activity.PBRoomActivity;
import com.baijiahulian.livecore.context.LPConstants;

public class LauncherActivity extends AppCompatActivity {
    private Button btnEnterRoomOnline;
    private EditText etRoomId, etRoomToken;
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
    }

    private void initListener() {
        btnEnterRoomOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = etRoomId.getText().toString().trim();
                String roomToken = etRoomToken.getText().toString().trim();
                PBRoomUI.enterPBRoom(LauncherActivity.this, roomId, roomToken, deployType, new PBRoomUI.OnEnterPBRoomFailedListener() {
                    @Override
                    public void onEnterPBRoomFailed(String msg) {
                        Toast.makeText(LauncherActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
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
}
