package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class AuditLogTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(AuditLog.class, """
            application_commands	array of application commands objects	List of application commands referenced in the audit log
            audit_log_entries	array of audit log entry objects	List of audit log entries, sorted from most to least recent
            auto_moderation_rules	array of auto moderation rule objects	List of auto moderation rules referenced in the audit log
            guild_scheduled_events	array of guild scheduled event objects	List of guild scheduled events referenced in the audit log
            integrations	array of partial integration objects	List of partial integration objects
            threads	array of thread-specific channel objects	List of threads referenced in the audit log*
            users	array of user objects	List of users referenced in the audit log
            webhooks	array of webhook objects	List of webhooks referenced in the audit log""");
    }
    
}
