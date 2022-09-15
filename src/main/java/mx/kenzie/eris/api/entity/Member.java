package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.api.entity.guild.ModifyMember;

public class Member extends ModifyMember {
    
    public User user = new User();
    public String avatar, joined_at, premium_since;
    public boolean pending;
    public String permissions;
    
    public transient String guild_id;
    
    private transient long permissions0;
    
    public boolean hasPermission(long permission) {
        if (permissions == null && permissions0 == 0) this.await();
        if (permissions == null) permissions = "0"; // solve null during conversion
        if (permissions0 == 0) permissions0 = Long.parseLong(permissions);
        return (permissions0 & permission) != 0;
    }
    
    public boolean isValid() {
        return user != null && user.id != null;
    }
    
}
