package mx.kenzie.eris.api.event.guild.event;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class ScheduledEventRemoveUser extends Payload implements Event {
    public String guild_scheduled_event_id;
    public String user_id;
    public String guild_id;
}
