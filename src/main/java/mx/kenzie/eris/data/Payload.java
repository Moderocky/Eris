package mx.kenzie.eris.data;

import mx.kenzie.argo.Json;

public class Payload {
    
    protected Payload() {
    }
    
    @Override
    public String toString() {
        return this.toJson("  ");
    }
    
    public String toJson(String indent) {
        return Json.toJson(this, indent);
    }
    
}
