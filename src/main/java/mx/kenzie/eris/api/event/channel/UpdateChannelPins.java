package mx.kenzie.eris.api.event.channel;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class UpdateChannelPins extends Payload implements Event {
    public @Optional String guild_id, last_pin_timestamp;
    public String channel_id;
}
