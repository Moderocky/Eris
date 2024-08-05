package mx.kenzie.eris.api.entity.command;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.data.Payload;

public abstract class CreateCommand extends Lazy {

    public String name;
    public @Optional String description;
    public @Optional int type = 1;
    public @Optional Option[] options;
    public @Optional Payload name_localizations;
    public @Optional Payload description_localizations;
    public @Optional String default_member_permissions;
    public @Optional boolean dm_permission = true;
}
