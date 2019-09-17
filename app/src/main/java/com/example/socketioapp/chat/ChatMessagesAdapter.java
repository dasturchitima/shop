package com.example.socketioapp.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socketioapp.R;
import com.example.socketioapp.data.ChatMessage;

import java.util.List;

public class ChatMessagesAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> mItems;
    private Context mContext;

    /**
     * Constructor to create a new ChatMessagesAdapter
     *
     * @param items
     * @param context
     */
    public ChatMessagesAdapter(List<ChatMessage> items, Context context) {
        mItems = items;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        if (viewType == ChatMessage.TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_message_received, parent, false);
            viewHolder = new ReceivedMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_message_sent, parent, false);
            viewHolder = new SentMessageViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = mItems.get(position);

        if (chatMessage.getType() == ChatMessage.TYPE_MESSAGE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).tvUsername.setText(chatMessage.getUsername());
            ((ReceivedMessageViewHolder) holder).tvMessage.setText(chatMessage.getMessage());
        } else {
            ((SentMessageViewHolder) holder).tvUsername.setText(chatMessage.getUsername());
            ((SentMessageViewHolder) holder).tvMessage.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    /**
     * Use this method to add new chat message to to the RecyclerView.
     *
     * @param chatMessage
     */
    public void addNewMessage(@NonNull ChatMessage chatMessage) {
        mItems.add(chatMessage);
        notifyItemInserted(mItems.size() - 1);
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvMessage;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvMessage;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

}
