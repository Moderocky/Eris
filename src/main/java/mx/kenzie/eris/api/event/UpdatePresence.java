package mx.kenzie.eris.api.event;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.outgoing.self.Presence;

public class UpdatePresence extends Payload implements Event {
    public User user;
    public String guild_id;
    public String status;
    public Presence.Activity[] activities;
    public Payload client_status;
}
