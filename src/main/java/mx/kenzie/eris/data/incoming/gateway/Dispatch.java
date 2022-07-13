package mx.kenzie.eris.data.incoming.gateway;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.eris.data.incoming.Incoming;

import java.util.Map;

public class Dispatch extends Incoming {
    
    public @Name("d") Map<String, Object> data;
    
}
