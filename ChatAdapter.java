package com.jarves.ai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jarves.ai.R;
import com.jarves.ai.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == ChatMessage.TYPE_USER
                ? R.layout.item_message_user
                : R.layout.item_message_bot;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.messageText.setText(message.getText());
        holder.timeText.setText(message.getTimestamp());

        // Animatsiya
        holder.itemView.setAnimation(
                AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in)
        );
    }

    @Override
    public int getItemCount() { return messages.size(); }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;

        ViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.messageText);
            timeText = view.findViewById(R.id.timeText);
        }
    }
}
