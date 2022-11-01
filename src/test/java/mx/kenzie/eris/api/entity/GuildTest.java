package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

import java.util.Arrays;

public class GuildTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Guild.class, """
            id	snowflake	guild id
            name	string	guild name (2-100 characters, excluding trailing and leading whitespace)
            icon	?string	icon hash
            icon_hash?	?string	icon hash, returned when in the template object
            splash	?string	splash hash
            discovery_splash	?string	discovery splash hash; only present for guilds with the "DISCOVERABLE" feature
            owner? *	boolean	true if the user is the owner of the guild
            owner_id	snowflake	id of owner
            permissions? *	string	total permissions for the user in the guild (excludes overwrites)
            region? **	?string	voice region id for the guild (deprecated)
            afk_channel_id	?snowflake	id of afk channel
            afk_timeout	integer	afk timeout in seconds, can be set to: 60, 300, 900, 1800, 3600
            widget_enabled?	boolean	true if the server widget is enabled
            widget_channel_id?	?snowflake	the channel id that the widget will generate an invite to, or null if set to no invite
            verification_level	integer	verification level required for the guild
            default_message_notifications	integer	default message notifications level
            explicit_content_filter	integer	explicit content filter level
            roles	array of role objects	roles in the guild
            emojis	array of emoji objects	custom guild emojis
            features	array of guild feature strings	enabled guild features
            mfa_level	integer	required MFA level for the guild
            application_id	?snowflake	application id of the guild creator if it is bot-created
            system_channel_id	?snowflake	the id of the channel where guild notices such as welcome messages and boost events are posted
            system_channel_flags	integer	system channel flags
            rules_channel_id	?snowflake	the id of the channel where Community guilds can display rules and/or guidelines
            max_presences?	?integer	the maximum number of presences for the guild (null is always returned, apart from the largest of guilds)
            max_members?	integer	the maximum number of members for the guild
            vanity_url_code	?string	the vanity url code for the guild
            description	?string	the description of a guild
            banner	?string	banner hash
            premium_tier	integer	premium tier (Server Boost level)
            premium_subscription_count?	integer	the number of boosts this guild currently has
            preferred_locale	string	the preferred locale of a Community guild; used in server discovery and notices from Discord, and sent in interactions; defaults to "en-US"
            public_updates_channel_id	?snowflake	the id of the channel where admins and moderators of Community guilds receive notices from Discord
            max_video_channel_users?	integer	the maximum amount of users in a video channel
            approximate_member_count?	integer	approximate number of members in this guild, returned from the GET /guilds/<id> endpoint when with_counts is true
            approximate_presence_count?	integer	approximate number of non-offline members in this guild, returned from the GET /guilds/<id> endpoint when with_counts is true
            welcome_screen?	welcome screen object	the welcome screen of a Community guild, shown to new members, returned in an Invite's guild object
            nsfw_level	integer	guild NSFW level
            stickers?	array of sticker objects	custom guild stickers
            premium_progress_bar_enabled	boolean	whether the guild has the boost progress bar enabled""");
        this.verify(Guild.Preview.class, """
            id	snowflake	guild id
            name	string	guild name (2-100 characters)
            icon	?string	icon hash
            splash	?string	splash hash
            discovery_splash	?string	discovery splash hash
            emojis	array of emoji objects	custom guild emojis
            features	array of guild feature strings	enabled guild features
            approximate_member_count	integer	approximate number of members in this guild
            approximate_presence_count	integer	approximate number of online members in this guild
            description	?string	the description for the guild
            stickers	array of sticker objects	custom guild stickers""");
    }
    
    @Test
    public void guildPreviewTest() {
        final Guild.Preview preview = api.getGuildPreview(guild.id());
        preview.await();
        assert preview.successful() : channel.error();
        assert preview.approximate_member_count > 0 : preview.approximate_member_count;
        assert preview.approximate_presence_count > 0 : preview.approximate_presence_count;
        assert preview.name != null : "Name was not retrieved.";
        assert preview.icon != null : "Icon was not retrieved.";
        assert preview.splash == null : "Splash was erroneously retrieved.";
        assert preview.discovery_splash == null : "Discovery was erroneously retrieved.";
        assert preview.features.length == 0 : Arrays.toString(preview.features);
        assert preview.emojis.length == 0 : Arrays.toString(preview.emojis);
        assert preview.stickers.length == 0 : Arrays.toString(preview.stickers);
    }
    
}
