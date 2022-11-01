package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.data.Payload;

import java.util.Map;

public class Self extends User {
    
    public String session_type, session_id;
    
    public Payload[] relationships, presences, guild_join_requests; // todo where do these actually come from?
    public Channel[] private_channels;
    public Guild[] guilds;
    public String[] geo_ordered_rtc_regions;
    
    public transient Map<String, Object> __data;
    
}
