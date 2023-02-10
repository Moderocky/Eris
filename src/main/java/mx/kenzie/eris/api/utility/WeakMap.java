package mx.kenzie.eris.api.utility;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class WeakMap<Key, Value> extends HashMap<Key, WeakReference<Value>> {

    public synchronized void set(Key key, Value value) {
        this.put(key, new WeakReference<>(value));
    }

    public synchronized Value getValue(Key key) {
        final WeakReference<Value> reference = this.get(key);
        if (reference == null) return null;
        return reference.get();
    }

    public void cleanAsync() {
        CompletableFuture.runAsync(this::clean);
    }

    public synchronized void clean() {
        this.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }

}
