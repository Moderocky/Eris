package mx.kenzie.eris.test;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.api.magic.Intents;
import mx.kenzie.eris.api.utility.LazyList;

import java.io.InputStream;
import java.lang.Thread;
import java.util.Objects;

public class BotTest {
    
    private static final String TOKEN;
    
    static {
        final InputStream stream = BotTest.class.getClassLoader().getResourceAsStream("token.json");
        assert stream != null;
        try (final Json json = new Json(stream)) {
            final String[] tokens = json.toArray(new String[0]);
            TOKEN = tokens[0];
        }
    }
    
    public static void main(String[] args) throws Throwable {
        final Bot bot = new Bot(TOKEN, Intents.MESSAGE_CONTENT, Intents.DIRECT_MESSAGES, Intents.GUILDS, Intents.GUILD_BANS, Intents.GUILD_MEMBERS);
        final DiscordAPI api = bot.getAPI();
        bot.start();
        bot.await();
        
        
        Thread.sleep(1000);
        bot.close();
        System.exit(0);
    }
    
    
}
