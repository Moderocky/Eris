package mx.kenzie.eris;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.Expecting;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.Listener;
import mx.kenzie.eris.api.command.CommandHandler;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Self;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.event.*;
import mx.kenzie.eris.api.event.channel.CreateChannel;
import mx.kenzie.eris.api.event.channel.DeleteChannel;
import mx.kenzie.eris.api.event.channel.UpdateChannel;
import mx.kenzie.eris.api.event.channel.UpdateChannelPins;
import mx.kenzie.eris.api.event.guild.*;
import mx.kenzie.eris.api.event.message.ReceiveMessage;
import mx.kenzie.eris.api.event.thread.*;
import mx.kenzie.eris.api.utility.WeakMap;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.incoming.Incoming;
import mx.kenzie.eris.data.incoming.gateway.Dispatch;
import mx.kenzie.eris.data.incoming.gateway.Hello;
import mx.kenzie.eris.data.incoming.gateway.InvalidSession;
import mx.kenzie.eris.data.incoming.gateway.Reconnect;
import mx.kenzie.eris.data.incoming.http.GatewayConnection;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.data.outgoing.gateway.Heartbeat;
import mx.kenzie.eris.data.outgoing.gateway.Identify;
import mx.kenzie.eris.data.outgoing.gateway.Resume;
import mx.kenzie.eris.network.NetworkController;

import java.io.IOException;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Bot extends Lazy implements Runnable, AutoCloseable {
    
    public static final WeakMap<String, Expecting<Interaction>> INLINE_CALLBACKS = new WeakMap<>();
    public static final Map<String, Class<? extends Event>> EVENT_LIST = new HashMap<>();
    public static String API_URL = "https://discord.com/api/v10";
    public static String CDN_URL = "https://cdn.discordapp.com";
    public static boolean DEBUG_MODE = false;
    public static Consumer<Throwable> exceptionHandler;
    
    static {
        EVENT_LIST.put("READY", Ready.class);
        EVENT_LIST.put("RESUMED", Resumed.class);
        EVENT_LIST.put("MESSAGE_CREATE", ReceiveMessage.class);
        EVENT_LIST.put("INTERACTION_CREATE", Interaction.class);
        EVENT_LIST.put("GUILD_JOIN_REQUEST_UPDATE", UpdateJoinRequest.class);
        EVENT_LIST.put("GUILD_CREATE", IdentifyGuild.class);
        EVENT_LIST.put("GUILD_UPDATE", UpdateGuild.class);
        EVENT_LIST.put("GUILD_DELETE", DeleteGuild.class);
        EVENT_LIST.put("GUILD_ROLE_CREATE", CreateGuildRole.class);
        EVENT_LIST.put("GUILD_ROLE_UPDATE", UpdateGuildRole.class);
        EVENT_LIST.put("GUILD_BAN_ADD", AddGuildBan.class);
        EVENT_LIST.put("GUILD_BAN_REMOVE", RemoveGuildBan.class);
        EVENT_LIST.put("GUILD_EMOJIS_UPDATE", UpdateGuildEmojis.class);
        EVENT_LIST.put("GUILD_STICKERS_UPDATE", UpdateGuildStickers.class);
        EVENT_LIST.put("GUILD_INTEGRATIONS_UPDATE", UpdateGuildIntegrations.class);
        EVENT_LIST.put("GUILD_MEMBER_ADD", AddGuildMember.class);
        EVENT_LIST.put("GUILD_MEMBER_UPDATE", UpdateGuildMember.class);
        EVENT_LIST.put("GUILD_MEMBER_REMOVE", RemoveGuildMember.class);
        EVENT_LIST.put("GUILD_MEMBERS_CHUNK", IdentifyGuildMembers.class);
        EVENT_LIST.put("AUTO_MODERATION_RULE_CREATE", CreateModerationRule.class);
        EVENT_LIST.put("AUTO_MODERATION_RULE_UPDATE", UpdateModerationRule.class);
        EVENT_LIST.put("AUTO_MODERATION_RULE_DELETE", DeleteModerationRule.class);
        EVENT_LIST.put("AUTO_MODERATION_ACTION_EXECUTION", ExecuteRule.class);
        EVENT_LIST.put("CHANNEL_CREATE", CreateChannel.class);
        EVENT_LIST.put("CHANNEL_UPDATE", UpdateChannel.class);
        EVENT_LIST.put("CHANNEL_DELETE", DeleteChannel.class);
        EVENT_LIST.put("CHANNEL_PINS_UPDATE", UpdateChannelPins.class);
        EVENT_LIST.put("THREAD_LIST_SYNC", ThreadListSync.class);
        EVENT_LIST.put("THREAD_CREATE", CreateThread.class);
        EVENT_LIST.put("THREAD_UPDATE", UpdateThread.class);
        EVENT_LIST.put("THREAD_DELETE", DeleteThread.class);
        EVENT_LIST.put("THREAD_UPDATE_MEMBER", UpdateThreadMember.class);
        EVENT_LIST.put("THREAD_UPDATE_MEMBERS", UpdateThreadMembers.class);
        EVENT_LIST.put("PRESENCE_UPDATE", UpdatePresence.class);
    }
    
    public final ExecutorService executor = Executors.newCachedThreadPool();
    protected final Map<Listener<?>, Class<? extends Event>> listeners = new HashMap<>();
    protected final Map<Command, CommandHandler> commands = new HashMap<>();
    protected final DiscordAPI api;
    final String token;
    final String[] headers = {"Authorization", null, "User-Agent", "DiscordBot(A, B)"};
    private final Object lock = new Object();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    protected NetworkController network;
    protected int intents;
    protected volatile Self self;
    protected volatile String session;
    private boolean running = true;
    private transient WebSocket socket;
    private CompletableFuture<?> process;
    private ScheduledFuture<?> heartbeat;
    private transient boolean shouldResume = false;
    
    public Bot(String token, int... intents) {
        this.token = token;
        this.headers[1] = "Bot " + token;
        this.network = new NetworkController(API_URL, this);
        this.api = new DiscordAPI(network, this);
        for (int intent : intents) this.intents |= intent;
    }
    
    public String getSessionID() {
        return session;
    }
    
    public Listener<?>[] getListeners(Class<? extends Event> type) {
        final List<Listener<?>> list = new ArrayList<>();
        for (final Map.Entry<Listener<?>, Class<? extends Event>> entry : listeners.entrySet()) {
            final Class<?> value = entry.getValue();
            if (type.isAssignableFrom(value)) list.add(entry.getKey());
        }
        return list.toArray(new Listener[0]);
    }
    
    public void unregisterListener(Listener<?> listener) {
        this.listeners.remove(listener);
        this.network.unregisterListener(listener);
    }
    
    public void registerCommand(Command command, CommandHandler handler) {
        this.registerCommand(command, null, handler);
    }
    
    public <IGuild> Command registerCommand(Command command, IGuild guild, CommandHandler handler) {
        final String id = (guild == null) ? null : api.getGuildId(guild);
        command.guild_id = id;
        this.commands.put(command, handler);
        return this.api.registerCommand(command, id);
    }
    
    public Listener<?>[] getPayloadListeners(Class<? extends Incoming> type) {
        return this.network.getListeners(type);
    }
    
    @Override
    public void close() {
        for (final Command command : commands.keySet()) this.api.deleteCommand(command);
        synchronized (this) {
            this.running = false;
        }
        this.network.close();
        this.process.cancel(true);
        this.executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) this.executor.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Bot.handle(e);
        }
        this.heartbeat.cancel(true);
        this.heartbeat = null;
        this.scheduler.shutdownNow();
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
    public static void handle(Throwable throwable) {
        if (exceptionHandler != null) exceptionHandler.accept(throwable);
        else throwable.printStackTrace();
    }
    
    public void start() {
        if (process != null) return;
        this.process = CompletableFuture.runAsync(this);
    }
    
    public synchronized boolean isRunning() {
        return running;
    }
    
    public DiscordAPI getAPI() {
        return this.api;
    }
    
    @Override
    public void run() {
        try {
            this.registerPayloadListener(Incoming.class, incoming -> incoming.network.notify(incoming.sequence));
            this.registerPayloadListener(Dispatch.class, dispatch -> {
                final Json.JsonHelper helper = dispatch.network.helper;
                if (Bot.DEBUG_MODE) System.out.println("Preparing: " + dispatch.key + " " + dispatch.data.keySet());
                final Class<? extends Event> type = EVENT_LIST.getOrDefault(dispatch.key, Event.Unknown.class);
                final Event event = helper.createObject(type);
                if (event instanceof Entity entity) entity.api = this.api;
                helper.mapToObject(event, type, dispatch.data);
                this.triggerEvent(event);
            });
            this.registerListener(SocketClose.class, close -> {
                if (close.code >= 1000 && close.code < 2000) {
                    if (heartbeat != null) heartbeat.cancel(true);
                    this.heartbeat = null;
                    this.network.sequence.set(0);
                    this.shouldResume = false; // Don't resume for RFC spec. closing codes
                }
                if (close.getReason() == SocketClose.Reason.INVALID_SEQUENCE) {
                    if (heartbeat != null) heartbeat.cancel(true);
                    this.heartbeat = null;
                    this.network.sequence.set(0);
                    this.shouldResume = false;
                    this.connect(true);
                } else if (close.shouldReconnect()) this.connect(true);
            });
            this.registerPayloadListener(Reconnect.class, reconnect -> this.connect(true));
            this.registerPayloadListener(InvalidSession.class, session -> {
                if (DEBUG_MODE) System.out.println("Received Invalid Session (9)");
                /*
                 * We never attempt a resume here, even if the payload suggests one
                 * may be possible — This is done to keep the code simple; keeping track
                 * of when to identify AND resume would result in code spaghetti.
                 * */
                if (heartbeat != null) heartbeat.cancel(true);
                this.heartbeat = null;
                this.shouldResume = false;
                this.network.sequence.set(0);
//                this.connect(true); // connect moved to socket close
            });
            this.registerPayloadListener(Hello.class, hello -> {
                if (shouldResume) {
                    this.resume();
                    return;
                }
                final Identify identify = new Identify();
                identify.data.intents = this.intents;
                identify.data.token = this.token;
                final int delay = hello.data.heartbeat_interval;
                this.dispatch(identify);
                this.heartbeat = scheduler.scheduleWithFixedDelay(() -> {
                    final Heartbeat heartbeat = new Heartbeat();
                    final int sequence = this.network.sequence.getAcquire();
                    heartbeat.data = sequence < 1 ? null : sequence;
                    this.dispatch(heartbeat);
                }, (long) (delay * ThreadLocalRandom.current().nextDouble(0, 1)), delay, TimeUnit.MILLISECONDS);
                this.shouldResume = true;
            });
            this.registerListener(Ready.class, ready -> {
                synchronized (this) {
                    this.self = ready.user;
                    this.session = ready.session_id;
                }
                if (heartbeat == null) throw new Error("No heartbeat monitor set up.");
                this.finish();
            });
            this.registerListener(Interaction.class, interaction -> {
                for (final Map.Entry<Command, CommandHandler> entry : this.commands.entrySet()) {
                    final Command command = entry.getKey();
                    if (interaction.data.type != null && interaction.data.type != command.type) continue;
                    if (!command.name.equals(interaction.data.name)) continue;
                    if (command.guild_id == null || command.guild_id.equals(interaction.guild_id))
                        entry.getValue().on(interaction);
                }
                if (interaction.data.custom_id != null) {
                    final Expecting<Interaction> expecting = Bot.INLINE_CALLBACKS.getValue(interaction.data.custom_id);
                    if (expecting == null) return;
                    Bot.INLINE_CALLBACKS.remove(interaction.data.custom_id);
                    expecting.setResult(interaction);
                    expecting.finish();
                }
            });
            this.connect(false);
            this.scheduler.schedule(this.api::cleanCache, 90, TimeUnit.SECONDS);
            this.scheduler.schedule(Bot.INLINE_CALLBACKS::cleanAsync, 120, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    public <Event extends Incoming> void registerPayloadListener(Class<Event> type, Listener<Event> listener) {
        this.network.registerListener(type, listener);
    }
    
    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    public void triggerEvent(Event event) {
        assert event instanceof Payload : "Event was not a payload.";
        if (Bot.DEBUG_MODE) System.out.println("Event: " + event.getClass().getSimpleName());
        for (final Map.Entry<Listener<?>, Class<? extends Event>> entry : this.listeners.entrySet()) {
            final Class<?> type = entry.getValue();
            if (!type.isInstance(event)) continue;
            final Listener listener = entry.getKey();
            CompletableFuture.runAsync(() -> {
                try {
                    listener.on((Payload) event);
                } catch (Throwable ex) {
                    Bot.handle(ex);
                }
            }, executor);
        }
    }
    
    public <Type extends Payload & Event> void registerListener(Class<Type> type, Listener<Type> listener) {
        this.listeners.put(listener, type);
    }
    
    private void connect(boolean wait) {
        if (wait) try {
            Thread.sleep(5000L); // required pause before reconnect
        } catch (InterruptedException ignored) {
        }
        this.openSocket();
    }
    
    public void resume() {
        final Resume resume = new Resume();
        resume.data.session_id = session;
        resume.data.sequence = this.getSequence();
        this.dispatch(resume);
    }
    
    protected void dispatch(Outgoing payload) {
        this.network.sendPayload(payload);
    }
    
    private void openSocket() {
        try (final Json json = new Json(network.request("GET", "/gateway/bot", null, headers).body())) {
            if (DEBUG_MODE) System.out.println("Opening socket.");
            final GatewayConnection connection = json.toObject(new GatewayConnection());
            this.socket = network.openSocket(connection.url + "/?v=10&encoding=json");
        } catch (IOException | InterruptedException ex) {
            Bot.handle(ex);
        }
    }
    
    public int getSequence() {
        return network.sequence.getAcquire();
    }
    
    @Override
    public String toString() {
        return "Bot " + this.self;
    }
}
