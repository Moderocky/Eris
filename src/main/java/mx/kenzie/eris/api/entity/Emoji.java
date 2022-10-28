package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;

public class Emoji extends Snowflake {
    
    public String name;
    public @Optional String[] roles;
    public @Optional User user;
    public @Optional boolean require_colons, managed, animated, available;
    
    public boolean standard() {
        return id == null && name != null;
    }
    
}
