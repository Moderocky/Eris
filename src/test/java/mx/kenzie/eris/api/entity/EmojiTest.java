package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class EmojiTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Emoji.class, """
            id	?snowflake	emoji id
            name	?string (can be null only in reaction emoji objects)	emoji name
            roles?	array of role object ids	roles allowed to use this emoji
            user?	user object	user that created this emoji
            require_colons?	boolean	whether this emoji must be wrapped in colons
            managed?	boolean	whether this emoji is managed
            animated?	boolean	whether this emoji is animated
            available?	boolean	whether this emoji can be used, may be false due to loss of Server Boosts""");
    }
    
}
