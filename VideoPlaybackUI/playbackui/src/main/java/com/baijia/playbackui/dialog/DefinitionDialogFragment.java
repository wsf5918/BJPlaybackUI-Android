package com.baijia.playbackui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.baijia.playbackui.R;


/**
 * Created by szw on 17/8/20.
 */

public class DefinitionDialogFragment extends DialogFragment implements View.OnClickListener {
    private CreateClickableListener listener;
    private TextView definitionLow;
    private TextView definitionMiddle;
    private TextView definitionHigh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setWindowAnimations(R.style.bottom_menu_animation);
        View view = inflater.inflate(R.layout.fragment_definition_dialog, container);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        definitionLow = (TextView) view.findViewById(R.id.fragment_definition_1_0x);
        definitionMiddle = (TextView) view.findViewById(R.id.fragment_definition_2_0x);
        definitionHigh = (TextView) view.findViewById(R.id.fragment_definition_3_0x);
        definitionLow.setOnClickListener(this);
        definitionMiddle.setOnClickListener(this);
        definitionHigh.setOnClickListener(this);
    }

    public void setCreateClickableListener(CreateClickableListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        //移动弹出菜单到底部
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.50f;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.gravity = Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    @Override
    public void onStop() {
        this.getView().setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_menu_disappear));
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public interface CreateClickableListener {
        void createCommonMeeting();

        void createImmediateMeeting();
    }
}
