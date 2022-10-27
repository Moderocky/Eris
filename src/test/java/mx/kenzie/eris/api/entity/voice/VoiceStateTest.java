package mx.kenzie.eris.api.entity.voice;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class VoiceStateTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(VoiceState.class, """
            guild_id?	snowflake	the guild id this voice state is for
            channel_id	?snowflake	the channel id this user is connected to
            user_id	snowflake	the user id this voice state is for
            member?	guild member object	the guild member this voice state is for
            session_id	string	the session id for this voice state
            deaf	boolean	whether this user is deafened by the server
            mute	boolean	whether this user is muted by the server
            self_deaf	boolean	whether this user is locally deafened
            self_mute	boolean	whether this user is locally muted
            self_stream?	boolean	whether this user is streaming using "Go Live"
            self_video	boolean	whether this user's camera is enabled
            suppress	boolean	whether this user's permission to speak is denied
            request_to_speak_timestamp	?ISO8601 timestamp	the time at which the user requested to speak""");
    }
    
}
