package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class StageTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Stage.class, """
            id	snowflake	The id of this Stage instance
            guild_id	snowflake	The guild id of the associated Stage channel
            channel_id	snowflake	The id of the associated Stage channel
            topic	string	The topic of the Stage instance (1-120 characters)
            privacy_level	integer	The privacy level of the Stage instance
            discoverable_disabled	boolean	Whether or not Stage Discovery is disabled (deprecated)
            guild_scheduled_event_id	?snowflake	The id of the scheduled event for this Stage instance""");
    }
    
}
