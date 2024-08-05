package mx.kenzie.eris.data.outgoing.self;

import mx.kenzie.grammar.Name;
import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.outgoing.Outgoing;

public class Presence extends Outgoing {
    public final int op = 3;
    
    public final @Name("d") Data data = new Data();
    
    @Override
    public boolean verify() {
        return data.status != null;
    }
    
    public static class Data extends Payload {
        public Integer since;
        public Activity[] activities = new Activity[0];
        
        public String status = "online";
        public boolean afk;
    }
    
    public static class Activity { // Todo: extra data.
        public String name;
        public int type;
        public @Optional String url;
    }
}
