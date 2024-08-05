package mx.kenzie.eris.data.outgoing.gateway;

import mx.kenzie.grammar.Name;
import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.data.outgoing.self.Presence;

public class Identify extends Outgoing {
    
    public final int op = 2;
    public final @Name("d") Data data = new Data();
    
    public static class Data extends Payload {
        public final Properties properties = new Properties();
        public String token;
        public int intents;
        public @Optional Presence presence;
    }
    
    public static class Properties extends Payload {
        public String os = "Unknown";
        public String browser = "Eris";
        public String device = "Unknown";
    }
    
}
