package mx.kenzie.eris.api.entity;

public class Emoji extends Snowflake {
    
    public String name;
    public String[] roles;
    public User user;
    public boolean require_colons, managed, animated, available;
    
    public boolean standard() {
        return id == null && name != null;
    }
    
}
