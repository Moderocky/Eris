package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.Name;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.entity.guild.Ban;
import mx.kenzie.eris.api.entity.guild.CreateChannel;
import mx.kenzie.eris.api.entity.guild.CreateRole;
import mx.kenzie.eris.api.entity.guild.ModifyMember;
import mx.kenzie.eris.api.utility.BulkEntity;
import mx.kenzie.eris.api.utility.LazyList;
import mx.kenzie.eris.data.Payload;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Guild extends Snowflake {
    
    public boolean widget_enabled, premium_progress_bar_enabled, lazy;
    public @Name("owner") boolean is_owner;
    public int afk_timeout, verification_level, default_message_notifications, explicit_content_filter, mfa_level, system_channel_flags, premium_tier, nsfw_level;
    public Integer max_presences, max_members, premium_subscription_count, max_video_channel_users, approximate_member_count, approximate_presence_count, hub_type;
    public String name, icon, icon_hash, splash, discovery_splash, owner_id, permissions, region, afk_channel_id, widget_channel_id, application_id, system_channel_id, rules_channel_id, vanity_url_code, description, banner, preferred_locale, public_updates_channel_id;
    
    public String[] features;
    public Payload[] roles, emojis, stickers, embedded_activities;
    
    public Payload welcome_screen, application_command_counts;
    public final GuildHashes guild_hashes = new GuildHashes();
    
    private transient LazyList<Role> roles0;
    
    public Command registerCommand(Command command) {
        assert id != null;
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        command.api = api;
        return api.registerCommand(command, this);
    }
    
    public Channel createChannel(Channel channel) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        channel.api = api;
        this.api.post("/guilds/" + this + "/channels", Json.toJson(channel, CreateChannel.class, null), channel)
            .exceptionally(channel::error).thenAccept(Lazy::finish);
        return channel;
    }
    
    public Channel modifyChannel(Channel channel) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        channel.api = api;
        this.api.post("/channels/" + channel, Json.toJson(channel, CreateChannel.class, null), channel)
            .exceptionally(channel::error).thenAccept(Lazy::finish);
        return channel;
    }
    
    public <IChannel> void deleteChannel(IChannel channel) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.api.delete("/channels/" + channel);
    }
    
    public Role createRole(Role role) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        role.api = api;
        this.api.post("/guilds/" + this + "/roles", Json.toJson(role, CreateRole.class, null), role)
            .exceptionally(role::error).thenAccept(Lazy::finish);
        return role;
    }
    
    public void deleteRole(Role role) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        role.api = api;
        this.api.delete("/guilds/" + this + "/roles/" + role);
    }
    
    public <IRole> LazyList<Role> moveRole(IRole role, int position) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String id = role instanceof String string ? string : role.toString();
        if (roles0 == null) roles0 = new LazyList<>(Role.class, new ArrayList<>());
        this.api.patch("/guilds/" + this + "/roles", "[{\"id\": \"" + id + "\", \"position\": " + position + "}]", roles0)
            .exceptionally(roles0::error).thenAccept(Lazy::finish);
        return roles0;
    }
    
    public LazyList<Role> getRoles() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        if (roles0 != null) return api.updateRoles(this, roles0);
        return roles0 = this.api.getRoles(this);
    }
    
    public class ResultChannels extends BulkEntity<Channel> {
        
        public int limit = 1000;
    
        @Override
        protected int limit() { return limit; }
    
        @Override
        protected DiscordAPI api() {return api;}
    
        @Override
        protected Class<Channel> getType() {
            return Channel.class;
        }
    
        public ResultChannels limit(int limit) {
            this.limit = limit;
            return this;
        }
    
        @Override
        protected CompletableFuture<List<?>> getEntities(List<?> list) {
            if (api == null) throw DiscordAPI.unlinkedEntity(Guild.this);
            return api.get("/guilds/" + id + "/channels", list);
        }
    }
    
    public ResultChannels getChannels() {
        return new ResultChannels();
    }
    
    public <IUser> Member setVoiceDeafened(IUser user, boolean deaf) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Member member = new Member();
        member.deaf = deaf;
        return this.api.modifyMember(this.id, user, member);
    }
    
    public <IUser> Member setVoiceMuted(IUser user, boolean mute) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Member member = new Member();
        member.mute = mute;
        return this.api.modifyMember(this.id, user, member);
    }
    
    public <IUser> Member setNickname(IUser user, String name) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Member member = new Member();
        member.nick = name;
        return this.api.modifyMember(this.id, user, member);
    }
    
    public <IUser> Member timeOut(IUser user, Duration duration) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String end = DateTimeFormatter.ISO_INSTANT.format(Instant.now().plus(duration));
        final Member member = new Member();
        member.communication_disabled_until = end;
        return this.api.modifyMember(this.id, user, member);
    }
    
    public <IUser> void ban(IUser user, String reason) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Ban ban = new Ban();
        ban.reason = reason;
        this.api.createBan(this, user, ban);
    }
    
    public <IUser> void ban(IUser user, Ban ban) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.api.createBan(this, user, ban);
    }
    
    public static class GuildHashes extends Payload {
        public Hash channels, roles, metadata;
        public int version;
    }
    
    public static class Preview extends Snowflake {
        public String name, icon, splash, discovery_splash, description;
        public String[] features;
        public Payload[] emojis, stickers;
        public int approximate_member_count, approximate_presence_count;
        
        public Guild getGuild() {
            if (api == null) throw DiscordAPI.unlinkedEntity(this);
            return this.api.getGuild(this.id);
        }
        
    }
    
}
