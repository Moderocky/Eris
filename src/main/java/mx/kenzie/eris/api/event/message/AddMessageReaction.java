package mx.kenzie.eris.api.event.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Emoji;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.data.Payload;

public class AddMessageReaction extends Payload implements Event {

    public String user_id, channel_id, message_id;
    public @Optional String guild_id;
    public @Optional Member member;
    public Emoji emoji;

}
