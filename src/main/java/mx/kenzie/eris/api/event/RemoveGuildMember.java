package mx.kenzie.eris.api.event;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.data.Payload;

public class RemoveGuildMember extends Entity implements Event {
    public String guild_id;
    public User user;
    
    public Guild getGuild() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.getGuild(guild_id);
    }
}
