package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.guild.CreateChannel;
import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.api.utility.BulkEntity;
import mx.kenzie.eris.api.utility.DeferredList;
import mx.kenzie.eris.api.utility.LazyList;
import mx.kenzie.eris.api.utility.MagicQueue;
import mx.kenzie.eris.data.Payload;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

public class Channel extends CreateChannel {
    
    public int flags;
    public @Optional String guild_id, last_message_id, owner_id, application_id, last_pin_timestamp, permissions;
    public @Optional User[] recipients;
    
    public Channel() {}
    public Channel(String name, int type) {
        this.name = name;
        this.type = type;
    }
    
    public <IMessage> Message getMessage(IMessage id) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Message message;
        if (id instanceof Message m) message = m;
        else message = new Message();
        this.api.get("/channels/" + this + "/messages/" + id, message)
            .exceptionally(message::error).thenAccept(Lazy::finish);
        return message;
    }
    
    public <IMessage> void deleteMessage(IMessage id) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        if (id instanceof Message m) m.delete();
        else this.api.delete("/channels/" + this + "/messages/" + id);
    }
    
    public void deleteMessages(Object[] messages) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Set<String> ids = new HashSet<>();
        for (final Object message : messages) {
            if (message == null) continue;
            if (message instanceof Message m) ids.add(m.id);
            else ids.add(message.toString());
        }
        class Body extends Payload { final String[] messages = ids.toArray(new String[0]); }
        this.api.post("/channels/" + this + "/messages/bulk-delete", new Body().toJson(null), null);
    }
    
    public Channel modify() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.patch("/channels/" + this , Json.toJson(this, CreateChannel.class, null), this)
            .exceptionally(this::error).thenAccept(Lazy::finish);
        return this;
    }
    
    public void delete() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.delete("/channels/" + this).exceptionally(this::error0).thenRun(this::finish);
    }
    
    public boolean isText() {
        if (name == null) this.await();
        return switch (type) {
            case ChannelType.DM, ChannelType.GROUP_DM, ChannelType.GUILD_TEXT, ChannelType.GUILD_NEWS, ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_PRIVATE_THREAD ->
                true;
            default -> false;
        };
    }
    
    public Message send(Message message) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.sendMessage(this, message);
    }
    
    public class ResultMessages extends BulkEntity<Message> implements Iterable<Message> {
        transient int limit = 50;
        transient String around, before, after;
    
        public ResultMessages limit(int limit) {
            this.limit = limit;
            return this;
        }
    
        public ResultMessages around(String around) {
            this.around = around;
            return this;
        }
    
        public ResultMessages before(String before) {
            this.before = before;
            return this;
        }
    
        public ResultMessages after(String after) {
            this.after = after;
            return this;
        }
    
        @Override
        protected int limit() {
            return this.limit;
        }
    
        @Override
        protected DiscordAPI api() {
            return api;
        }
    
        @Override
        protected Class<Message> getType() {
            return Message.class;
        }
    
        @Override
        protected CompletableFuture<List<?>> getEntities(List<?> list) {
            if (api == null) throw DiscordAPI.unlinkedEntity(Channel.this);
            final Map<String, Object> query = new HashMap<>();
            if (limit > 0) query.put("limit", limit);
            if (around != null) query.put("around", around);
            else if (before != null) query.put("before", before);
            else if (after != null) query.put("after", after);
            return Channel.this.api.get("/channels/" + id + "/messages", query, list);
        }
    }
    
    public ResultMessages getMessages() {
        return new ResultMessages();
    }

}
