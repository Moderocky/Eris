package mx.kenzie.eris;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.JsonException;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.annotation.Accept;
import mx.kenzie.eris.api.entity.*;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.entity.command.CreateCommand;
import mx.kenzie.eris.api.entity.guild.Ban;
import mx.kenzie.eris.api.entity.guild.ModifyMember;
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
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class DiscordAPI {
    
    private final NetworkController network;
    private final Bot bot;
    private final EntityCache cache = new EntityCache();
    private String application;
    
    DiscordAPI(NetworkController network, Bot bot) {
        this.network = network;
        this.bot = bot;
    }
    
    public CompletableFuture<?> dispatch(Outgoing payload) {
        return this.network.sendPayload(payload);
    }
    
    //<editor-fold desc="Request Helpers" defaultstate="collapsed">
    @SuppressWarnings("all")
    public CompletableFuture<InputStream> request(String type, String path, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.network.request(type, path, body, bot.headers).body();
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException(ex);
            }
        });
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> request(String type, String path, String body, Type object) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<?, ?> map;
            final List<?> result;
            try (final Json json = new CacheJson(this.network.request(type, path, body, bot.headers).body(), cache)) {
                final boolean isMap = json.willBeMap();
                if (isMap) map = json.toMap();
                else {map = null;}
                if (isMap && map.containsKey("code") && map.containsKey("message")) {
                    final APIException error = new APIException(map.get("message") + "");
                    this.network.helper.mapToObject(error, APIException.class, map);
                    throw error;
                }
                if (object == null) return null;
                else if (object instanceof LazyList<?> list && !isMap) list.update(this.network.helper, json.toList(), this);
                else if (object instanceof List list && !isMap) json.toList(list);
                else if (object instanceof Map source && isMap) source.putAll(map);
                else if (isMap) this.network.helper.mapToObject(object, object.getClass(), map);
                return object;
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException("Error in request.", ex);
            } catch (JsonException ignored) {
                return object;
            }
        }).exceptionally(throwable -> {
            if (throwable instanceof CompletionException ex) throwable = ex.getCause();
            if (object instanceof Lazy lazy) lazy.error(throwable);
            else Bot.handle(throwable);
            return object;
        });
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> get(String path, Type object) {
        return this.request("GET", path, null, object);
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> get(String path, Map<?, ?> query, Type object) {
        if (query != null && !query.isEmpty()) {
            final List<String> parts = new ArrayList<>();
            for (final Map.Entry<?, ?> entry : query.entrySet()) parts.add(entry.getKey() + "=" + entry.getValue());
            path += "?" + String.join("&", parts);
        }
        return this.request("GET", path, null, object);
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> patch(String path, String body, Type object) {
        return this.request("PATCH", path, body, object);
    }
    
    public CompletableFuture<Void> delete(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try (final InputStream stream = this.network.delete(path, bot.headers).body()) {
                return null;
            } catch (IOException | InterruptedException ex) {
                throw new DiscordException(ex);
            }
        });
    }
    
    @SuppressWarnings("all")
    public <Type> CompletableFuture<Type> post(String path, String body, Type object) {
        return this.request("POST", path, body, object);
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
        channel.id = id;
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
        final User user = cache.getOrUse(id, new User());
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
    
    public <IGuild> LazyList<Role> getRoles(IGuild guild) {
        final LazyList<Role> roles = new LazyList<>(Role.class, new ArrayList<>());
        if (guild instanceof Guild g) g.api = this;
        this.get("/guilds/" + guild + "/roles", roles);
        return roles;
    }
    
    public <IGuild> LazyList<Role> updateRoles(IGuild guild, LazyList<Role> roles) {
        roles.unready();
        this.get("/guilds/" + guild + "/roles", roles);
        return roles;
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
    
    public <IGuild, IUser> Member modifyMember(IGuild guild, IUser user, ModifyMember member) {
        final String gid = this.getGuildId(guild), uid = this.getUserId(user);
        final Member result;
        if (member instanceof Member value) result = value;
        else result = new Member();
        this.patch("/guilds/" + gid + "/members/" + uid, Json.toJson(member, ModifyMember.class, null), result).thenAccept(Lazy::finish);
        return result;
    }
    
    public void update(Member member) {
        if (!member.isValid()) throw new DiscordException("Unable to update member - user ID unknown.");
        if (member.guild_id == null) throw new DiscordException("Unable to update member - guild ID unknown.");
        member.unready();
        member.api = this;
        this.get("/guilds/" + member.guild_id + "/members/" + member.user.id, member).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
    //<editor-fold desc="Bans" defaultstate="collapsed">
    public <IGuild, IUser> Ban getBan(IGuild guild, IUser user) {
        final String gid = this.getGuildId(guild), uid = this.getUserId(user);
        final Ban ban = new Ban();
        this.get("/guilds/" + gid + "/bans/" + uid, ban).thenAccept(Lazy::finish);
        return ban;
    }
    
    public <IGuild, IUser> void createBan(IGuild guild, IUser user, Ban ban) {
        final String gid = this.getGuildId(guild), uid = this.getUserId(user);
        this.request("PUT", "/guilds/" + gid + "/bans/" + uid, Json.toJson(ban), null).thenRun(ban::finish);
    }
    
    public <
        @Accept({long.class, String.class, Guild.class}) IGuild,
        @Accept({long.class, String.class, User.class}) IUser
        > void removeBan(IGuild guild, IUser user) {
        final String gid = this.getGuildId(guild), uid = this.getUserId(user);
        this.request("DELETE", "/guilds/" + gid + "/bans/" + uid, null, null);
    }
    
    
    //</editor-fold>
    
    public void update(Guild guild) {
        guild.unready();
        this.cache.store(guild);
        guild.api = this;
        this.get("/guilds/" + guild.id, guild).thenAccept(Lazy::finish);
    }
    //</editor-fold>
    
    //<editor-fold desc="Interactions" defaultstate="collapsed">
    public Command registerCommand(Command command) {
        return this.registerCommand(command, (String) null);
    }
    public <IGuild> Command registerCommand(Command command, IGuild guild) {
        final String id = bot.self.id;
        final String body = Json.toJson(command, CreateCommand.class, null);
        command.api = this;
        command.unready();
        if (guild == null) this.post("/applications/" + id + "/commands", body, command).thenAccept(Lazy::finish);
        else this.post("/applications/" + id + "/guilds/" + this.getGuildId(guild) + "/commands", body, command)
            .thenAccept(Lazy::finish);
        return command;
    }
    
    public void deleteCommand(Command command) {
        this.deleteCommand(command.id, command.guild_id);
    }
    
    public <ICommand, IGuild> void deleteCommand(ICommand command, IGuild guild) {
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
    
    //<editor-fold desc="Helpers" defaultstate="collapsed">
    public String getUserId(Object object) {
        if (object instanceof String value) return value;
        if (object instanceof User value) return value.id;
        if (object instanceof Member value) return value.user.id;
        return Objects.toString(object);
    }
    
    public String getGuildId(Object object) {
        if (object instanceof String value) return value;
        if (object instanceof Guild value) return value.id;
        if (object instanceof Guild.Preview value) return value.id;
        return Objects.toString(object);
    }
    
    @SuppressWarnings("unchecked")
    public <Type> Type getLocal(String id, Class<Type> expected) {
        final Snowflake snowflake = cache.get(id);
        if (expected.isInstance(snowflake)) return (Type) snowflake;
        else return null;
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Entity> Type makeEntity(Type template, Map<String, Object> data) {
        this.cache.helper.mapToObject(template, template.getClass(), data);
        if (!(template instanceof Snowflake snowflake)) return template;
        if (!this.shouldCache(template)) return template;
        final Snowflake cached = cache.get(snowflake.id);
        if (cached != null) {
            this.cache.helper.mapToObject(cached, cached.getClass(), data);
            return (Type) cached;
        }
        this.cache.store(snowflake);
        return template;
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Entity> Type makeEntity(Type template) {
        if (!(template instanceof Snowflake snowflake)) return template;
        if (!this.shouldCache(template)) return template;
        final Snowflake cached = cache.get(snowflake.id);
        if (cached != null) return (Type) cached;
        this.cache.store(snowflake);
        return template;
        
    }
    
    @SuppressWarnings("unchecked")
    public <Type> Type getRemote(String id, Class<Type> expected) {
        if (expected == Guild.class) return (Type) this.getGuild(id);
        if (expected == Channel.class) return (Type) this.getChannel(id);
        if (expected == User.class) return (Type) this.getUser(id);
        if (expected == Guild.Preview.class) return (Type) this.getGuildPreview(id);
        else return null;
    }
    
    public void cleanCache() {
        this.cache.clean();
    }
    
    protected boolean shouldCache(Object entity) {
        return (entity instanceof User || entity instanceof Guild || entity instanceof Channel);
    }
    
    public String getApplicationID() {
        if (application == null) {
            bot.await();
            this.application = bot.self.id;
        }
        return application;
    }
    
    public static DiscordException unlinkedEntity(Entity entity) {
        return new DiscordException("The object " + entity.debugName() + " is not linked to a DiscordAPI.");
    }
    //</editor-fold>
    
}
