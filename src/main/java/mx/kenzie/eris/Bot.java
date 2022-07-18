package mx.kenzie.eris;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.Listener;
import mx.kenzie.eris.api.command.CommandHandler;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.Self;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.event.IdentifyGuild;
import mx.kenzie.eris.api.event.Interaction;
import mx.kenzie.eris.api.event.Ready;
import mx.kenzie.eris.api.event.ReceiveMessage;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.incoming.Incoming;
import mx.kenzie.eris.data.incoming.gateway.Dispatch;
import mx.kenzie.eris.data.incoming.gateway.Hello;
import mx.kenzie.eris.data.incoming.http.GatewayConnection;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.data.outgoing.gateway.Heartbeat;
import mx.kenzie.eris.data.outgoing.gateway.Identify;
import mx.kenzie.eris.network.NetworkController;

import java.io.InputStream;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Bot extends Lazy implements Runnable, AutoCloseable {
    
    public static String API_URL = "https://discord.com/api/v10";
    public static String CDN_URL = "https://cdn.discordapp.com";
    public static final Map<String, Class<? extends Event>> EVENT_LIST = new HashMap<>();
    
    public static Consumer<Throwable> exceptionHandler;
    
    static {
        EVENT_LIST.put("READY", Ready.class);
        EVENT_LIST.put("GUILD_CREATE", IdentifyGuild.class);
        EVENT_LIST.put("MESSAGE_CREATE", ReceiveMessage.class);
        EVENT_LIST.put("INTERACTION_CREATE", Interaction.class);
    }
    
    final String token;
    final String[] headers = {"Authorization", null, "User-Agent", "DiscordBot(A, B)", "Content-Type", "application/json"};
    public final Executor executor = Executors.newCachedThreadPool();
    protected final NetworkController network;
    private volatile boolean running = true;
    private WebSocket socket;
    private final Object lock = new Object();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    protected final Map<Listener<?>, Class<? extends Event>> listeners = new HashMap<>();
    protected final Map<Command, CommandHandler> commands = new HashMap<>();
    protected int intents;
    
    protected volatile Self self;
    protected volatile String session;
    protected final DiscordAPI api;
    private CompletableFuture<?> process;
    
    public Bot(String token, int... intents) {
        this.token = token;
        this.headers[1] = "Bot " + token;
        this.network = new NetworkController(API_URL, this);
        this.api = new DiscordAPI(network, this);
        for (int intent : intents) this.intents |= intent;
    }
    
    public <Type extends Payload & Event> void registerListener(Class<Type> type, Listener<Type> listener) {
        this.listeners.put(listener, type);
    }
    
    public Listener<?>[] getListeners(Class<? extends Event> type) {
        final List<Listener<?>> list = new ArrayList<>();
        for (final Map.Entry<Listener<?>, Class<? extends Event>> entry : listeners.entrySet()) {
            final Class<?> value = entry.getValue();
            if (type.isAssignableFrom(value)) list.add(entry.getKey());
        }
        return list.toArray(new Listener[0]);
    }
    
    public <Event extends Incoming> void registerPayloadListener(Class<Event> type, Listener<Event> listener) {
        this.network.registerListener(type, listener);
    }
    
    public void unregisterListener(Listener<?> listener) {
        this.listeners.remove(listener);
        this.network.unregisterListener(listener);
    }
    
    public <IGuild> void registerCommand(Command command, IGuild guild, CommandHandler handler) {
        final String id;
        if (guild == null) id = null;
        else if (guild instanceof Guild value) id = value.id;
        else if (guild instanceof String value) id = value;
        else id = guild.toString();
        command.guild_id = id;
        this.api.registerCommand(command, id);
        this.commands.put(command, handler);
    }
    
    public Listener<?>[] getPayloadListeners(Class<? extends Incoming> type) {
        return this.network.getListeners(type);
    }
    
    protected void dispatch(Outgoing payload) {
        this.network.sendPayload(payload);
    }
    
    @Override
    public void close() {
        for (final Command command : commands.keySet()) this.api.deleteCommand(command);
        this.running = false;
        this.network.codes.clear();
        this.network.listeners.clear();
        this.process.cancel(true);
//        this.socket.sendClose(100, "Shutting down.");
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    public void triggerEvent(Event event) {
        assert event instanceof Payload : "Event was not a payload.";
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
            final InputStream stream = network.get("/gateway/bot", headers).body();
            this.registerPayloadListener(Incoming.class, incoming -> incoming.network.notify(incoming.sequence));
            this.registerPayloadListener(Dispatch.class, dispatch -> {
                final Json.JsonHelper helper = dispatch.network.helper;
                final Class<? extends Event> type = EVENT_LIST.getOrDefault(dispatch.key, Event.Unknown.class);
                final Event event = helper.createObject(type);
                if (event instanceof Entity entity) entity.api = this.api;
                helper.mapToObject(event, type, dispatch.data);
                this.triggerEvent(event);
            });
            this.registerPayloadListener(Hello.class, hello -> {
                final Identify identify = new Identify();
                identify.data.intents = this.intents;
                identify.data.token = this.token;
                final int delay = hello.data.heartbeat_interval;
                this.dispatch(identify);
                this.scheduler.scheduleWithFixedDelay(() -> {
                    final Heartbeat heartbeat = new Heartbeat();
                    final int sequence = this.network.sequence.getAcquire();
                    heartbeat.data = sequence < 1 ? null : sequence;
                    this.dispatch(heartbeat);
                }, (long) delay * ThreadLocalRandom.current().nextInt(0, 1), delay, TimeUnit.MILLISECONDS);
            });
            this.registerListener(Ready.class, ready -> {
                synchronized (this) {
                    this.self = ready.user;
                    this.session = ready.session_id;
                }
                this.finish();
            });
            this.registerListener(Interaction.class, interaction -> {
                for (final Map.Entry<Command, CommandHandler> entry : this.commands.entrySet()) {
                    final Command command = entry.getKey();
                    if (!command.name.equals(interaction.data.name)) continue;
                    entry.getValue().on(interaction);
                }
            });
            try (final Json json = new Json(stream)) {
                final GatewayConnection connection = json.toObject(new GatewayConnection());
                this.socket = network.openSocket(connection.url + "/?v=10&encoding=json");
            }
            this.scheduler.schedule(this.api::cleanCache, 90, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return "Bot " + this.self;
    }
    
    public static void handle(Throwable throwable) {
        if (exceptionHandler != null) exceptionHandler.accept(throwable);
        else throwable.printStackTrace();
    }
}
