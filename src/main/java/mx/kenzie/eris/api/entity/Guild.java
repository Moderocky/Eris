package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.eris.data.Payload;

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
    
    public static class GuildHashes extends Payload {
        public Hash channels, roles, metadata;
        public int version;
    }
    
}
