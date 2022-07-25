package mx.kenzie.eris.test;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.magic.Intents;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;

public class BasicFunctionalityTest {
    
    private static final String TOKEN;
    
    static {
        final InputStream stream = BasicFunctionalityTest.class.getClassLoader().getResourceAsStream("token.json");
        assert stream != null;
        try (final Json json = new Json(stream)) {
            final String[] tokens = json.toArray(new String[0]);
            TOKEN = tokens[0];
        }
    }
    
    static Bot bot;
    static DiscordAPI api;
    static Guild guild;
    static Channel channel;
    @BeforeClass
    public static void start() {
        bot = new Bot(TOKEN, Intents.MESSAGE_CONTENT, Intents.DIRECT_MESSAGES, Intents.GUILDS, Intents.GUILD_BANS, Intents.GUILD_MEMBERS);
        api = bot.getAPI();
        bot.start();
        bot.await();
        guild = api.getGuild(399248280300683275L);
        channel = api.getChannel(1001024258140540938L);
        guild.await();
        channel.await();
    }
    
    @Test
    public void acquireEntities() {
        final Guild guild = api.getGuild(399248280300683275L);
        assert guild.successful();
        final Channel channel = api.getChannel(1001024258140540938L);
        assert channel.successful();
        final Message message = channel.getMessage(1001025353273327686L);
        assert message.successful();
    }
    
    @Test
    public void retrieveMessage() {
        final Message message = channel.getMessage(1001025353273327686L);
        assert message.successful();
        assert message.content.equals("message");
        assert message.author.id.equals("196709350469795841");
    }
    
    @Test
    public void sendDeleteMessage() {
        final Channel channel = api.getChannel(1001024258140540938L);
        final Message message = channel.send(new Message("test"));
        message.await();
        assert message.successful();
        message.delete();
        message.await();
        assert message.successful();
    }
    
    
    @AfterClass
    public static void stop() {
        bot.close();
    }
    
    
}
