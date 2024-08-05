package mx.kenzie.eris.api.entity.command;

import mx.kenzie.argo.Json;
import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.magic.CommandType;
import mx.kenzie.eris.data.Payload;

public class Command extends CreateCommand {

    public String id;
    public @Optional String guild_id, application_id, version;
    public @Optional
    @Deprecated Boolean default_permission;

    public Command() {
    }

    public Command(String name, String description, Option... options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public static Command slash(String name, String description, Option... options) {
        return new Command(name, description, options);
    }

    public static Command user(String name) {
        final Command command = new Command();
        command.name = name;
        command.type = CommandType.USER;
        return command;
    }

    public static Command message(String name) {
        final Command command = new Command();
        command.name = name;
        command.type = CommandType.MESSAGE;
        return command;
    }

    public Command name(String name) {
        this.name = name;
        return this;
    }

    public Command description(String description) {
        this.description = description;
        return this;
    }

    public Command type(int type) {
        this.type = type;
        return this;
    }

    public Command options(Option[] options) {
        this.options = options;
        return this;
    }

    public Command name_localizations(Payload name_localizations) {
        this.name_localizations = name_localizations;
        return this;
    }

    public Command description_localizations(Payload description_localizations) {
        this.description_localizations = description_localizations;
        return this;
    }

    public Command default_member_permissions(String default_member_permissions) {
        this.default_member_permissions = default_member_permissions;
        return this;
    }

    public Command dm_permission(boolean dm_permission) {
        this.dm_permission = dm_permission;
        return this;
    }

    public Command permissions(long permissions) {
        this.default_member_permissions = Long.toString(permissions);
        return this;
    }

    public Permissions getPermissions() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        assert guild_id != null : "Permissions may be retrieved only within a guild.";
        final Permissions permissions = new Permissions();
        permissions.api = api;
        permissions.application_id = application_id;
        permissions.guild_id = guild_id;
        this.api.get("/applications/" + application_id + "/guilds/" + guild_id + "/commands/" + id + "/permissions", permissions)
            .exceptionally(permissions::error).thenAccept(Lazy::finish);
        return permissions;
    }

    public Command updatePermissions(Permissions permissions) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        assert guild_id != null : "Permissions may be altered only within a guild.";
        this.unready();
        this.api.request("PUT", "/applications/" + application_id + "/guilds/" + guild_id + "/commands/" + id + "/permissions",
                Json.toJson(permissions))
            .exceptionally(throwable -> {
                this.error(throwable);
                return null;
            })
            .thenAccept(throwable -> this.finish());
        return this;
    }

    @Override
    public String toString() {
        return id;
    }

    public static class Permissions extends Snowflake {
        public String application_id, guild_id;
        public Override[] permissions = new Override[0];

        public static class Override extends Snowflake {
            public int type;
            public boolean permission;
        }
    }

}
