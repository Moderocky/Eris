package mx.kenzie.eris.api.event.message;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class DeleteMessage extends Payload implements Event {
    public String id, channel_id, guild_id;

}
