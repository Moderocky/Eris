package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.data.Payload;

public class Message extends UnsentMessage {
    public Message() {}
    
    public Message(String content) {
        this.content = content;
    }
    
    public static class Reference extends Payload {
        public boolean fail_if_not_exists;
        public String message_id, channel_id, guild_id;
    }
}

