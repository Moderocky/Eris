package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class WelcomeScreenTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(WelcomeScreen.class, """
            description	?string	the server description shown in the welcome screen
            welcome_channels	array of welcome screen channel objects	the channels shown in the welcome screen, up to 5""");
        this.verify(WelcomeScreen.Channel.class, """
            channel_id	snowflake	the channel's id
            description	string	the description shown for the channel
            emoji_id	?snowflake	the emoji id, if the emoji is custom
            emoji_name	?string	the emoji name if custom, the unicode character if standard, or null if no emoji is set""");
    }
    
}
