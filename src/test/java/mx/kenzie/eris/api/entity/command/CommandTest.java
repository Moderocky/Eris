package mx.kenzie.eris.api.entity.command;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class CommandTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Command.class, """
            id	snowflake	Unique ID of command	all
            type?	one of application command type	Type of command, defaults to 1	all
            application_id	snowflake	ID of the parent application	all
            guild_id?	snowflake	guild id of the command, if not global	all
            name	string	Name of command, 1-32 characters	all
            name_localizations?	?dictionary with keys in available locales	Localization dictionary for name field. Values follow the same restrictions as name	all
            description	string	Description for CHAT_INPUT commands, 1-100 characters. Empty string for USER and MESSAGE commands	all
            description_localizations?	?dictionary with keys in available locales	Localization dictionary for description field. Values follow the same restrictions as description	all
            options?	array of application command option	Parameters for the command, max of 25	CHAT_INPUT
            default_member_permissions	?string	Set of permissions represented as a bit set	all
            dm_permission?	boolean	Indicates whether the command is available in DMs with the app, only for globally-scoped commands. By default, commands are visible.	all
            default_permission?	?boolean	Not recommended for use as field will soon be deprecated. Indicates whether the command is enabled by default when the app is added to a guild, defaults to true	all
            version	snowflake	Autoincrementing version identifier updated during substantial record changes	all""");
        this.verify(Command.Permissions.class, """
            id	snowflake	ID of the command or the application ID
            application_id	snowflake	ID of the application the command belongs to
            guild_id	snowflake	ID of the guild
            permissions	array of application command permissions	Permissions for the command in the guild, max of 100""");
        this.verify(Command.Permissions.Override.class, """
            id	snowflake	ID of the role, user, or channel. It can also be a permission constant
            type	application command permission type	role (1), user (2), or channel (3)
            permission	boolean	true to allow, false, to disallow""");
    }
    
}
