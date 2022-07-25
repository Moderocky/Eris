package mx.kenzie.eris.data.incoming.gateway;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.eris.data.incoming.Incoming;

public class InvalidSession extends Incoming {
    public @Name("d") boolean data;
}
