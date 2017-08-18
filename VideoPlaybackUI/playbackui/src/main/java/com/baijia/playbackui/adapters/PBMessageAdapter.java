package com.baijia.playbackui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijia.playbackui.R;
import com.baijia.playbackui.utils.PBDisplayUtils;
import com.baijia.player.playback.PBRoom;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by wangkangfei on 17/8/17.
 */

public class PBMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MESSAGE_TYPE_TEXT = 0;
    private static final int MESSAGE_TYPE_EMOJI = 1;
    private static final int MESSAGE_TYPE_IMAGE = 2;

    private Context context;
    private PBRoom mRoom;
    private int emojiSize;

    public PBMessageAdapter(Context context, PBRoom room) {
        this.context = context;
        this.mRoom = room;
        emojiSize = (int) (PBDisplayUtils.getScreenDensity(context) * 32);
    }

    @Override
    public int getItemViewType(int position) {
        switch (mRoom.getChatVM().getMessage(position).getMessageType()) {
            case Text:
                return MESSAGE_TYPE_TEXT;
            case Emoji:
            case EmojiWithName:
                return MESSAGE_TYPE_EMOJI;
            case Image:
                return MESSAGE_TYPE_IMAGE;
            default:
                return MESSAGE_TYPE_TEXT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MESSAGE_TYPE_TEXT:
                View textView = LayoutInflater.from(context).inflate(R.layout.item_pb_chat_text, parent, false);
                return new PBTextViewHolder(textView);
            case MESSAGE_TYPE_IMAGE:
                View imgView = LayoutInflater.from(context).inflate(R.layout.item_pb_chat_image, parent, false);
                return new PBImageViewHolder(imgView);
            case MESSAGE_TYPE_EMOJI:
                View emojiView = LayoutInflater.from(context).inflate(R.layout.item_pb_chat_emoji, parent, false);
                return new PBEmojiViewHolder(emojiView);
        }
        View textView = LayoutInflater.from(context).inflate(R.layout.item_pb_chat_text, parent, false);
        return new PBTextViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IMessageModel messageModel = mRoom.getChatVM().getMessage(position);

        int color;
        if (messageModel.getFrom().getType() == LPConstants.LPUserType.Teacher) {
            color = ContextCompat.getColor(context, R.color.pb_live_blue);
        } else {
            color = ContextCompat.getColor(context, R.color.pb_secondary_text);
        }
        String name = messageModel.getFrom().getName() + "：";
        SpannableString spanText = new SpannableString(name);
        spanText.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        if (holder instanceof PBTextViewHolder) {
            PBTextViewHolder textViewHolder = (PBTextViewHolder) holder;
            textViewHolder.textView.setText(spanText);
            textViewHolder.textView.append(messageModel.getContent());
            if (messageModel.getFrom().getType() == LPConstants.LPUserType.Teacher ||
                    messageModel.getFrom().getType() == LPConstants.LPUserType.Assistant) {
                Linkify.addLinks(textViewHolder.textView, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
            } else {
                textViewHolder.textView.setAutoLinkMask(0);
            }
        } else if (holder instanceof PBImageViewHolder) {
            PBImageViewHolder imageViewHolder = (PBImageViewHolder) holder;
            imageViewHolder.ivImg.setOnClickListener(null);
            imageViewHolder.tvName.setText(spanText);
            Picasso.with(context).load(messageModel.getUrl())
                    .into(imageViewHolder.ivImg);
        } else if (holder instanceof PBEmojiViewHolder) {
            PBEmojiViewHolder emojiViewHolder = (PBEmojiViewHolder) holder;
            emojiViewHolder.tvName.setText(spanText);
            Picasso.with(context).load(messageModel.getUrl())
                    .placeholder(R.drawable.pb_ic_exit)
                    .error(R.drawable.pb_ic_exit)
                    .resize(emojiSize, emojiSize)
                    .into(emojiViewHolder.ivEmoji);
        }
    }

    @Override
    public int getItemCount() {
        return mRoom.getChatVM().getMessageCount();
    }

    private static class PBTextViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        PBTextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.pb_item_chat_text);
        }
    }

    private static class PBImageViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvExclamation, tvMask;
        ImageView ivImg;

        PBImageViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.pb_item_chat_image_name);
            ivImg = (ImageView) itemView.findViewById(R.id.pb_item_chat_image);
            tvExclamation = (TextView) itemView.findViewById(R.id.pb_item_chat_image_exclamation);
            tvMask = (TextView) itemView.findViewById(R.id.pb_item_chat_image_mask);
        }
    }

    private static class PBEmojiViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivEmoji;

        PBEmojiViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.pb_item_chat_emoji_name);
            ivEmoji = (ImageView) itemView.findViewById(R.id.pb_item_chat_emoji);
        }
    }
}
