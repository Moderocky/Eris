package mx.kenzie.eris.api.entity.command;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.magic.CommandType;
import mx.kenzie.eris.data.Payload;

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
    
    @Override
    public String toString() {
        return id;
    }
    
}
