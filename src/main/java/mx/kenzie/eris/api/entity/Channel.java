package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.guild.CreateChannel;
import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.api.utility.BulkEntity;
import mx.kenzie.eris.api.utility.RequestBuilder;
import mx.kenzie.eris.data.Payload;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Channel extends CreateChannel {
    
    public int flags;
    public @Optional String guild_id, last_message_id, owner_id, application_id, last_pin_timestamp, permissions;
    public @Optional User[] recipients;
    
    public Channel() {
    }
    
    public Channel(String name, int type) {
        this.name = name;
        this.type = type;
    }
    
    public ThreadRequest getPublicArchivedThreads() {
        if (api == null) throw DiscordAPI.unlinkedEntity(Channel.this);
        final ThreadRequest request = new ThreadRequest();
        this.api.get("/channels/" + id + "/threads/archived/public", request)
            .exceptionally(request::error).thenAccept(Lazy::finish);
        return request;
    }
    
    public ThreadRequest getPrivateArchivedThreads() {
        if (api == null) throw DiscordAPI.unlinkedEntity(Channel.this);
        final ThreadRequest request = new ThreadRequest();
        this.api.get("/channels/" + id + "/threads/archived/private", request)
            .exceptionally(request::error).thenAccept(Lazy::finish);
        return request;
    }
    
    public RequestBuilder<Thread> createThread(String name) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Thread thread = new Thread();
        this.api.post("/channels/" + id + "/threads", "{\"name\":\"" + name + "\"}", thread)
            .exceptionally(thread::error).thenAccept(Lazy::finish);
        final RequestBuilder<Thread> builder = new RequestBuilder<>(api, "POST", "/channels/" + id + "/threads", thread);
        builder.set("name", name);
        return builder;
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
        class Body extends Payload {
            final String[] messages = ids.toArray(new String[0]);
        }
        this.api.post("/channels/" + this + "/messages/bulk-delete", new Body().toJson(null), null);
    }
    
    public Channel modify() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.patch("/channels/" + this, Json.toJson(this, CreateChannel.class, null), this)
            .exceptionally(this::error).thenAccept(Lazy::finish);
        return this;
    }
    
    public Channel delete() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.delete("/channels/" + this).exceptionally(this::error0).thenRun(this::finish);
        return this;
    }
    
    public boolean isText() {
        if (name == null) this.await();
        return switch (type) {
            case ChannelType.DM, ChannelType.GROUP_DM, ChannelType.GUILD_TEXT, ChannelType.GUILD_NEWS,
                ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_PRIVATE_THREAD ->
                true;
            default -> false;
        };
    }
    
    public Message send(Message message) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.sendMessage(this, message);
    }
    
    public ResultMessages getMessages() {
        return new ResultMessages();
    }
    
    public BulkEntity<Message> getPinnedMessages() {
        if (api == null) throw DiscordAPI.unlinkedEntity(Channel.this);
        return BulkEntity.of(api, Message.class, list -> this.api.get("/channels/" + id + "/pins", null, list));
    }
    
    public class ThreadRequest extends Lazy {
        public Thread[] threads = new Thread[0];
        public Thread.Member[] members = new Thread.Member[0];
        public boolean has_more;
        
        public Channel getOwner() {
            return Channel.this;
        }
    }
    
    public class ResultMessages extends BulkEntity<Message> {
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
        
        @Override
        protected int limit() {
            return this.limit;
        }
        
        @Override
        protected DiscordAPI api() {
            return api;
        }
    }
    
}
