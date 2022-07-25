package mx.kenzie.eris.api.event.thread;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Thread;
import mx.kenzie.eris.data.Payload;

public class UpdateThreadMembers extends Payload implements Event {
    public String id, guild_id;
    public int member_count;
    public Thread.Member[] added_members = new Thread.Member[0];
    public String[] removed_member_ids = new String[0];
}
