package mx.kenzie.eris.api.event.thread;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Thread;

public class UpdateThreadMember extends Thread.Member implements Event {
    public String guild_id;
}
