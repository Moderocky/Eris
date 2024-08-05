package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Integration;
import mx.kenzie.eris.api.entity.Thread;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.entity.Webhook;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Optional;

public class AuditLog extends Lazy {

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
        public @Optional Change[] changes;
        public int action_type;
        public Payload options;

    }

    public static class Change extends Payload {

        public Object new_value, old_value;
        public String key;

    }

}
