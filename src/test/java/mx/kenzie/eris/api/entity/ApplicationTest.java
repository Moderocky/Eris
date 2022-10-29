package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class ApplicationTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Application.class, """
            id	snowflake	the id of the app
            name	string	the name of the app
            icon	?string	the icon hash of the app
            description	string	the description of the app
            rpc_origins?	array of strings	an array of rpc origin urls, if rpc is enabled
            bot_public	boolean	when false only app owner can join the app's bot to guilds
            bot_require_code_grant	boolean	when true the app's bot will only join upon completion of the full oauth2 code grant flow
            terms_of_service_url?	string	the url of the app's terms of service
            privacy_policy_url?	string	the url of the app's privacy policy
            owner?	partial user object	partial user object containing info on the owner of the application
            summary (deprecated)	string	deprecated and will be removed in v11. An empty string.
            verify_key	string	the hex encoded key for verification in interactions and the GameSDK's GetTicket
            team	?team object	if the application belongs to a team, this will be a list of the members of that team
            guild_id?	snowflake	if this application is a game sold on Discord, this field will be the guild to which it has been linked
            primary_sku_id?	snowflake	if this application is a game sold on Discord, this field will be the id of the "Game SKU" that is created, if exists
            slug?	string	if this application is a game sold on Discord, this field will be the URL slug that links to the store page
            cover_image?	string	the application's default rich presence invite cover image hash
            flags?	integer	the application's public flags
            tags?	array of strings	up to 5 tags describing the content and functionality of the application
            install_params?	install params object	settings for the application's default in-app authorization link, if enabled
            custom_install_url?	string	the application's default custom authorization link, if enabled""");
    }
    
}
