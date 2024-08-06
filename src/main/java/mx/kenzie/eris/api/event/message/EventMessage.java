package mx.kenzie.eris.api.event.message;

import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.entity.User;

import java.util.Objects;

public abstract class EventMessage extends Message {

    public String guild_id;
    public Member member;
    public User[] mentions;

    public boolean isFromGuild() {
        return guild_id != null;
    }

    public boolean isFromGuild(long guildId) {
        return this.isFromGuild(guildId + "");
    }

    public boolean isFromGuild(String guildId) {
        return Objects.equals(guildId, this.guild_id);
    }

    public boolean isFromGuild(Snowflake guild) {
        return this.isFromGuild(guild.id);
    }

}
