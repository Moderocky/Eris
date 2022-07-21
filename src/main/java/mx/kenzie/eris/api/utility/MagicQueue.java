package mx.kenzie.eris.api.utility;

import mx.kenzie.eris.error.DiscordException;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MagicQueue<Type> {
    
    private final SynchronousQueue<Type> queue = new SynchronousQueue<>();
    private final Object lock = new Object();
    private final AtomicInteger integer = new AtomicInteger(6);
    volatile boolean closed;
    
    public MagicQueue() {
    }
    
    public void close() {
        this.closed = true;
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
    public void add(Type type) {
        try {
            this.queue.offer(type, 100L, TimeUnit.MILLISECONDS);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    public Type get() {
        try {
            return this.queue.take();
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
