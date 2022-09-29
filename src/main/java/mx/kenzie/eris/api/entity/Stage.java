package mx.kenzie.eris.api.entity;

public class Stage extends Snowflake {
    public String guild_id, channel_id, topic, guild_scheduled_event_id;
    public int privacy_level;
    public boolean discoverable_disabled;
}
