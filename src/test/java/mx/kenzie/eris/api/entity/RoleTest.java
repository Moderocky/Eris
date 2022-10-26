package mx.kenzie.eris.api.entity;

import junit.framework.TestCase;
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
    }
    
}
