package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.api.Lazy;

public class Member extends Lazy {
    
    public User user = new User();
    public String nick, avatar, joined_at, premium_since, communication_disabled_until;
    public String[] roles;
    public boolean deaf, mute, pending;
    public String permissions;
    
    public transient String guild_id;
    
    public boolean isValid() {
        return user != null && user.id != null;
    }
    
}
