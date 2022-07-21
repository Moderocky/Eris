package mx.kenzie.eris.api.utility;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.entity.Message;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class BulkEntity<Type> implements Iterable<Type> {
    
    public LazyList<Type> get() {
        final LazyList<Type> list = LazyList.of(this.getType());
        this.getEntities(list);
        return list;
    }
    
    protected abstract int limit();
    protected abstract DiscordAPI api();
    protected abstract Class<Type> getType();
    
    protected abstract CompletableFuture<List<?>> getEntities(List<?> list);
    
    @NotNull
    @Override
    public Iterator<Type> iterator() {
        final MagicQueue<Type> magic = new MagicQueue<>();
        class Entities implements Iterator<Type> {
            final int limit;
            transient int count;
            boolean closed;
            final MagicQueue<Type> queue = magic;
            transient Type current;
            
            Entities(int limit) { this.limit = limit; }
            
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
