package mx.kenzie.eris.api.event.channel;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.data.Payload;

public class StartTyping extends Payload implements Event {
    public String channel_id, user_id, guild_id;
    public int timestamp;
    public @Optional Member member;
}
