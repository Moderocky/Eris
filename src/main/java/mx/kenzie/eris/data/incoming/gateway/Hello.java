package mx.kenzie.eris.data.incoming.gateway;

import mx.kenzie.grammar.Name;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.incoming.Incoming;

public class Hello extends Incoming implements Event {
    
    public @Name("d") Data data;
    
    public static class Data extends Payload {
        public int heartbeat_interval;
        public String[] _trace;
    }
}
