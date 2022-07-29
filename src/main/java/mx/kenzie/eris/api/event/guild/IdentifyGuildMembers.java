package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.api.event.UpdatePresence;
import mx.kenzie.eris.data.Payload;

public class IdentifyGuildMembers extends Payload implements Event {
    public String guild_id, nonce;
    public Member[] members;
    public int chunk_index, chunk_count;
    // not_found?
    public UpdatePresence[] presences;
}
