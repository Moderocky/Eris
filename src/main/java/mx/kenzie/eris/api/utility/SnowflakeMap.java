package mx.kenzie.eris.api.utility;

import java.util.HashMap;
import java.util.Map;

public class SnowflakeMap<Type> extends HashMap<String, Type> {
    
    public SnowflakeMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public SnowflakeMap(int initialCapacity) {
        super(initialCapacity);
    }
    
    public SnowflakeMap() {
        super();
    }
    
    public SnowflakeMap(Map<? extends String, ? extends Type> m) {
        super(m);
    }
    
    public Type get(long id) {
        return this.get(Long.toString(id));
    }
    
    public boolean containsKey(long id) {
        return this.containsKey(Long.toString(id));
    }
    
    public Type remove(long id) {
        return this.remove(Long.toString(id));
    }
    
}
