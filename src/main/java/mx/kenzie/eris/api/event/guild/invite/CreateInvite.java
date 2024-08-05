package mx.kenzie.eris.api.event.guild.invite;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Invite;

public class CreateInvite extends Invite implements Event {
    public @Optional String guild_id, channel_id;
}
