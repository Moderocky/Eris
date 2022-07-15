package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.data.Payload;

public class Entity extends Payload {
    public transient DiscordAPI api;
    
    public boolean verify() {
        return true;
    }
    
    public String debugName() {
        return "[" + this.getClass().getSimpleName() + "]";
    }
}
