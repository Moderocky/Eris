package mx.kenzie.eris.api.entity.command;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.magic.CommandType;

public class Command extends CreateCommand {
    
    public String id;
    public @Optional String guild_id, application_id, version;
    
    public Command() {
    }
    
    public Command(String name, String description, Option... options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }
    
    public Command guild_id(String guild_id) {
        this.guild_id = guild_id;
        return this;
    }
    
    public Command application_id(String application_id) {
        this.application_id = application_id;
        return this;
    }
    
    public Command version(String version) {
        this.version = version;
        return this;
    }
    
    public Command permissions(long permissions) {
        this.default_member_permissions = Long.toString(permissions);
        return this;
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
    
}
