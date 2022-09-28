package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.data.Payload;

public class Self extends User {
    
    public String session_type, session_id;
    
    public Payload[] relationships, private_channels, presences, guild_join_requests; // todo
    public Guild[] guilds;
    public String[] geo_ordered_rtc_regions;
    
}
