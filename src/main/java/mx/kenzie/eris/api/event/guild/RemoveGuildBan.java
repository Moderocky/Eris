package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.data.Payload;

public class RemoveGuildBan extends Payload implements Event {
    public String guild_id;
    public User user;
}
