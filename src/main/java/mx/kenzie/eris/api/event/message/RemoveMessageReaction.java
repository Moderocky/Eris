package mx.kenzie.eris.api.event.message;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Emoji;
import mx.kenzie.eris.data.Payload;

public class RemoveMessageReaction extends Payload implements Event {

    public String user_id, channel_id, message_id;
    public @Optional String guild_id;
    public Emoji emoji;

}
