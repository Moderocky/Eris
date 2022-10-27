package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class RoleTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Role.class, """
            id	snowflake	role id
            name	string	role name
            color	integer	integer representation of hexadecimal color code
            hoist	boolean	if this role is pinned in the user listing
            icon?	?string	role icon hash
            unicode_emoji?	?string	role unicode emoji
            position	integer	position of this role
            permissions	string	permission bit set
            managed	boolean	whether this role is managed by an integration
            mentionable	boolean	whether this role is mentionable
            tags?	role tags object	the tags this role has""");
        this.verify(Role.Tag.class, """
            bot_id?	snowflake	the id of the bot this role belongs to
            integration_id?	snowflake	the id of the integration this role belongs to
            premium_subscriber?	null	whether this is the guild's premium subscriber role""");
    }
    
}
