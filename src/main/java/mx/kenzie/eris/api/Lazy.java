package mx.kenzie.eris.api;

import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.error.DiscordException;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Lazy objects are created unfulfilled.
 * The data is then asked for in the background and added to the object.
 * <p>
 * A lazy object has been completely acquired if:
 * 1. Its `ready` method returns `true`.
 * 2. The `await` method calls and completes without exception.
 * 3. The object was cached or acquired from a local source, its data having been regurgitated.
 * <p>
 * The fields of a lazy object are **unsafe** to use outside these cases.
 * Even when acquired successfully, the fields should be accessed only when __synchronized__.
 * Not all fields are volatile: some may never be safe to access.
 * <p>
 * The methods of a lazy object are safe to use outside synchronization.
 */
public abstract class Lazy extends Entity {
    private transient final Object lock = new Object();
    private transient volatile boolean ready0;
    private transient DiscordException exception;
    
    @Contract(pure = true)
    public void await(long timeout) {
        try {
            synchronized (lock) {
                if (this.ready()) return;
                this.lock.wait(timeout);
            }
        } catch (Throwable ex) {
            this.exception = new DiscordException(ex);
        }
    }
    
    @Contract(pure = true)
    public synchronized boolean ready() {
        return this.ready0;
    }
    
    @Contract(pure = true)
    public synchronized void unready() {
        this.exception = null;
        this.ready0 = false;
    }
    
    @Contract(pure = true)
    public void finish() {
        synchronized (this) {
            this.ready0 = true;
        }
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
    /**
     * Exists purely for use in CompletableFuture chains.
     */
    public Void error0(Throwable ex) {
        this.error(ex);
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public <Type extends Lazy> Type error(Throwable ex) {
        synchronized (this) {
            if (ex instanceof DiscordException discord) exception = discord;
            else exception = new DiscordException(ex);
            this.ready0 = true;
        }
        synchronized (lock) {
            this.lock.notifyAll();
        }
        return (Type) this;
    }
    
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <Type extends Lazy> CompletableFuture<Void> whenReady(Consumer<Type> consumer, Consumer<DiscordException> error) {
        return CompletableFuture.runAsync(() -> {
            if (this.successful()) consumer.accept((Type) this);
            else error.accept(this.error());
        });
    }
    
    @Contract(pure = true)
    public boolean successful() {
        this.await();
        synchronized (this) {
            return exception == null;
        }
    }
    
    public synchronized DiscordException error() {
        return exception;
    }
    
    @Contract(pure = true)
    public void await() {
        try {
            synchronized (lock) {
                if (this.ready()) return;
                this.lock.wait();
            }
        } catch (Throwable ex) {
            this.exception = new DiscordException(ex);
        }
    }
    
    @Contract(pure = true)
    public <Type extends Lazy> CompletableFuture<Void> whenReady(Consumer<Type> consumer) {
        return this.<Type>whenReady().thenAccept(consumer);
    }
    
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <Type extends Lazy> CompletableFuture<Type> whenReady() {
        return CompletableFuture.supplyAsync(() -> {
            this.await();
            return (Type) this;
        });
    }
    
}
