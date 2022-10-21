package mx.kenzie.eris.test;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.api.magic.Intents;
import mx.kenzie.eris.network.EntityCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;

public class BasicFunctionalityTest {
    
    private static final String TOKEN;
    static Bot bot;
    static DiscordAPI api;
    static Guild guild;
    static Channel channel;
    
    static {
        final InputStream stream = BasicFunctionalityTest.class.getClassLoader().getResourceAsStream("token.json");
        assert stream != null;
        try (final Json json = new Json(stream)) {
            final String[] tokens = json.toArray(new String[0]);
            TOKEN = tokens[0];
        }
    }
    
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
    
    @AfterClass
    public static void stop() {
        bot.close();
    }
    
    @Test
    public void acquireEntities() {
        final Guild guild = api.getGuild(399248280300683275L);
        assert guild.successful();
        final Channel channel = api.getChannel(1001024258140540938L);
        assert channel.successful();
        final Message message = channel.getMessage(1001025353273327686L);
        assert message.successful() : message.error().getMessage();
    }
    
    @Test
    public void retrieveMessage() {
        final Message message = channel.getMessage(1001025353273327686L);
        assert message.successful() : message.error().getMessage();
        assert message.content.equals("message");
        assert message.author.id.equals("196709350469795841");
    }
    
    @Test
    public void sendDeleteMessage() {
        final Channel channel = api.getChannel(1001024258140540938L);
        final Message message = channel.send(new Message("test"));
        message.await();
        assert message.successful() : message.error().getMessage();
        message.delete();
        message.await();
        assert message.successful() : message.error().getMessage();
    }
    
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
    
    @Test
    public void writeTest() {
        final Message message = bot.getAPI().write("hello there");
        assert message != null;
        assert message.successful() : message.error().getMessage();
    }
    
    @Test
    public void sendMessageTest() {
        final Message message = new Message("hello there");
        message.addAttachment("test.txt", "test file.");
        api.sendMessage(channel, message);
        message.await();
        assert message.successful() : message.error().getMessage();
        assert message.content.equals("hello there");
        message.content = "general kenobi";
        message.edit();
        message.await();
        assert message.successful() : message.error().getMessage();
        message.delete();
        message.await();
        assert message.successful() : message.error().getMessage();
    }
    
    @Test
    public void guildPreviewTest() {
        final Guild.Preview preview = api.getGuildPreview(guild.id());
        preview.await();
        assert preview.successful(): channel.error();
        assert preview.approximate_member_count > 0;
        assert preview.approximate_presence_count > 0;
        assert preview.name != null;
        assert preview.icon != null;
        assert preview.splash == null;
        assert preview.discovery_splash == null;
        assert preview.features.length == 0 : Arrays.toString(preview.features);
        assert preview.emojis.length == 0 : Arrays.toString(preview.emojis);
        assert preview.stickers.length == 0 : Arrays.toString(preview.stickers);
    }
    
    @Test
    public void createChannelTest() {
        final Channel channel = new Channel();
        channel.type = ChannelType.GUILD_VOICE;
        channel.name = "Test Channel";
        guild.createChannel(channel);
        channel.await();
        assert channel.successful(): channel.error();
        assert channel.type == ChannelType.GUILD_VOICE;
        assert channel.name.equals("Test Channel");
        channel.delete();
        channel.await();
    }
    
}
