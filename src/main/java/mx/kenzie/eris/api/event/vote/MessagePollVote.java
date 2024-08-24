package mx.kenzie.eris.api.event.vote;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Optional;

public abstract class MessagePollVote extends Payload implements Event {

    public String user_id, channel_id, message_id;
    public @Optional String guild_id;
    public int answer_id;

}
