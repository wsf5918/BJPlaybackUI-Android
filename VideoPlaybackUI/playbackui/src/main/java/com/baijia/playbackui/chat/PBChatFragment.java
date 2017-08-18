package com.baijia.playbackui.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baijia.playbackui.R;
import com.baijia.playbackui.adapters.PBMessageAdapter;
import com.baijia.player.playback.PBRoom;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by wangkangfei on 17/8/17.
 */

public class PBChatFragment extends Fragment implements PBChatContract.View {
    //view
    private RecyclerView rvChat;

    //data
    private PBRoom mRoom;

    //adapter
    private PBMessageAdapter messageAdapter;

    public void setRoom(PBRoom room) {
        this.mRoom = room;
        mRoom.getChatVM();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pb_chat, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        messageAdapter = new PBMessageAdapter(getContext(), mRoom);
        rvChat = (RecyclerView) view.findViewById(R.id.rv_pb_fragment_chat);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(messageAdapter);

        mRoom.getChatVM().getObservableOfNotifyDataChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        messageAdapter.notifyDataSetChanged();
                        rvChat.scrollToPosition(messageAdapter.getItemCount());
                    }
                });
    }

    @Override
    public void setPresenter(PBChatContract.Presenter presenter) {

    }
}
