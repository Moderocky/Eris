package mx.kenzie.eris.api.utility;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RequestBuilder<Type> {

    protected final Type type;
    protected final Map<String, Object> map;
    protected final String mode, path;
    protected final DiscordAPI api;

    public RequestBuilder(DiscordAPI api, String type, String path, Type object) {
        this.api = api;
        this.type = object;
        this.map = new HashMap<>();
        this.mode = type;
        this.path = path;
    }

    public void set(String string, Object value) {
        this.map.put(string, value);
    }

    public Type submit() {
        final CompletableFuture<Type> future = this.api.request(mode, path, Json.toJson(map), type);
        if (type instanceof Lazy lazy) future.exceptionally(throwable -> {
            lazy.error(throwable);
            return type;
        }).thenAccept(thing -> lazy.finish());
        if (type instanceof Entity entity) entity.api = api;
        return type;
    }

}
