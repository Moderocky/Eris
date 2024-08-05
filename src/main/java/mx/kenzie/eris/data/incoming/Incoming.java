package mx.kenzie.eris.data.incoming;

import mx.kenzie.grammar.Name;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.network.NetworkController;

public class Incoming extends Payload {
    
    public int op;
    public @Name("s") Integer sequence;
    public @Name("t") String key;
    
    public transient NetworkController network;
    
    public void reply(Outgoing payload) {
        this.network.sendPayload(payload);
    }
    
}
