package mx.kenzie.eris.data.outgoing.gateway;

import mx.kenzie.grammar.Name;
import mx.kenzie.eris.data.outgoing.Outgoing;

public class Resume extends Outgoing {
    
    public final int op = 6;
    public @Name("d") Data data = new Data();
    
    
    public static class Data {
        public String token;
        public String session_id;
        public @Name("seq") int sequence;
    }
}
