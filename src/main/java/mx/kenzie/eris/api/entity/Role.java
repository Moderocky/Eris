package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.guild.CreateRole;
import mx.kenzie.eris.data.Payload;

public class Role extends CreateRole {
    
    public int position;
    public boolean managed;
    public @Optional Tag tags;
    public @Optional String icon;
    
    public Role() {
    }
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role name(String name) {
        this.name = name;
        return this;
    }
    
    public Role permissions(String permissions) {
        this.permissions = permissions;
        return this;
    }
    
    public Role unicode_emoji(String unicode_emoji) {
        this.unicode_emoji = unicode_emoji;
        return this;
    }
    
    public Role color(int color) {
        this.color = color;
        return this;
    }
    
    public Role hoist(boolean hoist) {
        this.hoist = hoist;
        return this;
    }
    
    public Role mentionable(boolean mentionable) {
        this.mentionable = mentionable;
        return this;
    }
    
    public Role icon(Payload icon) {
//        this.icon = icon; todo
        return this;
    }

//    name	string	role name
//    color	integer	integer representation of hexadecimal color code
//    hoist	boolean	if this role is pinned in the user listing
//    icon?	?string	role icon hash
//    unicode_emoji?	?string	role unicode emoji
//    position	integer	position of this role
//    permissions	string	permission bit set
//    managed	boolean	whether this role is managed by an integration
//    mentionable	boolean	whether this role is mentionable
//    tags?	role tags object	the tags this role has
    
    public static class Tag extends Payload {
        public @Optional String bot_id, integration_id;
        public @Optional Payload premium_subscriber;
        
        public Tag() {
        }
    }
    
}
