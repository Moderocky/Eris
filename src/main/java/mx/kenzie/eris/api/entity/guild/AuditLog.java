package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Thread;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.data.Payload;

public class AuditLog extends Entity {
    
    public Command[] application_commands;
    public Entry[] audit_log_entries;
    public Rule[] auto_moderation_rules;
    public ScheduledEvent[] guild_scheduled_events;
    public Integration[] integrations;
    public Thread[] threads;
    public User[] users;
    public Webhook[] webhooks;
    
    public static class Entry extends Payload {
        public String target_id, user_id, id, reason;
        public @Optional Payload[] changes;
        public int action_type;
        public Payload options;
        
    }
    
}
