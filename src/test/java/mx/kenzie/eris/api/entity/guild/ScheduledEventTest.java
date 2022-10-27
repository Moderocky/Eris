package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class ScheduledEventTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(ScheduledEvent.class, """
            id	snowflake	the id of the scheduled event
            guild_id	snowflake	the guild id which the scheduled event belongs to
            channel_id **	?snowflake	the channel id in which the scheduled event will be hosted, or null if scheduled entity type is EXTERNAL
            creator_id? *	?snowflake	the id of the user that created the scheduled event *
            name	string	the name of the scheduled event (1-100 characters)
            description?	?string	the description of the scheduled event (1-1000 characters)
            scheduled_start_time	ISO8601 timestamp	the time the scheduled event will start
            scheduled_end_time **	?ISO8601 timestamp	the time the scheduled event will end, required if entity_type is EXTERNAL
            privacy_level	privacy level	the privacy level of the scheduled event
            status	event status	the status of the scheduled event
            entity_type	scheduled entity type	the type of the scheduled event
            entity_id	?snowflake	the id of an entity associated with a guild scheduled event
            entity_metadata **	?entity metadata	additional metadata for the guild scheduled event
            creator?	user object	the user that created the scheduled event
            user_count?	integer	the number of users subscribed to the scheduled event
            image?	?string	the cover image hash of the scheduled event""");
    }
    
}
