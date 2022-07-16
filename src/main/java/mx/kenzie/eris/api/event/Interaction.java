package mx.kenzie.eris.api.event;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.entity.message.InteractionMessage;
import mx.kenzie.eris.data.Payload;

public class Interaction extends Entity implements Event {
    
    public int type;
    public String token, id, guild_id, app_permissions, guild_locale, locale, channel_id, target_id;
    public Member member;
    public User user;
    public Data data;
    
    public Message respond(Message message) {
        final MessageResponse response = new MessageResponse();
        response.type = 4;
        response.data = message;
        message.unready();
        this.api.interactionResponse(this, response);
        return message;
    }
    
    public static class Data extends Payload {
        public String name, id;
        public Option[] options;
        public Payload resolved;
    }
    
    public static class Option extends Payload {
        public int type;
        public String name;
        public Object value;
        public Option[] options;
        public boolean focused;
    }
    
    
    static class GenericResponse extends Response {
        public @Optional
        @Any Payload data;
        
        @Override
        public Object data() {
            return data;
        }
    }
    
    static class MessageResponse extends Response {
        public InteractionMessage data;
        
        @Override
        public Object data() {
            return data;
        }
    }
    
    public static abstract class Response extends Payload {
        public int type;
        
        public abstract Object data();
    }
    
}
