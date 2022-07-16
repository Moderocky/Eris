package mx.kenzie.eris.api.event;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Application;
import mx.kenzie.eris.api.entity.Self;
import mx.kenzie.eris.data.Payload;

public class Ready extends Payload implements Event { // todo
    
    public @Name("v") int version;
    public Self user;
    public Payload[] guilds;
    public String session_id;
    public @Optional int[] shard;
    public Application application;
    public @Optional Payload user_settings;
    
}
