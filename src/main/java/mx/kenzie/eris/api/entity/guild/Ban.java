package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Lazy;

public class Ban extends Lazy {
    
    public int delete_message_days;
    public @Optional String reason;

    public Ban() {
    }
    
    public Ban(String reason, int deleteDays) {
        this.reason = reason;
        this.delete_message_days = deleteDays;
    }
    
}
