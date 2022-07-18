package mx.kenzie.eris.api.entity.command;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.data.Payload;

public abstract class CreateCommand extends Lazy {
    
    public String name;
    public @Optional String description;
    public int type = 1;
    public @Optional Option[] options;
    public @Optional Payload name_localizations;
    public @Optional Payload description_localizations;
    public @Optional String default_member_permissions;
    public boolean dm_permission = true;
    
}
