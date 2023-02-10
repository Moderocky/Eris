package mx.kenzie.eris.api.utility;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MagicQueue<Type> {

    private final Queue<Type> queue = new ConcurrentLinkedQueue<>();
    private final Object lock = new Object();
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
            this.queue.offer(type);
            synchronized (lock) {
                this.lock.notifyAll();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public Type get() {
        Type type;
        while ((type = this.queue.poll()) == null && !closed) try {
            synchronized (lock) {
                this.lock.wait(50L);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return type;
    }
}
