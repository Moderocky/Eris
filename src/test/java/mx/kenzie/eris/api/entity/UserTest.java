package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class UserTest extends VerifierTest {

    @Test
    public void test() {
        this.verify(User.class, """
            id	snowflake	the user's id	identify
            username	string	the user's username, not unique across the platform	identify
            discriminator	string	the user's Discord-tag	identify
            global_name	?string	the user's display name, if it is set. For bots, this is the application name	identify
            avatar	?string	the user's avatar hash	identify
            bot?	boolean	whether the user belongs to an OAuth2 application	identify
            system?	boolean	whether the user is an Official Discord System user (part of the urgent message system)	identify
            mfa_enabled?	boolean	whether the user has two factor enabled on their account	identify
            banner?	?string	the user's banner hash	identify
            accent_color?	?integer	the user's banner color encoded as an integer representation of hexadecimal color code	identify
            locale?	string	the user's chosen language option	identify
            verified?	boolean	whether the email on this account has been verified	email
            email?	?string	the user's email	email
            flags?	integer	the flags on a user's account	identify
            premium_type?	integer	the type of Nitro subscription on a user's account	identify
            public_flags?	integer	the public flags on a user's account	identify""");
    }

}
