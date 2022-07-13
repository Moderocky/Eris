package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.data.Payload;

public class Thread extends Channel {
    
    public int message_count, member_count, default_auto_archive_duration;
    public Payload thread_metadata;
    public Payload member;
    
    
}
