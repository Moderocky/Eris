package mx.kenzie.eris.api.event.channel;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class UpdateWebhooks extends Payload implements Event {
    public String guild_id, channel_id;
}
