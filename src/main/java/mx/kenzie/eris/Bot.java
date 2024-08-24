package mx.kenzie.eris;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.Listener;
import mx.kenzie.eris.api.command.CommandHandler;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Self;
import mx.kenzie.eris.api.entity.command.Command;
import mx.kenzie.eris.api.event.*;
import mx.kenzie.eris.api.event.channel.*;
import mx.kenzie.eris.api.event.entitlement.CreateEntitlement;
import mx.kenzie.eris.api.event.entitlement.DeleteEntitlement;
import mx.kenzie.eris.api.event.entitlement.UpdateEntitlement;
import mx.kenzie.eris.api.event.guild.*;
import mx.kenzie.eris.api.event.guild.ban.AddGuildBan;
import mx.kenzie.eris.api.event.guild.ban.RemoveGuildBan;
import mx.kenzie.eris.api.event.guild.event.*;
import mx.kenzie.eris.api.event.guild.invite.CreateInvite;
import mx.kenzie.eris.api.event.guild.invite.DeleteInvite;
import mx.kenzie.eris.api.event.guild.member.AddGuildMember;
import mx.kenzie.eris.api.event.guild.member.IdentifyGuildMembers;
import mx.kenzie.eris.api.event.guild.member.RemoveGuildMember;
import mx.kenzie.eris.api.event.guild.member.UpdateGuildMember;
import mx.kenzie.eris.api.event.integration.CreateIntegration;
import mx.kenzie.eris.api.event.integration.DeleteIntegration;
import mx.kenzie.eris.api.event.integration.UpdateIntegration;
import mx.kenzie.eris.api.event.message.*;
import mx.kenzie.eris.api.event.stage.CreateStage;
import mx.kenzie.eris.api.event.stage.DeleteStage;
import mx.kenzie.eris.api.event.stage.UpdateStage;
import mx.kenzie.eris.api.event.thread.*;
import mx.kenzie.eris.api.event.vote.AddMessagePollVote;
import mx.kenzie.eris.api.event.vote.RemoveMessagePollVote;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.data.incoming.Incoming;
import mx.kenzie.eris.data.incoming.gateway.*;
import mx.kenzie.eris.data.incoming.http.GatewayConnection;
import mx.kenzie.eris.data.outgoing.Outgoing;
import mx.kenzie.eris.data.outgoing.gateway.Heartbeat;
import mx.kenzie.eris.data.outgoing.gateway.Identify;
import mx.kenzie.eris.data.outgoing.gateway.Resume;
import mx.kenzie.eris.network.NetworkController;
import mx.kenzie.eris.utility.CommandRegister;
import mx.kenzie.eris.utility.ResponseManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Bot extends Lazy implements Runnable, AutoCloseable {

    public static final Map<String, Class<? extends Event>> EVENT_LIST = new HashMap<>();
    public static String API_URL = "https://discord.com/api/v10";
    public static String CDN_URL = "https://cdn.discordapp.com";
    public static boolean DEBUG_MODE = false;
    public static Consumer<Throwable> exceptionHandler;

    static {
        EVENT_LIST.put("READY", Ready.class);
        EVENT_LIST.put("RESUMED", Resumed.class);
        EVENT_LIST.put("MESSAGE_CREATE", ReceiveMessage.class);
        EVENT_LIST.put("MESSAGE_UPDATE", UpdateMessage.class);
        EVENT_LIST.put("MESSAGE_DELETE", DeleteMessage.class);
        EVENT_LIST.put("MESSAGE_DELETE_BULK", BulkDeleteMessage.class);
        EVENT_LIST.put("INTERACTION_CREATE", Interaction.class);
        EVENT_LIST.put("GUILD_JOIN_REQUEST_UPDATE", UpdateJoinRequest.class);
        EVENT_LIST.put("GUILD_CREATE", IdentifyGuild.class);
        EVENT_LIST.put("GUILD_UPDATE", UpdateGuild.class);
        EVENT_LIST.put("GUILD_DELETE", DeleteGuild.class);
        EVENT_LIST.put("GUILD_AUDIT_LOG_ENTRY_CREATE", CreateAuditLogEntry.class);
        EVENT_LIST.put("GUILD_ROLE_CREATE", CreateGuildRole.class);
        EVENT_LIST.put("GUILD_ROLE_UPDATE", UpdateGuildRole.class);
        EVENT_LIST.put("GUILD_ROLE_DELETE", DeleteGuildRole.class);
        EVENT_LIST.put("INVITE_CREATE", CreateInvite.class);
        EVENT_LIST.put("INVITE_DELETE", DeleteInvite.class);
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
        EVENT_LIST.put("THREAD_MEMBER_UPDATE", UpdateThreadMember.class);
        EVENT_LIST.put("THREAD_MEMBERS_UPDATE", UpdateThreadMembers.class);
        EVENT_LIST.put("GUILD_SCHEDULED_EVENT_CREATE", CreateScheduledEvent.class);
        EVENT_LIST.put("GUILD_SCHEDULED_EVENT_UPDATE", UpdateScheduledEvent.class);
        EVENT_LIST.put("GUILD_SCHEDULED_EVENT_DELETE", DeleteScheduledEvent.class);
        EVENT_LIST.put("GUILD_SCHEDULED_EVENT_USER_ADD", ScheduledEventAddUser.class);
        EVENT_LIST.put("GUILD_SCHEDULED_EVENT_USER_REMOVE", ScheduledEventRemoveUser.class);
        EVENT_LIST.put("PRESENCE_UPDATE", UpdatePresence.class);
        EVENT_LIST.put("APPLICATION_COMMAND_PERMISSIONS_UPDATE", UpdateCommandPermissions.class);
        EVENT_LIST.put("INTEGRATION_CREATE", CreateIntegration.class);
        EVENT_LIST.put("INTEGRATION_UPDATE", UpdateIntegration.class);
        EVENT_LIST.put("INTEGRATION_DELETE", DeleteIntegration.class);
        EVENT_LIST.put("MESSAGE_REACTION_ADD", AddMessageReaction.class);
        EVENT_LIST.put("MESSAGE_REACTION_REMOVE", RemoveMessageReaction.class);
        EVENT_LIST.put("MESSAGE_REACTION_REMOVE_ALL", RemoveAllMessageReactions.class);
        EVENT_LIST.put("MESSAGE_REACTION_REMOVE_EMOJI", RemoveEmojiMessageReactions.class);
        EVENT_LIST.put("STAGE_INSTANCE_CREATE", CreateStage.class);
        EVENT_LIST.put("STAGE_INSTANCE_UPDATE", UpdateStage.class);
        EVENT_LIST.put("STAGE_INSTANCE_DELETE", DeleteStage.class);
        EVENT_LIST.put("TYPING_START", StartTyping.class);
        EVENT_LIST.put("USER_UPDATE", UpdateUser.class);
        EVENT_LIST.put("VOICE_STATE_UPDATE", UpdateVoiceState.class);
        EVENT_LIST.put("VOICE_SERVER_UPDATE", UpdateVoiceServer.class);
        EVENT_LIST.put("WEBHOOKS_UPDATE", UpdateWebhooks.class);
        EVENT_LIST.put("ENTITLEMENT_CREATE", CreateEntitlement.class);
        EVENT_LIST.put("ENTITLEMENT_UPDATE", UpdateEntitlement.class);
        EVENT_LIST.put("ENTITLEMENT_DELETE", DeleteEntitlement.class);
        EVENT_LIST.put("VOICE_CHANNEL_EFFECT_SEND", SendVoiceChannelEffect.class);
        EVENT_LIST.put("MESSAGE_POLL_VOTE_ADD", AddMessagePollVote.class);
        EVENT_LIST.put("MESSAGE_POLL_VOTE_REMOVE", RemoveMessagePollVote.class);
        // not technically events but here for documentation parity
        EVENT_LIST.put("HELLO", Hello.class);
        EVENT_LIST.put("RECONNECT", Reconnect.class);
        EVENT_LIST.put("INVALID_SESSION", InvalidSession.class);
        // internally-tracked events
        EVENT_LIST.put("$INTERNAL_CLOSE_SOCKET", SocketClose.class);
        EVENT_LIST.put("$INTERNAL_DEBUG", Debug.class);
    }

    public final ExecutorService executor = Executors.newCachedThreadPool();
    protected final Map<Listener<?>, Class<? extends Event>> listeners = new HashMap<>();
    protected final Map<Command, CommandHandler> commands = new HashMap<>();
    protected final DiscordAPI api;
    protected final ResponseManager responder;
    protected String token, secret;
    final String[] headers = {"Authorization", null, "User-Agent", "DiscordBot(Eris, B)"};
    private final Object lock = new Object();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    protected NetworkController network;
    protected int intents;
    protected volatile Self self;
    protected volatile String session;
    protected transient WebSocket socket;
    protected boolean heartbeatReceived;
    private boolean running = true;
    private CompletableFuture<?> process;
    private ScheduledFuture<?> heartbeat;
    private transient boolean shouldResume = false;

    Bot() {
        this("token");
    }

    public Bot(String token, int... intents) {
        this(token, null, intents);
    }

    public Bot(String token, @Nullable String secret, int... intents) {
        this.secret = secret;
        this.token = token;
        this.headers[1] = "Bot " + token;
        this.network = new NetworkController(API_URL, this);
        this.api = new DiscordAPI(network, this);
        this.responder = new ResponseManager(this, api);
        for (int intent : intents) this.intents |= intent;
    }

    public boolean hasClientSecret() {
        return secret != null;
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

    @Contract(pure = true)
    public CommandRegister registerCommands() {
        return new CommandRegister(this, api);
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
            this.registerPayloadListener(HeartbeatReceived.class, beat -> this.heartbeatReceived = true);
            this.registerPayloadListener(Incoming.class, incoming -> incoming.network.notify(incoming.sequence));
            this.registerPayloadListener(Dispatch.class, dispatch -> {
                final Json.JsonHelper helper = dispatch.network.helper;
                this.debug("Preparing " + dispatch.key);
                final Class<? extends Event> type = EVENT_LIST.getOrDefault(dispatch.key, Event.Unknown.class);
                final Event event = helper.createObject(type);
                if (event instanceof Entity entity) entity.api = this.api;
                helper.mapToObject(event, type, dispatch.data);
                this.triggerEvent(event);
            });
            this.registerListener(Debug.class, debug -> System.out.println(debug.message));
            this.registerListener(SocketClose.class, close -> {
                this.debug("Received close event (" + close.code + ")");
                if (close.code >= 1000 && close.code < 2000) {
                    if (heartbeat != null) heartbeat.cancel(true);
                    this.heartbeat = null;
                    this.network.sequence.set(0);
                    this.shouldResume = false; // Don't resume for RFC spec. closing codes
                }
                this.debug("Attempting reconnect sequence.");
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
                this.debug("Received Invalid Session (9)");
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
                if (heartbeat != null) heartbeat.cancel(true);
                this.heartbeat = scheduler.scheduleWithFixedDelay(() -> {
                    final Heartbeat heartbeat = new Heartbeat();
                    final int sequence = this.network.sequence.getAcquire();
                    heartbeat.data = sequence < 1 ? null : sequence;
                    if (heartbeatReceived) {
                        this.dispatch(heartbeat);
                        this.heartbeatReceived = false;
                    } else this.connect(true);
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
                if (responder.consume(interaction)) return;
                for (final Map.Entry<Command, CommandHandler> entry : this.commands.entrySet()) {
                    final Command command = entry.getKey();
                    if (interaction.data.type != null && interaction.data.type != command.type) continue;
                    if (!command.name.equals(interaction.data.name)) continue;
                    if (command.guild_id == null || command.guild_id.equals(interaction.guild_id))
                        entry.getValue().on(interaction);
                }
            });
            this.connect(false);
            this.scheduler.schedule(this.api::cleanCache, 90, TimeUnit.SECONDS);
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
        this.heartbeatReceived = true; // to pass first time
        this.debug("Preparing to open socket.");
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
            this.debug("Requested socket URL.");
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

    public void debug(String message) {
        if (!DEBUG_MODE) return;
        final Throwable throwable = new Throwable();
        final StackTraceElement[] original, trace;
        throwable.fillInStackTrace();
        original = throwable.getStackTrace();
        trace = new StackTraceElement[original.length - 1];
        System.arraycopy(original, 1, trace, 0, trace.length);
        final Debug debug = new Debug(message, trace);
        this.triggerEvent(debug);
    }

    @ApiStatus.Internal
    public ScheduledExecutorService scheduler() {
        return scheduler;
    }

    @ApiStatus.Internal
    public Map<Command, CommandHandler> commands() {
        return commands;
    }

    public ResponseManager responder() {
        return responder;
    }

    public String token() {
        return token;
    }

    public String secret() {
        return secret;
    }

    public int intents() {
        return intents;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setIntents(int intents) {
        this.intents = intents;
    }

}
