package mx.kenzie.eris.api.utility;

import mx.kenzie.eris.DiscordAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BulkEntity<Type> implements Iterable<Type> {
    
    public static <Type> BulkEntity<Type> of(DiscordAPI api, Class<Type> type, Function<List<?>, CompletableFuture<List<?>>> function) {
        return new DefaultImplementation<>(api, type, function);
    }
    
    public LazyList<Type> get() {
        final LazyList<Type> list = LazyList.of(this.getType());
        this.getEntities(list);
        return list;
    }
    
    protected abstract Class<Type> getType();
    
    protected abstract CompletableFuture<List<?>> getEntities(List<?> list);
    
    @NotNull
    @Override
    public Iterator<Type> iterator() {
        final MagicQueue<Type> magic = new MagicQueue<>();
        class Entities implements Iterator<Type> {
            final int limit;
            final MagicQueue<Type> queue = magic;
            transient int count;
            boolean closed;
            transient Type current;
            
            Entities(int limit) {
                this.limit = limit;
            }
            
            public void close() {
                this.queue.close();
                this.closed = true;
            }
            
            @Override
            public boolean hasNext() {
                if (count >= limit) return false;
                this.current = queue.get();
                if (current != null) return true;
                return !closed;
            }
            
            @Override
            public Type next() {
                this.count++;
                return current;
            }
        }
        final Entities messages = new Entities(this.limit());
        final DeferredList<Type> list = new DeferredList<>(this.getType(), magic::add, this.api());
        this.getEntities(list).thenRun(messages::close);
        return messages;
    }
    
    protected abstract int limit();
    
    protected abstract DiscordAPI api();
    
    @Override
    public void forEach(Consumer<? super Type> action) {
        final DeferredList<Type> list = new DeferredList<>(this.getType(), action, this.api());
        this.getEntities(list);
    }
    
    @Override
    public Spliterator<Type> spliterator() {
        return Iterable.super.spliterator();
    }
}

class DefaultImplementation<Type> extends BulkEntity<Type> {
    
    private final DiscordAPI api;
    private final Class<Type> type;
    private final Function<List<?>, CompletableFuture<List<?>>> function;
    
    DefaultImplementation(DiscordAPI api, Class<Type> type, Function<List<?>, CompletableFuture<List<?>>> function) {
        this.api = api;
        this.type = type;
        this.function = function;
    }
    
    @Override
    protected Class<Type> getType() {
        return type;
    }
    
    @Override
    protected CompletableFuture<List<?>> getEntities(List<?> list) {
        return function.apply(list);
    }
    
    @Override
    protected int limit() {
        return 200;
    }
    
    @Override
    protected DiscordAPI api() {
        return api;
    }
}
