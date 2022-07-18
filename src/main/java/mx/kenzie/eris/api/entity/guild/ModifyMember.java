package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Lazy;

public class ModifyMember extends Lazy {
    
    public @Optional String[] roles;
    public @Optional String nick, channel_id;
    public @Optional Boolean deaf, mute;
    public @Optional String communication_disabled_until;
    
}
