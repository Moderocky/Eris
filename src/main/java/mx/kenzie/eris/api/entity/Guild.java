package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.entity.guild.*;
import mx.kenzie.eris.api.utility.BulkEntity;
import mx.kenzie.eris.api.utility.LazyList;
import mx.kenzie.eris.api.utility.Query;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Name;
import mx.kenzie.grammar.Optional;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Guild extends Snowflake {

    public final GuildHashes guild_hashes = new GuildHashes();
    public boolean premium_progress_bar_enabled, lazy;
    public @Optional
    @Name("owner") boolean is_owner;
    public @Optional boolean widget_enabled;
    public int afk_timeout, verification_level, default_message_notifications, explicit_content_filter, mfa_level,
        system_channel_flags, premium_tier, nsfw_level;
    public @Optional Integer max_presences, max_members, premium_subscription_count, max_video_channel_users,
        approximate_member_count, approximate_presence_count, hub_type;
    public String name, icon, splash, discovery_splash, owner_id, afk_channel_id, application_id, system_channel_id,
        rules_channel_id, vanity_url_code, description, banner, preferred_locale, public_updates_channel_id;
    public @Optional String icon_hash, permissions, region, widget_channel_id;
    public String[] features;
    public Role[] roles;
    public Emoji[] emojis;
    public @Optional Sticker[] stickers;
    public Payload[] embedded_activities;
    public Payload application_command_counts;
    public @Optional Payload welcome_screen;
    private transient LazyList<Role> roles0;

    public AuditLog getAuditLog(Query query) {
        final AuditLog log = new AuditLog();
        log.api = this.api;
        this.api.get("/guilds/" + this.id + "/audit-logs", query, log)
            .exceptionally(log::error).thenAccept(Lazy::finish);
        return log;
    }

    public BulkEntity<Rule> getRules() {
        return BulkEntity.of(api, Rule.class, list -> this.api.get("/guilds/" + id + "/auto-moderation/rules", null,
            list));
    }

    public <IRule> Rule getRule(IRule id) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String string = id.toString();
        final Rule rule = new Rule();
        rule.id = string;
        this.api.get("/guilds/" + this.id + "/auto-moderation/rules/" + string, null, rule)
            .exceptionally(rule::error).thenAccept(Lazy::finish);
        return rule;
    }

    public BulkEntity<Emoji> getEmojis() {
        return BulkEntity.of(api, Emoji.class, list -> this.api.get("/guilds/" + id + "/emojis", null, list));
    }

    public <IEmoji> Emoji getEmoji(IEmoji id) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Emoji emoji = new Emoji();
        emoji.id = String.valueOf(id);
        this.api.get("/guilds/" + this.id + "/emojis/" + emoji.id, null, emoji)
            .exceptionally(emoji::error).thenAccept(Lazy::finish);
        return emoji;
    }

    public <IApplication> LazyList<Command.Permissions> getCommandPermissions(IApplication application) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final LazyList<Command.Permissions> list = new LazyList<>(Command.Permissions.class, new ArrayList<>());
        this.api.get("/applications/" + application + "/guilds/" + id + "/commands/permissions", list)
            .exceptionally(list::error).thenAccept(Lazy::finish);
        return list;
    }

    public <IChannel> Channel getChannel(IChannel id) {
        return api.getChannel(String.valueOf(id));
    }

    public <IChannel> Forum getForum(IChannel id) {
        return api.getForumChannel(String.valueOf(id));
    }

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
        this.api.patch("/channels/" + channel, Json.toJson(channel, CreateChannel.class, null), channel)
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
        this.api.patch("/guilds/" + this + "/roles", "[{\"id\": \"" + id + "\", \"position\": " + position + "}]",
                roles0)
            .exceptionally(roles0::error).thenAccept(Lazy::finish);
        return roles0;
    }

    public LazyList<Thread> getActiveThreads() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return this.api.getActiveThreads(this);
    }

    public LazyList<Role> getRoles() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return this.api.getRoles(this);
    }

    public <IUser, IRole> Guild addRole(IUser id, IRole role) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
//        System.out.println("/guilds/" + this.id + "/members/" + api.getUserId(id) + "/roles/" + role);
// todo check if this is actually correct ?
        this.api.request("PUT", "/guilds/" + this.id + "/members/" + api.getUserId(id) + "/roles/" + role, "[]", null)
            .exceptionally(this::error).thenRun(this::finish);
        return this;
    }

    public <IUser, IRole> Guild removeRole(IUser id, IRole role) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.request("DELETE", "/guilds/" + this.id + "/members/" + api.getUserId(id) + "/roles/" + role,
            null, null).exceptionally(this::error).thenRun(this::finish);
        return this;
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
        return this.timeOut(user, duration, null);
    }

    public <IUser> Member timeOut(IUser user, Duration duration, String reason) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final String end = DateTimeFormatter.ISO_INSTANT.format(Instant.now().plus(duration));
        final Member member = new Member();
        member.communication_disabled_until = end;
        return this.api.modifyMember(this.id, user, member, reason);
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

    public Template createTemplate(String name, String description) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return this.api.createTemplate(this, name, description);
    }

    public WelcomeScreen getWelcomeScreen() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return this.api.getWelcomeScreen(this);
    }

    public WelcomeScreen modifyWelcomeScreen(WelcomeScreen screen) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return this.api.modifyWelcomeScreen(this, screen);
    }

    public static class GuildHashes extends Payload {

        public Hash channels, roles, metadata;
        public int version;

    }

    public static class Preview extends Snowflake {

        public String name, icon, splash, discovery_splash, description;
        public String[] features = new String[0];
        public Emoji[] emojis = new Emoji[0];
        public Sticker[] stickers = new Sticker[0];
        public int approximate_member_count, approximate_presence_count;

        public Guild getGuild() {
            if (api == null) throw DiscordAPI.unlinkedEntity(this);
            return this.api.getGuild(this.id);
        }

    }

    public class ResultChannels extends BulkEntity<Channel> {

        public int limit = 1000;

        @Override
        protected Class<Channel> getType() {
            return Channel.class;
        }

        @Override
        protected CompletableFuture<List<?>> getEntities(List<?> list) {
            if (api == null) throw DiscordAPI.unlinkedEntity(Guild.this);
            return api.get("/guilds/" + id + "/channels", list);
        }

        @Override
        protected int limit() {
            return limit;
        }

        @Override
        protected DiscordAPI api() {
            return api;
        }

        public ResultChannels limit(int limit) {
            this.limit = limit;
            return this;
        }

    }

}
