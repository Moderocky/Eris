package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class UpdateJoinRequest extends Payload implements Event {
    public String guild_id;
    public Payload request;
    public int status;
}
