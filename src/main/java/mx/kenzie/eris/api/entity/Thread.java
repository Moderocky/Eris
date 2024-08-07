package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.utility.BulkEntity;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Optional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Thread extends Channel {

    public @Optional int message_count, member_count, total_message_sent, default_auto_archive_duration;
    public @Optional Payload thread_metadata;
    public @Optional Member member;
    public @Optional String[] member_ids_preview;
    public @Optional String[] applied_tags = new String[0];

    public boolean isForumThread() {
        if (applied_tags.length > 0) return true;
        this.await();
        return this.api.getChannel(parent_id).isForum();
    }

    public Thread modify() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.patch("/channels/" + this, Json.toJson(this, null,
                "name", "archived", "auto_archive_duration", "locked", "invitable", "rate_limit_per_user",
                "flags", "applied_tags"), this)
            .exceptionally(this::error).thenAccept(Lazy::finish);
        return this;
    }

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
        this.api.request("PUT", "/channels/" + this.id + "/thread-members/" + id, null, null)
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

    public BulkEntity<Member> getMembers() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return new ThreadMembers();
    }

    public <IChannel> boolean isParent(IChannel channel) {
        if (parent_id != null && channel instanceof Channel other)
            return Objects.equals(parent_id, other.id);
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        if (parent_id == null && this.name == null && !this.ready()) this.await(); // probably not yet loaded
        return Objects.equals(parent_id, api.getId(channel));
    }

    public static class Member extends Lazy {

        public String id, user_id, join_timestamp;
        public int flags;

    }

    public class ThreadMembers extends BulkEntity<Member> {

        @Override
        protected Class<Member> getType() {
            return Member.class;
        }

        @Override
        protected CompletableFuture<List<?>> getEntities(List<?> list) {
            return Thread.this.api.request("GET", "/channels/" + id + "/thread-members", null, list);
        }

        @Override
        protected int limit() {
            return 200;
        }

        @Override
        protected DiscordAPI api() {
            return api;
        }

    }

}
