package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.Lazy;
import org.jetbrains.annotations.Contract;

public class Snowflake extends Lazy {
    
    public @Optional String id;
    private transient long id0;
    
    @Contract(pure = true)
    public long id() {
        if (id0 < 1) return id0 = Long.parseLong(id);
        return id0;
    }
    
    @Override
    public String debugName() {
        return "[" + this.getClass().getSimpleName() + ":" + this.id + "]";
    }
}
