package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.api.entity.guild.ModifyMember;

public class Member extends ModifyMember {
    
    public User user = new User();
    public String avatar, joined_at, premium_since;
    public boolean pending;
    public String permissions;
    
    public transient String guild_id;
    
    public boolean isValid() {
        return user != null && user.id != null;
    }
    
}
