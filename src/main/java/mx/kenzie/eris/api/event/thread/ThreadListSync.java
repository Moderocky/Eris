package mx.kenzie.eris.api.event.thread;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Thread;
import mx.kenzie.eris.data.Payload;

public class ThreadListSync extends Payload implements Event {
    public String guild_id;
    public String[] channel_ids;
    public Thread[] threads;
    public Thread.Member[] members;
}
