package mx.kenzie.eris.api.event.message;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class BulkDeleteMessage extends Payload implements Event {
    public String[] ids;
    public String channel_id, guild_id;
    
}
