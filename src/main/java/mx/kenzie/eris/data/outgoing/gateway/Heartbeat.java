package mx.kenzie.eris.data.outgoing.gateway;

import mx.kenzie.grammar.Name;
import mx.kenzie.eris.data.outgoing.Outgoing;

public class Heartbeat extends Outgoing {
    
    public final int op = 1;
    public @Name("d") Integer data;
    
}
