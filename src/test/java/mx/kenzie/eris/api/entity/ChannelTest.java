package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class ChannelTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Channel.class, """
            id	snowflake	the id of this channel
            type	integer	the type of channel
            guild_id?	snowflake	the id of the guild (may be missing for some channel objects received over gateway guild dispatches)
            position?	integer	sorting position of the channel
            permission_overwrites?	array of overwrite objects	explicit permission overwrites for members and roles
            name?	?string	the name of the channel (1-100 characters)
            topic?	?string	the channel topic (0-4096 characters for GUILD_FORUM channels, 0-1024 characters for all others)
            nsfw?	boolean	whether the channel is nsfw
            last_message_id?	?snowflake	the id of the last message sent in this channel (or thread for GUILD_FORUM channels) (may not point to an existing or valid message or thread)
            bitrate?	integer	the bitrate (in bits) of the voice channel
            user_limit?	integer	the user limit of the voice channel
            rate_limit_per_user?*	integer	amount of seconds a user has to wait before sending another message (0-21600); bots, as well as users with the permission manage_messages or manage_channel, are unaffected
            recipients?	array of user objects	the recipients of the DM
            icon?	?string	icon hash of the group DM
            owner_id?	snowflake	id of the creator of the group DM or thread
            application_id?	snowflake	application id of the group DM creator if it is bot-created
            parent_id?	?snowflake	for guild channels: id of the parent category for a channel (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
            last_pin_timestamp?	?ISO8601 timestamp	when the last pinned message was pinned. This may be null in events such as GUILD_CREATE when a message is not pinned.
            rtc_region?	?string	voice region id for the voice channel, automatic when set to null
            video_quality_mode?	integer	the camera video quality mode of the voice channel, 1 when not present
            default_auto_archive_duration?	integer	default duration, copied onto newly created threads, in minutes, threads will stop showing in the channel list after the specified period of inactivity, can be set to: 60, 1440, 4320, 10080
            permissions?	string	computed permissions for the invoking user in the channel, including overwrites, only included when part of the resolved data received on a slash command interaction
            flags?	integer	channel flags combined as a bitfield
            default_thread_rate_limit_per_user?	integer	the initial rate_limit_per_user to set on newly created threads in a channel. this field is copied to the thread at creation time and does not live update.""");
    }
    
    @Test
    public void createChannelTest() {
        final Channel channel = new Channel();
        channel.type = ChannelType.GUILD_VOICE;
        channel.name = "Test Channel";
        guild.createChannel(channel);
        channel.await();
        assert channel.successful() : channel.error();
        assert channel.type == ChannelType.GUILD_VOICE;
        assert channel.name.equals("Test Channel");
        assert !channel.nsfw;
        assert channel.parent_id == null;
        channel.parent_id = "399248280854200332";
        channel.modify();
        channel.await();
        assert channel.parent_id != null;
        channel.delete();
        channel.await();
    }
    
}
