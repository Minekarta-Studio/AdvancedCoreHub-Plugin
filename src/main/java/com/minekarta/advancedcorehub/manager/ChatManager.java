package com.minekarta.advancedcorehub.manager;

public class ChatManager {

    private boolean chatLocked = false;

    public boolean isChatLocked() {
        return chatLocked;
    }

    public void setChatLocked(boolean chatLocked) {
        this.chatLocked = chatLocked;
    }

    public void toggleChatLock() {
        this.chatLocked = !this.chatLocked;
    }
}
