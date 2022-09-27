package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.voice.VoiceState;
import mx.kenzie.eris.data.Payload;

public class IdentifyGuild extends Guild implements Event {
    
    public String joined_at;
    public boolean large, unavailable;
    public int member_count;
    public VoiceState[] voice_states;
    
    public Payload[] members, presences, stage_instances, guild_scheduled_events;
    public Channel[] channels;
    public Thread[] threads;
//    member_count	integer	total number of members in this guild
//    voice_states	array of partial voice state objects	states of members currently in voice channels; lacks the guild_id key
//    members	array of guild member objects	users in the guild
//    channels	array of channel objects	channels in the guild
//    threads	array of channel objects	all active threads in the guild that current user has permission to view
//    presences	array of partial presence update objects	presences of the members in the guild, will only include non-offline members if the size is greater than large threshold
//    stage_instances	array of stage instance objects	Stage instances in the guild
//        guild_scheduled_events

}
