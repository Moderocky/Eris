package mx.kenzie.eris.data.incoming.gateway;

import mx.kenzie.grammar.Name;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.incoming.Incoming;

public class InvalidSession extends Incoming implements Event {
    public @Name("d") boolean data;
}
