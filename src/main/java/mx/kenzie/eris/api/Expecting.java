package mx.kenzie.eris.api;

import mx.kenzie.eris.error.DiscordException;

public abstract class Expecting<Type> extends Lazy {

    private transient final Object lock = new Object();
    private transient volatile boolean triggered0, ready0, cancelled0;
    private transient Type object;
    private transient final long createdAt = System.currentTimeMillis();

    @Override
    public void await(long timeout) {
        if (!triggered0) return;
        try {
            synchronized (lock) {
                if (ready0) return;
                this.lock.wait(timeout);
            }
            if (!ready0) this.cancel();
        } catch (Throwable ex) {
            throw new DiscordException(ex);
        }
    }

    @Override
    public synchronized boolean ready() {
        return this.ready0 && super.ready();
    }

    @Override
    public void await() {
        if (!triggered0) return;
        try {
            synchronized (lock) {
                if (ready0) return;
                this.lock.wait();
            }
        } catch (Throwable ex) {
            throw new DiscordException(ex);
        }
    }

    protected synchronized void cancel() {
        this.cancelled0 = true;
        this.finish();
    }

    protected synchronized void trigger() {
        this.triggered0 = true;
        this.ready0 = false;
    }

    protected void setResult(Type result) {
        synchronized (this) {
            this.object = result;
            this.triggered0 = false;
            this.ready0 = true;
        }
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }

    protected synchronized boolean cancelled() {
        return this.cancelled0;
    }

    protected synchronized Type getResult() {
        return object;
    }

    /**
     * Used internally for deciding when to discard auto-generated interaction listeners.
     */
    protected long createdAt() {
        return createdAt;
    }

}
