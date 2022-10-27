package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class InviteTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Invite.class, """
            code	string	the invite code (unique ID)
            guild?	partial guild object	the guild this invite is for
            channel	?partial channel object	the channel this invite is for
            inviter?	user object	the user who created the invite
            target_type?	integer	the type of target for this voice channel invite
            target_user?	user object	the user whose stream to display for this voice channel stream invite
            target_application?	partial application object	the embedded application to open for this voice channel embedded application invite
            approximate_presence_count?	integer	approximate count of online members, returned from the GET /invites/<code> endpoint when with_counts is true
            approximate_member_count?	integer	approximate count of total members, returned from the GET /invites/<code> endpoint when with_counts is true
            expires_at?	?ISO8601 timestamp	the expiration date of this invite, returned from the GET /invites/<code> endpoint when with_expiration is true
            stage_instance?	invite stage instance object	stage instance data if there is a public Stage instance in the Stage channel this invite is for (deprecated)
            guild_scheduled_event?	guild scheduled event object	guild scheduled event data, only included if guild_scheduled_event_id contains a valid guild scheduled event id""");
    }
    
}
