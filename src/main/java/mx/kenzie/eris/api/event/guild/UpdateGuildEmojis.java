package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Emoji;
import mx.kenzie.eris.data.Payload;

public class UpdateGuildEmojis extends Payload implements Event {
    public String guild_id;
    public Emoji[] emojis;
}
