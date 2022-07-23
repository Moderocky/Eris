package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.message.ActionRow;
import mx.kenzie.eris.api.entity.message.UnsentMessage;
import mx.kenzie.eris.api.utility.RequestBuilder;
import mx.kenzie.eris.data.Payload;

public class Message extends UnsentMessage {
    public String channel_id, timestamp, edited_timestamp, webhook_id, application_id;
    public User author;
    public boolean mention_everyone, pinned;
    public User[] mentions;
    public Role[] mention_roles;
    public Channel[] mention_channels;
    public Payload[] reactions, sticker_items;
    public Object nonce;
    public int type;
    public Payload activity, application, interaction;
    public Message referenced_message;
    public Thread thread;
    
    public Message() {
    }
    
    public Message(String content) {
        this.content = content;
    }
    
    public Message(Embed... embeds) {
        this.embeds = embeds;
    }
    
    public Message(ActionRow... rows) {
        this.components = rows;
    }
    
    public Message pin() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.request("PUT", "/channels/" + channel_id + "/pins/" + id, null, null)
            .exceptionally(this::error).thenRun(this::finish);
        return this;
    }
    
    public Message unpin() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.api.request("DELETE", "/channels/" + channel_id + "/pins/" + id, null, null)
            .exceptionally(this::error).thenRun(this::finish);
        return this;
    }
    
    public RequestBuilder<Thread> createThread(String name) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Thread thread = new Thread();
        final RequestBuilder<Thread> builder = new RequestBuilder<>(api, "POST", "/channels/" + channel_id + "/messages/" + id + "/threads", thread);
        builder.set("name", name);
        return builder;
    }
    
    public Message reply(Message message) {
        message.message_reference = new Reference();
        message.message_reference.message_id = id;
//        message.message_reference.channel_id = channel_id;
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.sendMessage(channel_id, message);
    }
    
    public Message edit() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.patch("/channels/" + channel_id + "/messages/" + id, Json.toJson(this, UnsentMessage.class, null), this)
            .exceptionally(this::error).thenAccept(Lazy::finish);
        return this;
    }
    
    public void delete() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.delete("/channels/" + channel_id + "/messages/" + id).thenRun(this::finish);
    }
    
    public Message withFlag(int flag) {
        this.flags |= flag;
        return this;
    }
    
    public Message withoutFlag(int flag) {
        this.flags &= flag;
        return this;
    }
    
    public Message reply(String message) {
        return this.reply(new Message(message));
    }
    
    public boolean isFromSelf() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return (author.equals(api.getSelf()));
    }
    
    public static class Reference extends Payload {
        public boolean fail_if_not_exists;
        public @Optional String message_id, channel_id, guild_id;
    }
    
}
