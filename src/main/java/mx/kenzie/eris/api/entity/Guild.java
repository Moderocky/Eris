package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.entity.guild.Ban;
import mx.kenzie.eris.api.entity.guild.ModifyMember;
import mx.kenzie.eris.data.Payload;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
    
    public Command registerCommand(Command command) {
        assert id != null;
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.registerCommand(command, this);
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
