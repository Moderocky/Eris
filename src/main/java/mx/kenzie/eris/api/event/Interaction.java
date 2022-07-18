package mx.kenzie.eris.api.event;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.entity.command.callback.Callback;
import mx.kenzie.eris.api.entity.message.InteractionMessage;
import mx.kenzie.eris.api.utility.SnowflakeMap;
import mx.kenzie.eris.data.Payload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Interaction extends Entity implements Event {
    
    public int type;
    public String token, id, guild_id, app_permissions, guild_locale, locale, channel_id, target_id;
    public Member member;
    public User user;
    public Data data = new Data();
    
    public Callback respond(Callback callback) {
        return this.respond(callback, callback.interactionResponseType());
    }
    
    public Callback respond(Callback callback, int type) {
        final Response response;
        if (callback instanceof Message message) response = new MessageResponse(message);
        else response = new GenericResponse(callback);
        response.type = type;
        if (callback instanceof Lazy lazy) lazy.unready();
        this.api.interactionResponse(this, response);
        return callback;
    }
    
    public Message getOriginalMessage() {
        final String application = api.getApplicationID();
        final Message message = new Message();
        this.api.get("/webhooks/" + application + "/" + token + "/messages/@original", message)
            .thenAccept(Lazy::finish);
        return message;
    }
    
    public static class Input extends Payload {
        public int type;
        public String custom_id, value;
        public String[] values;
    }
    
    public static class Row extends Payload {
        public int type = 1;
        public Input[] components;
    }
    
    public class Data extends Payload {
        public String name, id, custom_id;
        public Option[] options;
        
        public Row[] components;
        public Map<String, Object> resolved;
        
        private transient Input[] inputs0;
        
        public String getInputValue(String id) {
            final Input[] inputs = this.getInputs();
            for (final Input input : inputs) {
                if (!id.equals(input.custom_id)) continue;
                if (input.value != null) return input.value;
                if (input.values != null && input.values.length > 0) return input.values[0];
                return null;
            }
            return null;
        }
        
        public String[] getInputValues(String id) {
            final Input[] inputs = this.getInputs();
            for (final Input input : inputs) {
                if (!id.equals(input.custom_id)) continue;
                if (input.values != null) return input.values;
                if (input.value != null) return new String[]{input.value};
                return new String[0];
            }
            return new String[0];
        }
        
        public Input[] getInputs() {
            if (inputs0 != null) return inputs0;
            final List<Input> list = new ArrayList<>();
            for (final Row row : components) Collections.addAll(list, row.components);
            return inputs0 = list.toArray(new Input[0]);
        }
        
        @SuppressWarnings("unchecked")
        public SnowflakeMap<Message> getMessages() {
            final SnowflakeMap<Message> map = new SnowflakeMap<>();
            if (!resolved.containsKey("messages")) return map;
            final Object object = resolved.get("messages");
            if (!(object instanceof Map<?, ?> child)) return map;
            for (final Map.Entry<?, ?> entry : child.entrySet()) {
                final Map<String, Object> value = (Map<String, Object>) entry.getValue();
                final Message message = api.makeEntity(new Message(), value);
                map.put(entry.getKey().toString(), message);
            }
            return map;
        }
        
        @SuppressWarnings("unchecked")
        public SnowflakeMap<User> getUsers() {
            final SnowflakeMap<User> map = new SnowflakeMap<>();
            if (!resolved.containsKey("users")) return map;
            final Object object = resolved.get("users");
            if (!(object instanceof Map<?, ?> child)) return map;
            for (final Map.Entry<?, ?> entry : child.entrySet()) {
                final Map<String, Object> value = (Map<String, Object>) entry.getValue();
                final User user = api.makeEntity(new User(), value);
                map.put(entry.getKey().toString(), user);
            }
            return map;
        }
        
        // todo members
        
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
        @Any Callback data;
        
        public GenericResponse(Callback callback) {
            this.data = callback;
        }
        
        @Override
        public Object data() {
            return data;
        }
    }
    
    static class MessageResponse extends Response {
        public InteractionMessage data;
        
        public MessageResponse(Message message) {
            this.data = message;
        }
        
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