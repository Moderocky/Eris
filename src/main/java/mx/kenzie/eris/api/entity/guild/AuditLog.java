package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Thread;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.data.Payload;

public class AuditLog extends Entity {
    
    public Entry[] audit_log_entries;
    public Payload[] auto_moderation_rules; // todo
    public Payload[] guild_scheduled_events;
    public Payload[] integrations;
    public Thread[] threads;
    public User[] users;
    public Payload[] webhooks;
    
    public static class Entry extends Payload {
        public String target_id, user_id, id, reason;
        public @Optional Payload[] changes;
        public int action_type;
        public Payload options;
    
    }
    
}
