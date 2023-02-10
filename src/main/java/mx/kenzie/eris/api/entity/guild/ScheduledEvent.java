package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.magic.GuildScheduledEvent;
import mx.kenzie.eris.data.Payload;

public class ScheduledEvent extends Snowflake implements GuildScheduledEvent {
    public String guild_id, name, entity_id;
    public @Optional String channel_id, creator_id, description, image;
    public String scheduled_start_time, scheduled_end_time;
    public int privacy_level = 2, status = 1, entity_type;
    public @Optional Metadata entity_metadata;
    public @Optional User creator;
    public @Optional Integer user_count;

    public static class Metadata extends Payload {
        public @Optional String location;
    }

}
