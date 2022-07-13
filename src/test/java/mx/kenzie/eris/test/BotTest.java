package mx.kenzie.eris.test;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.event.IdentifyGuild;
import mx.kenzie.eris.api.event.Ready;
import mx.kenzie.eris.api.magic.Intents;
import mx.kenzie.eris.data.incoming.gateway.Dispatch;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class BotTest {
    
    private static final String TOKEN;
    static {
        final InputStream stream = BotTest.class.getClassLoader().getResourceAsStream("token.json");
        assert stream != null;
        try (final Json json = new Json(stream)) {
            TOKEN = json.toArray(new String[0])[0];
        }
    }
    
    public static void main(String[] args) throws Throwable {
        try (final Bot bot = new Bot(TOKEN, Intents.MESSAGE_CONTENT, Intents.DIRECT_MESSAGES, Intents.GUILDS)) {
            final DiscordAPI api = bot.getAPI();
            bot.registerListener(IdentifyGuild.class, guild -> {
                System.out.println("Loaded guild '" + guild.name + "'");
                System.out.println("This guild has " + guild.member_count + " members!");
            });
            bot.registerPayloadListener(Dispatch.class, dispatch -> {
                System.out.println("Event: " +  dispatch.key);
            });
            bot.start();
            bot.await();
            final User user = api.getUser(196709350469795841L);
            
            
            final Channel channel = api.createDirectMessage(196709350469795841L);
            final Message message = new Message();
            channel.await();
            message.content = "Hello there!";
            api.sendMessage(channel, message);
            
            
            while (bot.isRunning()) Thread.sleep(1000L);
        }
    }
    
    public void identify() {
        
        try (final Bot bot = new Bot(TOKEN)) {
            bot.registerListener(IdentifyGuild.class, guild -> {
                System.out.println("Loaded guild '" + guild.name + "'");
                System.out.println("This guild has " + guild.member_count + " members!");
            });
        }
        
    }
    
    public void message() {
        
        try (final Bot bot = new Bot(TOKEN)) {
            
            final DiscordAPI api = bot.getAPI();
            
            final Channel channel = api.createDirectMessage("196709350469795841");
            
            final Message message = new Message();
            message.content = "Hello there!";
            api.sendMessage(channel, message);
        }
        
    }
    
    
    
}
