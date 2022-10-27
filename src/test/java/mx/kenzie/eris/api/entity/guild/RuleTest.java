package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class RuleTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Rule.class, """
            id	snowflake	the id of this rule
            guild_id	snowflake	the id of the guild which this rule belongs to
            name	string	the rule name
            creator_id	snowflake	the user which first created this rule
            event_type	integer	the rule event type
            trigger_type	integer	the rule trigger type
            trigger_metadata	object	the rule trigger metadata
            actions	array of action objects	the actions which will execute when the rule is triggered
            enabled	boolean	whether the rule is enabled
            exempt_roles	array of snowflakes	the role ids that should not be affected by the rule (Maximum of 20)
            exempt_channels	array of snowflakes	the channel ids that should not be affected by the rule (Maximum of 50)""");
        this.verify(Rule.Trigger.class, """
            keyword_filter	array of strings *	KEYWORD	substrings which will be searched for in content
            presets	array of keyword preset types	KEYWORD_PRESET	the internally pre-defined wordsets which will be searched for in content
            allow_list	array of strings *	KEYWORD_PRESET	substrings which will be exempt from triggering the preset trigger type
            mention_total_limit	integer	MENTION_SPAM	total number of unique role and user mentions allowed per message (Maximum of 50)""");
        this.verify(Rule.Action.class, """
            type	action type	the type of action
            metadata? *	action metadata	additional metadata needed during execution for this specific action type""");
        this.verify(Rule.Metadata.class, """
            channel_id	snowflake	SEND_ALERT_MESSAGE	channel to which user content should be logged
            duration_seconds	integer	TIMEOUT	timeout duration in seconds *""");
    }
    
}
