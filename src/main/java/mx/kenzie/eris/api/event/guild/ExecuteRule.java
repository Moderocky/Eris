package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.guild.Rule;
import mx.kenzie.eris.data.Payload;

public class ExecuteRule extends Payload implements Event {
    public String guild_id, rule_id, user_id, channel_id, message_id, alert_system_message_id, content,
        matched_keyword, matched_content;
    public Rule.Action action;
    public int trigger_type;
}
