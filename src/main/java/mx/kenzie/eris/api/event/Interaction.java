package mx.kenzie.eris.api.event;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.entity.command.callback.Callback;
import mx.kenzie.eris.api.entity.message.InteractionMessage;
import mx.kenzie.eris.api.magic.MessageFlags;
import mx.kenzie.eris.api.utility.SnowflakeMap;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Any;
import mx.kenzie.grammar.Optional;

import java.util.*;

public class Interaction extends Entity implements Event, Replied {

    public int type;
    public String token, id, guild_id, app_permissions, guild_locale, locale, channel_id, target_id;
    protected @Optional Channel channel;
    public Member member;
    public User user;
    public Data data = new Data();
    public transient Map<String, Object> __data;
    private transient Message message0;
    public transient boolean alreadyResponded;

    public Message sendMessage(Message message) {
        final String application = api.getApplicationID();
        this.api.sendMessagePoint("/webhooks/" + application + "/" + token, message, InteractionMessage.class);
        return message;
    }

    public Callback respond(Callback callback) {
        return this.respond(callback, callback.interactionResponseType());
    }

    public Callback respond(Callback callback, int type) {
        final Response response;
        if (callback instanceof Entity entity) entity.api = api;
        if (callback instanceof Message message) response = new MessageResponse(message);
        else response = new GenericResponse(callback);
        response.type = type;
        if (callback instanceof Lazy lazy) lazy.unready();
        this.api.interactionResponse(this, response);
        return callback;
    }

    public void acknowledge(boolean showLoading) {
        if (showLoading) this.respond(() -> 5); // DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE
        else this.respond(() -> 6); // DEFERRED_UPDATE_MESSAGE
    }

    public User getSource() {
        if (user != null && user.id != null) return user;
        if (member != null) return member.user;
        throw new IllegalStateException("No user found");
    }

    public void deleteOriginalResponse() {
        final String application = api.getApplicationID();
        assert api != null;
        this.api.delete("/webhooks/" + application + "/" + token + "/messages/@original");
    }

    public Message getOriginalResponse() {
        final String application = api.getApplicationID();
        final Message message = new Message();
        this.api.get("/webhooks/" + application + "/" + token + "/messages/@original", message)
            .exceptionally(message::error).thenAccept(Lazy::finish);
        return message;
    }

    public Message editOriginalResponse(Message message) {
        final String application = api.getApplicationID();
        message.unready();
        this.api.request("PATCH", "/webhooks/" + application + "/" + token + "/messages/@original",
                Json.toJson(message, InteractionMessage.class, null), message)
            .exceptionally(message::error).thenAccept(Lazy::finish);
        return message;
    }

    @SuppressWarnings("unchecked")
    public synchronized Message getMessage() {
        if (message0 != null) return message0;
        if (!data.resolved.containsKey("messages")) return null;
        final Object object = data.resolved.get("messages");
        if (!(object instanceof Map<?, ?> child)) return null;
        for (final Map.Entry<?, ?> entry : child.entrySet()) {
            final Map<String, Object> value = (Map<String, Object>) entry.getValue();
            final Message message = api.makeEntity(new Message(), value);
            message.id = (String) entry.getKey();
            message.api = api;
            return message0 = message;
        }
        return null;
    }

    @Override
    public Message reply(Message message) {
        this.respond(message);
        return message;
    }

    @Override
    public Message reply(String message) {
        final Message response = new Message(message).withFlag(MessageFlags.EPHEMERAL);
        if (alreadyResponded) this.sendMessage(response);
        else this.reply(response);
        return response;
    }

    public Channel getChannel() {
        if (channel == null) return null;
        if (channel.api == null) channel.api = this.api;
        return channel;
    }

    public class Reader implements Iterator<String> {

        private Option current;
        private Iterator<Option> iterator;

        public Reader() {
            if (data == null || data.options == null) iterator = Collections.emptyIterator();
            else {
                final List<Option> options = new ArrayList<>();
                this.consumeOptions(data, options);
                this.iterator = options.iterator();
            }
        }

        private void consumeOptions(Optioned current, List<Option> options) {
            if (current.options == null) return;
            for (Option option : current.options) {
                options.add(option);
                this.consumeOptions(option, options);
            }
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        public String next() {
            if (iterator.hasNext()) {
                return (current = iterator.next()).name;
            }
            return null;
        }

        public String name() {
            return current != null ? current.name : null;
        }

        @Deprecated
        public <Type> Type value() {
            //noinspection unchecked
            return (Type) current.value;
        }

        public String asString() {
            return (String) current.value;
        }

        public Integer asInteger() {
            return ((Number) current.value).intValue();
        }

        public Double asDouble() {
            return ((Number) current.value).doubleValue();
        }

        public User asUser() {
            return api.getUser(String.valueOf(current.value));
        }

        public Channel asChannel() {
            return api.getChannel(String.valueOf(current.value));
        }

        public Option current() {
            return current;
        }

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

    public static class Option extends Optioned {

        public int type;
        public String name;
        public Object value;
        public boolean focused;

    }

    static class Optioned extends Payload {

        public Option[] options;

    }

    static class GenericResponse extends Response {

        public @Optional
        @Any Callback data;

        private GenericResponse() {
            this(null);
        }

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

        private MessageResponse() {
            this(null);
        }

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

    public class Data extends Optioned {

        public String name, id, guild_id, target_id;
        public Integer type;

        public Row[] components;
        public Map<String, Object> resolved;

        public String custom_id;
        public Integer component_type;
        public Object[] values;
        public transient Map<String, Object> __data;
        private transient Input[] inputs0;

        public boolean isFromMessage() {
            return component_type != null && type == null;
        }

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

        public Input[] getInputs() {
            if (inputs0 != null) return inputs0;
            final List<Input> list = new ArrayList<>();
            for (final Row row : components) Collections.addAll(list, row.components);
            return inputs0 = list.toArray(new Input[0]);
        }

        public String[] getInputValues(String id) {
            final Input[] inputs = this.getInputs();
            for (final Input input : inputs) {
                if (!id.equals(input.custom_id)) continue;
                if (input.values != null) return input.values;
                if (input.value != null) return new String[] {input.value};
                return new String[0];
            }
            return new String[0];
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
                message.id = (String) entry.getKey();
                message.api = api;
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
                user.id = (String) entry.getKey();
                user.api = api;
                map.put(entry.getKey().toString(), user);
            }
            return map;
        }

        // todo members

    }

}
