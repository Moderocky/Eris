package mx.kenzie.eris.network;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.api.entity.Snowflake;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class CacheJson extends Json {
    
    protected EntityCache cache;
    
    public CacheJson(String string, EntityCache cache) {
        super(string);
    }
    
    public CacheJson(InputStream reader, EntityCache cache) {
        super(reader);
        this.cache = cache;
    }
    
    public CacheJson(File file, EntityCache cache) {
        super(file);
        this.cache = cache;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected <Type> Type toObject(Type object, Class<?> type, Map<?, ?> map) {
        if (!(object instanceof Snowflake source)) return super.toObject(object, type, map);
        if (!Snowflake.class.isAssignableFrom(type)) return super.toObject(object, type, map);
        final String id;
        if (source.id != null) id = source.id;
        else if (map.containsKey("id")) id = map.get("id") + "";
        else return super.toObject(object, type, map);
        final Snowflake snowflake = cache.get(id), result;
        super.toObject(source, type, map);
        if (snowflake == null) {
            result = source;
            this.cache.store(source);
        } else if (!object.getClass().isInstance(snowflake)) {
            result = source;
            this.cache.store(source);
            super.toObject(snowflake, type, map);
        } else {
            result = super.toObject(snowflake, type, map);
        }
        return (Type) result;
    }
    
    @Override
    public <Type> Type createObject(Class<Type> type) {
        return super.createObject(type);
    }
    
}
