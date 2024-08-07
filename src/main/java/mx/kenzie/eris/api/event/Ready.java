package mx.kenzie.eris.api.event;

import mx.kenzie.grammar.Name;
import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Application;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.Self;
import mx.kenzie.eris.data.Payload;

public class Ready extends Payload implements Event { // todo

    public @Name("v") int version;
    public Self user;
    public Guild[] guilds;
    public String session_id;
    public @Optional int[] shard;
    public Application application;
    public @Optional Payload user_settings;

}
