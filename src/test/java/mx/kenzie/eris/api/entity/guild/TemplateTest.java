package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class TemplateTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Template.class, """
            code	string	the template code (unique ID)
            name	string	template name
            description	?string	the description for the template
            usage_count	integer	number of times this template has been used
            creator_id	snowflake	the ID of the user who created the template
            creator	user object	the user who created the template
            created_at	ISO8601 timestamp	when this template was created
            updated_at	ISO8601 timestamp	when this template was last synced to the source guild
            source_guild_id	snowflake	the ID of the guild this template is based on
            serialized_source_guild	partial guild object	the guild snapshot this template contains
            is_dirty	?boolean	whether the template has unsynced changes""");
    }
    
}
