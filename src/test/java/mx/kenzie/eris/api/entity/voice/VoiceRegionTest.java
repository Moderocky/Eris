package mx.kenzie.eris.api.entity.voice;

import mx.kenzie.eris.test.VerifierTest;
import org.junit.Test;

public class VoiceRegionTest extends VerifierTest {
    
    @Test
    public void test() {
        this.verify(VoiceRegion.class, """
            id	string	unique ID for the region
            name	string	name of the region
            optimal	boolean	true for a single server that is closest to the current user's client
            deprecated	boolean	whether this is a deprecated voice region (avoid switching to these)
            custom	boolean	whether this is a custom voice region (used for events/etc)""");
    }
    
}
