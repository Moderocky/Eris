package mx.kenzie.eris;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.JsonException;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.entity.command.CreateCommand;
import mx.kenzie.eris.api.entity.message.UnsentMessage;
import mx.kenzie.eris.api.event.Interaction;
import mx.kenzie.eris.api.utility.LazyList;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.error.APIException;
import mx.kenzie.eris.error.DiscordException;
import mx.kenzie.eris.network.CacheJson;
import mx.kenzie.eris.network.EntityCache;
import mx.kenzie.eris.network.NetworkController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DiscordAPI {
    
    private final NetworkController network;
    private final Bot bot;
    private final EntityCache cache = new EntityCache();
    
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
            try (final Json json = new CacheJson(this.network.get(path, bot.headers).body(), cache)) {
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
            try (final Json json = new CacheJson(this.network.patch(path, body, bot.headers).body(), cache)) {
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
    
    public CompletableFuture<Void> delete(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Json json = new Json(this.network.delete(path, bot.headers).body())) {
                return null;
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException(ex);
            }
        });
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> post(String path, String body, Type object) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<String, Object> map = new HashMap<>();
            try (final Json json = new CacheJson(this.network.post(path, body, bot.headers).body(), cache)) {
                json.toMap(map);
                if (map.containsKey("code") && map.containsKey("message")) {
                    System.out.println(path); // todo
                    System.out.println(map); // todo
                    System.out.println(body); // todo
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
            } catch (JsonException ignored) {
                return object;
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return object;
        });
    }
    //</editor-fold>
    
    //<editor-fold desc="Messages" defaultstate="collapsed">
    public Message write(String content) {
        final Message message = new Message();
        message.api = this;
        message.finish();
        return message;
    }
    
    public Message sendMessage(Channel channel, Message message) {
        if (channel.api == null) channel.api = this;
        return this.sendMessage(channel.id, message);
    }
    
    public Message sendMessage(long channel, Message message) {
        return this.sendMessage(Long.toString(channel), message);
    }
    
    public Message sendMessage(String channel, Message message) {
        final String body = Json.toJson(message, UnsentMessage.class, null);
        message.unready();
        this.post("/channels/" + channel + "/messages", body, message).thenAccept(Lazy::finish);
        if (message.api == null) message.api = this;
        return message;
    }
    //</editor-fold>
    
    //<editor-fold desc="Channels" defaultstate="collapsed">
    public Channel createDirectChannel(long id) {
        return this.createDirectChannel(Long.toString(id));
    }
    
    public Channel createDirectChannel(String id) {
        final Channel channel = cache.getOrUse(id, new Channel());
        channel.api = this;
        this.post("/users/@me/channels", "{\"recipient_id\":" + id + "}", channel).thenAccept(Lazy::finish);
        return channel;
    }
    
    public Channel getChannel(long id) {
        return this.getChannel(Long.toString(id));
    }
    
    public Channel getChannel(String id) {
        final Channel channel = cache.getOrUse(id, new Channel());
        channel.api = this;
        cache.store(channel);
        this.get("/channels/" + id, channel).thenAccept(Lazy::finish);
        return channel;
    }
    //</editor-fold>
    
    //<editor-fold desc="Users" defaultstate="collapsed">
    public Self getSelf() {
        assert bot.session != null : "Bot has not connected";
        return bot.self;
    }
    
    public User getUser(long id) {
        return this.getUser(Long.toString(id));
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
        cache.store(user);
        user.api = this;
        if (user instanceof Self self) this.get("/users/@me", self).thenAccept(Lazy::finish);
        else this.get("/users/" + user.id, user).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
    //<editor-fold desc="Guilds" defaultstate="collapsed">
    public Guild getGuild(long id) {
        return this.getGuild(Long.toString(id));
    }
    
    public Guild getGuild(String id) {
        final Guild guild = cache.getOrUse(id, new Guild());
        guild.id = id;
        guild.api = this;
        this.get("/guilds/" + id, guild).thenAccept(Lazy::finish);
        return guild;
    }
    
    public Guild.Preview getGuildPreview(long id) {
        return this.getGuildPreview(Long.toString(id));
    }
    
    public Guild.Preview getGuildPreview(String id) {
        final Guild.Preview preview = new Guild.Preview();
        preview.id = id;
        preview.api = this;
        this.get("/guilds/" + id + "/preview", preview).thenAccept(Lazy::finish);
        return preview;
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
    
    //<editor-fold desc="Members" defaultstate="collapsed">
    public Member getMember(long guild, long user) {
        return this.getMember(Long.toString(guild), Long.toString(user));
    }
    
    public Member getMember(String guild, String user) {
        final Member member = new Member();
        member.guild_id = guild;
        member.user.id = user;
        member.api = this;
        this.get("/guilds/" + guild + "/members/" + user, member);
        return member;
    }
    
    public Member getMember(Guild guild, User user) {
        return this.getMember(guild.id, user);
    }
    
    public Member getMember(long guild, User user) {
        return this.getMember(Long.toString(guild), user);
    }
    
    public Member getMember(String guild, User user) {
        final Member member = new Member(); // don't cache members due to the ID overload
        member.user = user;
        member.guild_id = guild;
        member.api = this;
        this.get("/guilds/" + guild + "/members/" + user.id, member);
        return member;
    }
    
    public void update(Member member) {
        if (!member.isValid()) throw new DiscordException("Unable to update member - user ID unknown.");
        if (member.guild_id == null) throw new DiscordException("Unable to update member - guild ID unknown.");
        member.unready();
        member.api = this;
        this.get("/guilds/" + member.guild_id + "/members/" + member.user.id, member).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
    public void update(Guild guild) {
        guild.unready();
        cache.store(guild);
        guild.api = this;
        this.get("/guilds/" + guild.id, guild).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
    //<editor-fold desc="Interactions" defaultstate="collapsed">
    public Command registerCommand(Command command) {
        return this.registerCommand(command, (String) null);
    }
    
    public Command registerCommand(Command command, Guild guild) {
        return this.registerCommand(command, guild.id);
    }
    
    public Command registerCommand(Command command, long guild) {
        return this.registerCommand(command, Long.toString(guild));
    }
    
    public Command registerCommand(Command command, String guild) {
        final String id = bot.self.id;
        final String body = Json.toJson(command, CreateCommand.class, null);
        command.unready();
        if (guild == null) this.post("/applications/" + id + "/commands", body, command).thenAccept(Lazy::finish);
        else
            this.post("/applications/" + id + "/guilds/" + guild + "/commands", body, command).thenAccept(Lazy::finish);
        return command;
    }
    
    public void deleteCommand(Command command) {
        this.deleteCommand(command.id, command.guild_id);
    }
    
    public void deleteCommand(long command, long guild) {
        this.deleteCommand(Long.toString(command), Long.toString(guild));
    }
    
    public void deleteCommand(String command, String guild) {
        final String id = bot.self.id;
        if (guild == null) this.delete("/applications/" + id + "/commands/" + command);
        else this.delete("/applications/" + id + "/guilds/" + guild + "/commands/" + command);
    }
    
    public void interactionResponse(Interaction interaction, Interaction.Response response) {
        final String body = Json.toJson(response);
        if (response.data() instanceof Lazy lazy)
            this.post("/interactions/" + interaction.id + "/" + interaction.token + "/callback", body, lazy)
                .thenAccept(Lazy::finish);
        else this.post("/interactions/" + interaction.id + "/" + interaction.token + "/callback", body, null);
    }
    //</editor-fold>
    
    @SuppressWarnings("unchecked")
    public <Type> Type getLocal(String id, Class<Type> expected) {
        final Snowflake snowflake = cache.get(id);
        if (expected.isInstance(snowflake)) return (Type) snowflake;
        else return null;
    }
    
    @SuppressWarnings("unchecked")
    public <Type> Type getRemote(String id, Class<Type> expected) {
        if (expected == Guild.class) return (Type) this.getGuild(id);
        if (expected == Channel.class) return (Type) this.getChannel(id);
        if (expected == User.class) return (Type) this.getUser(id);
        if (expected == Guild.Preview.class) return (Type) this.getGuildPreview(id);
        else return null;
    }
    
    public static DiscordException unlinkedEntity(Entity entity) {
        return new DiscordException("The object " + entity.debugName() + " is not linked to a DiscordAPI.");
    }
    
}
