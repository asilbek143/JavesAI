package com.jarves.ai.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {

    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    private String text;
    private int type;
    private String timestamp;

    public ChatMessage(String text, int type) {
        this.text = text;
        this.type = type;
        this.timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    public String getText() { return text; }
    public int getType() { return type; }
    public String getTimestamp() { return timestamp; }
    public boolean isUser() { return type == TYPE_USER; }
}
