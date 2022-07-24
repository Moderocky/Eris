package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.utility.BulkEntity;
import mx.kenzie.eris.data.Payload;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Thread extends Channel {
    
    public int message_count, member_count, default_auto_archive_duration;
    public Payload thread_metadata;
    public Payload member;
    
    public Thread join() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.request("PUT", "/channels/" + id + "/thread-members/@me", null, null)
            .exceptionally(this::error)
            .thenRun(this::finish);
        return this;
    }
    
    public Thread leave() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.request("DELETE", "/channels/" + id + "/thread-members/@me", null, null)
            .exceptionally(this::error)
            .thenRun(this::finish);
        return this;
    }
    
    public <IUser> Thread addUser(IUser user) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String id = api.getUserId(user);
        this.unready();
        this.api.request("PUT", "/channels/" + id + "/thread-members/" + id, null, null)
            .exceptionally(this::error)
            .thenRun(this::finish);
        return this;
    }
    
    public <IUser> Thread removeUser(IUser user) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String id = api.getUserId(user);
        this.unready();
        this.api.request("DELETE", "/channels/" + id + "/thread-members/" + id, null, null)
            .exceptionally(this::error)
            .thenRun(this::finish);
        return this;
    }
    
    public <IUser> Member getMember(IUser user) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String id = api.getUserId(user);
        final Member member = new Member();
        member.api = api;
        this.api.request("GET", "/channels/" + id + "/thread-members/" + id, null, member)
            .exceptionally(member::error).thenAccept(Lazy::finish);
        return member;
    }
    
    public class ThreadMembers extends BulkEntity<Member> {
        
        @Override
        protected int limit() {
            return 200;
        }
        
        @Override
        protected DiscordAPI api() {
            return api;
        }
        
        @Override
        protected Class<Member> getType() {
            return Member.class;
        }
        
        @Override
        protected CompletableFuture<List<?>> getEntities(List<?> list) {
            return Thread.this.api.request("GET", "/channels/" + id + "/thread-members", null, list);
        }
    }
    
    public BulkEntity<Member> getMembers() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return new ThreadMembers();
    }
    
    public static class Member extends Lazy {
        public String id, user_id, join_timestamp;
        public int flags;
    }
    
    
}
