package mx.kenzie.eris.api.event.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Emoji;
import mx.kenzie.eris.data.Payload;

public class RemoveEmojiMessageReactions extends Payload implements Event {
    
    public String channel_id, message_id;
    public @Optional String guild_id;
    public Emoji emoji;
    
}
