package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.data.Payload;

public class Channel extends Snowflake {
    
    public int type, position = -1, bitrate = -1, user_limit = -1, rate_limit_per_user = -1, video_quality_mode = -1, flags;
    public boolean nsfw;
    public String guild_id, name, topic, last_message_id, icon, owner_id, application_id, parent_id, last_pin_timestamp, rtc_region, permissions;
    public Payload[] permission_overwrites;
    public User[] recipients;
    
    public Message send(Message message) {
        return api.sendMessage(this, message);
    }
    
//    id	snowflake	the id of this channel
//    type	integer	the type of channel
//    guild_id?	snowflake	the id of the guild (may be missing for some channel objects received over gateway guild dispatches)
//    position?	integer	sorting position of the channel
//    permission_overwrites?	array of overwrite objects	explicit permission overwrites for members and roles
//    name?	?string	the name of the channel (1-100 characters)
//    topic?	?string	the channel topic (0-1024 characters)
//    nsfw?	boolean	whether the channel is nsfw
//    last_message_id?	?snowflake	the id of the last message sent in this channel (or thread for GUILD_FORUM channels) (may not point to an existing or valid message or thread)
//    bitrate?	integer	the bitrate (in bits) of the voice channel
//    user_limit?	integer	the user limit of the voice channel
//    rate_limit_per_user?*	integer	amount of seconds a user has to wait before sending another message (0-21600); bots, as well as users with the permission manage_messages or manage_channel, are unaffected
//    recipients?	array of user objects	the recipients of the DM
//    icon?	?string	icon hash of the group DM
//    owner_id?	snowflake	id of the creator of the group DM or thread
//    application_id?	snowflake	application id of the group DM creator if it is bot-created
//    parent_id?	?snowflake	for guild channels: id of the parent category for a channel (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
//    last_pin_timestamp?	?ISO8601 timestamp	when the last pinned message was pinned. This may be null in events such as GUILD_CREATE when a message is not pinned.
//        rtc_region?	?string	voice region id for the voice channel, automatic when set to null
//    video_quality_mode?	integer	the camera video quality mode of the voice channel, 1 when not present
//    message_count?	integer	an approximate count of messages in a thread, stops counting at 50
//    member_count?	integer	an approximate count of users in a thread, stops counting at 50
//    thread_metadata?	a thread metadata object	thread-specific fields not needed by other channels
//    member?	a thread member object	thread member object for the current user, if they have joined the thread, only included on certain API endpoints
//    default_auto_archive_duration?	integer	default duration that the clients (not the API) will use for newly created threads, in minutes, to automatically archive the thread after recent activity, can be set to: 60, 1440, 4320, 10080
//    permissions?	string	computed permissions for the invoking user in the channel, including overwrites, only included when part of the resolved data received on a slash command interaction
//    flags?

}
