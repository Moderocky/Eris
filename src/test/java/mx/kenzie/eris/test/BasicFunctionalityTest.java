package mx.kenzie.eris.test;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.network.EntityCache;
import org.junit.Test;

import java.time.Instant;

public class BasicFunctionalityTest extends VerifierTest {
    
    @Test
    public void instantTest() {
        final Instant instant = Instant.now();
        final String timestamp = DiscordAPI.getTimestamp(instant);
        final Instant reverted = DiscordAPI.getInstant(timestamp);
        assert reverted.equals(instant) : "Time conversion was inaccurate.";
    }
    
    @Test
    public void entityCacheTest() {
        final EntityCache cache = bot.getAPI().getCache();
        assert cache != null : "Cache was null.";
    }
    
}
