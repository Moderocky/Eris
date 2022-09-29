package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class UpdateVoiceServer extends Payload implements Event {
    public String token, guild_id, endpoint;
}
