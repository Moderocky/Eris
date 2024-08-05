package mx.kenzie.eris.api.event.message;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class RemoveAllMessageReactions extends Payload implements Event {

    public String channel_id, message_id;
    public @Optional String guild_id;

}
