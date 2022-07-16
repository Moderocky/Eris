package mx.kenzie.eris.data;

import mx.kenzie.argo.Json;

public class Payload {
    
    protected Payload() {
    }
    
    @Override
    public String toString() {
        return Json.toJson(this, "  ");
    }
    
}
