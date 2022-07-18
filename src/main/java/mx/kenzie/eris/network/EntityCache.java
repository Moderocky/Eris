package mx.kenzie.eris.network;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.error.DiscordException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityCache {
    
    public Set<Class<? extends Snowflake>> permitted = new HashSet<>();
    protected final Map<String, WeakReference<Snowflake>> map;
    protected boolean shouldCache;
    public final Json.JsonHelper helper = new Json.JsonHelper();
    
    public EntityCache() {
        this(new HashMap<>());
    }
    
    protected EntityCache(Map<String, WeakReference<Snowflake>> map) {
        this.map = map;
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Snowflake> Type getOrCreate(String id, Class<Type> type) {
        if (!shouldCache) return helper.createObject(type);
        final WeakReference<Snowflake> reference;
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
    
    @SuppressWarnings("unchecked")
    public <Type extends Snowflake> Type getOrUse(String id, Type type) {
        if (!shouldCache) return type;
        final WeakReference<Snowflake> reference;
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
        final WeakReference<Snowflake> reference;
        synchronized (map) {
            reference = map.get(id);
        }
        if (reference == null) return null;
        final Entity entity = reference.get();
        return (Type) entity;
    }
    
    public void store(Snowflake entity) {
        if (!shouldCache) return;
        if (entity == null || entity.id == null) throw new DiscordException("Unable to handle entity: " + entity);
        synchronized (map) {
            this.map.put(entity.id, new WeakReference<>(entity));
        }
    }
    
    public void clean() {
        synchronized (map) {
            this.map.entrySet().removeIf(entry -> entry.getValue().get() == null);
        }
    }
}

