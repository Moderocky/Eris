package mx.kenzie.eris;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.utility.LazyList;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.error.APIException;
import mx.kenzie.eris.error.DiscordException;
import mx.kenzie.eris.http.NetworkController;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DiscordAPI {
    
    private final NetworkController network;
    private final Bot bot;
    
    DiscordAPI(NetworkController network, Bot bot) {
        this.network = network;
        this.bot = bot;
    }
    
    public CompletableFuture<?> dispatch(Outgoing payload) {
        return this.network.sendPayload(payload);
    }
    
    //<editor-fold desc="Request Helpers" defaultstate="collapsed">
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> get(String path, Type object) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Json json = new Json(this.network.get(path, bot.headers).body())) {
                if (object == null) return null;
                else if (object instanceof List list) list.addAll(json.toList());
                else if (object instanceof Map map) map.putAll(json.toMap());
                else json.toObject(object);
                return object;
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException(ex);
            }
        });
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> patch(String path, String body, Type object) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Json json = new Json(this.network.patch(path, body, bot.headers).body())) {
                if (object == null) return null;
                else if (object instanceof List list) list.addAll(json.toList());
                else if (object instanceof Map map) map.putAll(json.toMap());
                else json.toObject(object);
                return object;
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException(ex);
            }
        });
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> post(String path, String body, Type object) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<String, Object> map = new HashMap<>();
            try (final Json json = new Json(this.network.post(path, body, bot.headers).body())) {
                json.toMap(map);
                if (map.containsKey("code") && map.containsKey("message")) {
                    final APIException error = new APIException(map.get("message") + "");
                    this.network.helper.mapToObject(error, APIException.class, map);
                    throw error;
                }
                if (object == null) return null;
                else if (object instanceof Map result) result.putAll(json.toMap());
                else this.network.helper.mapToObject(object, object.getClass(), map);
                return object;
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException("Error in request:\n" + Json.toJson(map), ex);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return object;
        });
    }
    //</editor-fold>
    
    public Message write(String content) {
        final Message message = new Message();
        message.api = this;
        return message;
    }
    
    public Message sendMessage(Channel channel, Message message) {
        channel.await();
        final String body = Json.toJson(message, UnsentMessage.class, null);
        message.unready();
        channel.api = this;
        message.api = this;
        this.post("/channels/" + channel.id + "/messages", body, message).thenAccept(Lazy::finish);
        message.finish();
        return message;
    }
    
    public Channel createDirectMessage(long id) {
        final Channel channel = new Channel();
        channel.api = this;
        this.post("/users/@me/channels", "{\"recipient_id\":" + id + "}", channel).thenAccept(Lazy::finish);
        return channel;
    }
    
    public Channel createDirectMessage(String id) {
        final Channel channel = new Channel();
        channel.api = this;
        this.post("/users/@me/channels", "{\"recipient_id\":" + id + "}", channel).thenAccept(Lazy::finish);
        return channel;
    }
    
    //<editor-fold desc="Users" defaultstate="collapsed">
    public Self getSelf() {
        assert bot.session != null: "Bot has not connected";
        return bot.self;
    }
    
    public User getUser(long id) {
        final User user = new User();
        user.api = this;
        user.id = id + "";
        this.get("/users/" + id, user).thenAccept(Lazy::finish);
        return user;
    }
    
    public User getUser(String id) {
        final User user = new User();
        user.api = this;
        user.id = id;
        this.get("/users/" + id, user).thenAccept(Lazy::finish);
        return user;
    }
    
    public void update(User user) {
        user.unready();
        user.api = this;
        if (user instanceof Self self) this.get("/users/@me", self).thenAccept(Lazy::finish);
        else this.get("/users/" + user.id, user).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
    //<editor-fold desc="Guilds" defaultstate="collapsed">
    public Guild getGuild(long id) {
        final Guild guild = new Guild();
        guild.api = this;
        this.get("/guilds/" + id, guild).thenAccept(Lazy::finish);
        return guild;
    }
    
    public Guild getGuild(String id) {
        final Guild guild = new Guild();
        guild.api = this;
        this.get("/guilds/" + id, guild).thenAccept(Lazy::finish);
        return guild;
    }
    
    public Guild getGuildPreview(String id) { // todo
        final Guild guild = new Guild();
        guild.api = this;
        this.get("/guilds/" + id + "/preview", guild).thenAccept(Lazy::finish);
        return guild;
    }
    
    public LazyList<Channel> getChannels(Guild guild) {
        final List<Object> data = new ArrayList<>();
        final List<Channel> backer = new ArrayList<>();
        final LazyList<Channel> channels = new LazyList<>(Channel.class, backer);
        guild.api = this;
        this.get("/guilds/" + guild.id + "/channels", data).thenAccept(list -> {
            final Json.JsonHelper helper = bot.network.helper;
            for (final Object o : list) {
                final Channel channel = new Channel();
                backer.add(channel);
                channel.api = this;
                helper.mapToObject(channel, Channel.class, (Map<?, ?>) o);
            }
            channels.finish();
        });
        return channels;
    }
    
    public void update(Guild guild) {
        guild.unready();
        guild.api = this;
        this.get("/guilds/" + guild.id, guild).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
}
