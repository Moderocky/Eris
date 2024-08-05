package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.guild.AuditLog;

import java.util.Map;

public class CreateAuditLogEntry extends AuditLog.Entry implements Event {

    public String guild_id;
    public transient Map<String, Object> __data;

}
