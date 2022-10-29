package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class BanTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(Ban.class, """
            reason	?string	the reason for the ban
            user	user object	the banned user""");
    }
    
}
