package mx.kenzie.eris.api.entity.command;

import mx.kenzie.argo.meta.Optional;

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
    
}
