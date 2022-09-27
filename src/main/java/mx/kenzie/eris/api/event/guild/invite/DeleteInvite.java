package mx.kenzie.eris.api.event.guild.invite;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class DeleteInvite extends Payload implements Event {
    public @Optional String guild_id, channel_id, code;
}
