package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class DeleteGuildRole extends Payload implements Event {
    public String guild_id;
    public String role_id;
}
