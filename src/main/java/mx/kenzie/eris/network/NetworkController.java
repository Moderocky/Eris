package mx.kenzie.eris.network;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.Listener;
import mx.kenzie.eris.api.utility.MultiBody;
import mx.kenzie.eris.data.incoming.Incoming;
import mx.kenzie.eris.data.incoming.gateway.*;
import mx.kenzie.eris.data.outgoing.Outgoing;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkController implements Closeable {

    public static final Map<Integer, Class<? extends Incoming>> NETWORK_CODES = new HashMap<>();

    static {
        NETWORK_CODES.put(0, Dispatch.class);
        NETWORK_CODES.put(7, Reconnect.class);
        NETWORK_CODES.put(9, InvalidSession.class);
        NETWORK_CODES.put(10, Hello.class);
        NETWORK_CODES.put(11, HeartbeatReceived.class);
    }

    public final Map<Integer, Class<? extends Incoming>> codes = new HashMap<>(NETWORK_CODES);
    public final Map<Listener<?>, Class<? extends Incoming>> listeners = new HashMap<>();

    public final String base;
    public final AtomicInteger sequence = new AtomicInteger();

    public final Json.JsonHelper helper = new Json.JsonHelper();
    protected final Bot bot;
    protected WebSocket socket;
    private HttpClient client = HttpClient.newHttpClient();

    public NetworkController(String base, Bot bot) {
        this.base = base;
        this.bot = bot;
    }

    public HttpClient getClient() {
        return client;
    }

    @Override
    public void close() {
        this.client = null; // Null the client to allow termination
        if (socket != null && !socket.isOutputClosed())
            this.socket.sendClose(1000, "Network controller forcibly closed socket.");
        this.codes.clear();
        this.listeners.clear();
    }

    void triggerEvent(Event event) {
        this.bot.triggerEvent(event);
    }

    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    void triggerEvent(Incoming payload) {
        for (final Map.Entry<Listener<?>, Class<? extends Incoming>> entry : this.listeners.entrySet()) {
            final Class<?> type = entry.getValue();
            if (!type.isInstance(payload)) continue;
            final Listener listener = entry.getKey();
            CompletableFuture.runAsync(() -> {
                try {
                    listener.on(payload);
                } catch (Throwable ex) {
                    Bot.handle(ex);
                }
            }, bot.executor);
        }
    }

    public CompletableFuture<?> sendPayload(Outgoing payload) {
        assert socket != null;
        assert payload != null;
        this.bot.debug("Dispatch " + payload.getClass().getSimpleName());
        return this.socket.sendText(Json.toJson(payload), true);
    }

    public <Event extends Incoming> void registerListener(Class<Event> type, Listener<Event> listener) {
        this.listeners.put(listener, type);
    }

    public void unregisterListener(Listener<?> listener) {
        this.listeners.remove(listener);
    }

    public Listener<?>[] getListeners(Class<? extends Incoming> type) {
        final List<Listener<?>> list = new ArrayList<>();
        for (final Map.Entry<Listener<?>, Class<? extends Incoming>> entry : listeners.entrySet()) {
            final Class<?> value = entry.getValue();
            if (type.isAssignableFrom(value)) list.add(entry.getKey());
        }
        return list.toArray(new Listener[0]);
    }

    public void registerNetworkCode(int code, Class<? extends Incoming> payload) {
        this.codes.put(code, payload);
    }

    public Incoming getPayload(CharSequence data) {
        try (final Json json = new Json(data.toString())) {
            return this.getPayload(json);
        }
    }

    private Incoming getPayload(Json json) {
        final Map<String, Object> map = json.toMap();
        final Integer code = (Integer) map.get("op");
        final Class<? extends Incoming> type = codes.getOrDefault(code, Incoming.class);
        final Incoming payload = helper.createObject(type);
        this.helper.mapToObject(payload, type, map);
        return payload;
    }

    public Incoming getPayload(InputStream data) {
        try (final Json json = new Json(data)) {
            return this.getPayload(json);
        }
    }

    public void notify(Integer sequence) {
        synchronized (this.sequence) {
            if (sequence == null) return;
            if (sequence < this.sequence.get()) return;
            this.sequence.set(sequence);
        }
    }

    public WebSocket openSocket(String url) {
        this.bot.debug("Building websocket.");
        final WebSocket.Builder builder = client.newWebSocketBuilder();
        this.socket = builder.buildAsync(URI.create(url), new SocketListener(this)).join();
        return socket;
    }

    public HttpResponse<InputStream> multiRequest(String method, String path, MultiBody body, String... headers)
        throws IOException, InterruptedException {
        final URI uri = URI.create(base + path);
        final HttpRequest.BodyPublisher publisher;
        final List<String> list = new ArrayList<>();
        for (final String header : headers) list.add(header.trim());
        list.add("Content-Type");
        list.add("multipart/form-data; boundary=boundary");
        if (body == null) publisher = HttpRequest.BodyPublishers.noBody();
        else publisher = HttpRequest.BodyPublishers.ofInputStream(body::stream);
        final HttpRequest request = HttpRequest.newBuilder(uri).method(method, publisher)
            .headers(list.toArray(new String[0])).build();
        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    public HttpResponse<InputStream> request(String method, String path, String body, String... headers)
        throws IOException, InterruptedException {
        final URI uri = URI.create(base + path);
        final HttpRequest.BodyPublisher publisher;
        if (body == null) publisher = HttpRequest.BodyPublishers.noBody();
        else publisher = HttpRequest.BodyPublishers.ofString(body);
        final List<String> list = new ArrayList<>();
        for (final String header : headers) list.add(header.trim());
        if (body != null && !list.contains("Content-Type")) {
            list.add("Content-Type");
            list.add("application/json");
        }
        final HttpRequest request = HttpRequest.newBuilder(uri).method(method, publisher)
            .headers(list.toArray(new String[0])).build();
        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    public HttpResponse<InputStream> get(String path, String... headers) throws IOException, InterruptedException {
        final URI uri = URI.create(base + path);
        final HttpRequest request = HttpRequest.newBuilder(uri).GET().headers(headers).build();
        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    public HttpResponse<InputStream> post(String path, InputStream stream, String... headers)
        throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofInputStream(() -> stream);
        return this.post(path, publisher, headers);
    }

    protected HttpResponse<InputStream> post(String path, HttpRequest.BodyPublisher publisher, String... headers)
        throws IOException, InterruptedException {
        final URI uri = URI.create(base + path);
        final HttpRequest request = HttpRequest.newBuilder(uri).POST(publisher).headers(headers).build();
        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    public HttpResponse<InputStream> post(String path, Path file, String... headers)
        throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofFile(file);
        return this.post(path, publisher, headers);
    }

    public HttpResponse<InputStream> post(String path, String body, String... headers)
        throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
        return this.post(path, publisher, headers);
    }

    public HttpResponse<InputStream> patch(String path, String body, String... headers)
        throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
        return this.patch(path, publisher, headers);
    }

    protected HttpResponse<InputStream> patch(String path, HttpRequest.BodyPublisher publisher, String... headers)
        throws IOException, InterruptedException {
        final URI uri = URI.create(base + path);
        final HttpRequest request = HttpRequest.newBuilder(uri).method("PATCH", publisher).headers(headers).build();
        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    public HttpResponse<Void> delete(String path, String... headers) throws IOException, InterruptedException {
        final URI uri = URI.create(base + path);
        final HttpRequest request = HttpRequest.newBuilder(uri).DELETE().headers(headers).build();
        return client.send(request, HttpResponse.BodyHandlers.discarding());
    }

}
