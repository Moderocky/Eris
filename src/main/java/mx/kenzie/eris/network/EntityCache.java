package mx.kenzie.eris.network;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.error.DiscordException;

import java.lang.ref.SoftReference;
import java.util.*;

public class EntityCache {
    
    public final Json.JsonHelper helper = new Json.JsonHelper();
    protected final Map<String, SoftReference<Snowflake>> map;
    public Set<Class<? extends Snowflake>> permitted = new HashSet<>();
    protected boolean shouldCache;
    
    @SafeVarargs
    public EntityCache(Class<? extends Snowflake>... permitted) {
        this();
        this.permitted.addAll(Arrays.asList(permitted));
    }
    
    public EntityCache() {
        this(new HashMap<>());
    }
    
    protected EntityCache(Map<String, SoftReference<Snowflake>> map) {
        this.map = map;
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Snowflake> Type getOrCreate(String id, Class<Type> type) {
        if (!shouldCache) return helper.createObject(type);
        final SoftReference<Snowflake> reference;
        synchronized (map) {
            reference = map.get(id);
        }
        if (reference == null || reference.get() == null) {
            final Type thing = helper.createObject(type);
            this.store(thing);
            return thing;
        }
        return (Type) reference.get();
    }
    
    public void store(Snowflake entity) {
        if (!shouldCache) return;
        if (entity == null || entity.id == null) throw new DiscordException("Unable to handle entity: " + entity);
        synchronized (map) {
            this.map.put(entity.id, new SoftReference<>(entity));
        }
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Snowflake> Type getOrUse(String id, Type type) {
        if (!shouldCache) return type;
        final SoftReference<Snowflake> reference;
        synchronized (map) {
            reference = map.get(id);
        }
        if (reference == null || reference.get() == null) {
            this.store(type);
            return type;
        }
        return (Type) reference.get();
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Snowflake> Type get(String id) {
        if (!shouldCache) return null;
        final SoftReference<Snowflake> reference;
        synchronized (map) {
            reference = map.get(id);
        }
        if (reference == null) return null;
        final Entity entity = reference.get();
        return (Type) entity;
    }
    
    public void clean() {
        synchronized (map) {
            this.map.entrySet().removeIf(entry -> entry.getValue().get() == null);
        }
    }
}

