package mx.kenzie.eris.api.event.guild.member;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.User;

public class RemoveGuildMember extends Entity implements Event {
    public String guild_id;
    public User user;

    public Guild getGuild() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.getGuild(guild_id);
    }
}
