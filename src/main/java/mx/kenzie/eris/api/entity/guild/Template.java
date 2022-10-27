package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.User;

public class Template extends Lazy {
    
    public String code, name, description, creator_id;
    public String created_at, updated_at, source_guild_id;
    public Guild serialized_source_guild;
    public User creator;
    public int usage_count;
    public boolean is_dirty;
    
}
