package mx.kenzie.eris.api.entity.command;

import mx.kenzie.eris.api.magic.OptionType;
import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class OptionTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Option.class, """
            type	one of application command option type	Type of option
            name	string	1-32 character name
            name_localizations?	?dictionary with keys in available locales	Localization dictionary for the name field. Values follow the same restrictions as name
            description	string	1-100 character description
            description_localizations?	?dictionary with keys in available locales	Localization dictionary for the description field. Values follow the same restrictions as description
            required?	boolean	If the parameter is required or optional--default false
            choices?	array of application command option choice	Choices for STRING, INTEGER, and NUMBER types for the user to pick from, max 25
            options?	array of application command option	If the option is a subcommand or subcommand group type, these nested options will be the parameters
            channel_types?	array of channel types	If the option is a channel type, the channels shown will be restricted to these types
            min_value?	integer for INTEGER options, double for NUMBER options	If the option is an INTEGER or NUMBER type, the minimum value permitted
            max_value?	integer for INTEGER options, double for NUMBER options	If the option is an INTEGER or NUMBER type, the maximum value permitted
            min_length?	integer	For option type STRING, the minimum allowed length (minimum of 0, maximum of 6000)
            max_length?	integer	For option type STRING, the maximum allowed length (minimum of 1, maximum of 6000)
            autocomplete? *	boolean	If autocomplete interactions are enabled for this STRING, INTEGER, or NUMBER type option""");
        this.magic(OptionType.class, """
            SUB_COMMAND	1
            SUB_COMMAND_GROUP	2
            STRING	3
            INTEGER	4	Any integer between -2^53 and 2^53
            BOOLEAN	5
            USER	6
            CHANNEL	7	Includes all channel types + categories
            ROLE	8
            MENTIONABLE	9	Includes users and roles
            NUMBER	10	Any double between -2^53 and 2^53
            ATTACHMENT	11	attachment object""");
        this.verify(Option.Choice.class, """
            name	string	1-100 character choice name
            name_localizations?	?dictionary with keys in available locales	Localization dictionary for the name field. Values follow the same restrictions as name
            value	string, integer, or double *	Value for the choice, up to 100 characters if string""");
    }
    
}
