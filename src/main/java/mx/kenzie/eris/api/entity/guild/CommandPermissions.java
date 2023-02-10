package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.data.Payload;

public class CommandPermissions extends Snowflake {
    public String application_id, guild_id;
    public Permission[] permissions;

    public static class Permission extends Payload {
        public String id;
        public int type;
        public boolean permission;
    }
}
