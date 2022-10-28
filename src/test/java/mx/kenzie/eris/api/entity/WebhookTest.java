package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class WebhookTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Webhook.class, """
            id	snowflake	the id of the webhook
            type	integer	the type of the webhook
            guild_id?	?snowflake	the guild id this webhook is for, if any
            channel_id	?snowflake	the channel id this webhook is for, if any
            user?	user object	the user this webhook was created by (not returned when getting a webhook with its token)
            name	?string	the default name of the webhook
            avatar	?string	the default user avatar hash of the webhook
            token?	string	the secure token of the webhook (returned for Incoming Webhooks)
            application_id	?snowflake	the bot/OAuth2 application that created this webhook
            source_guild? *	partial guild object	the guild of the channel that this webhook is following (returned for Channel Follower Webhooks)
            source_channel? *	partial channel object	the channel that this webhook is following (returned for Channel Follower Webhooks)
            url?	string	the url used for executing the webhook (returned by the webhooks OAuth2 flow)""");
    }
    
}
