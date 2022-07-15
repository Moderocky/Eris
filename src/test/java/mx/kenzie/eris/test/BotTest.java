package mx.kenzie.eris.test;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.entity.message.ActionRow;
import mx.kenzie.eris.api.entity.message.Button;
import mx.kenzie.eris.api.event.IdentifyGuild;
import mx.kenzie.eris.api.event.Ready;
import mx.kenzie.eris.api.magic.ButtonStyle;
import mx.kenzie.eris.api.magic.Intents;
import mx.kenzie.eris.data.incoming.gateway.Dispatch;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
        try (final Bot bot = new Bot(TOKEN, Intents.MESSAGE_CONTENT, Intents.DIRECT_MESSAGES, Intents.GUILDS)) {
            final DiscordAPI api = bot.getAPI();
            bot.start();
            bot.await();
        
        
        }
    }
    
    
    
}
