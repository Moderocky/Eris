package mx.kenzie.eris.api;

import mx.kenzie.eris.error.DiscordException;

public abstract class Expecting<Type> extends Lazy {
    
    private transient final Object lock = new Object();
    private transient volatile boolean triggered0, ready0, cancelled0;
    private transient Type object;
    
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
    
    public synchronized void cancel() {
        this.cancelled0 = true;
        this.finish();
    }
    
    public abstract void expectResult();
    
    public synchronized void trigger() {
        this.triggered0 = true;
        this.ready0 = false;
    }
    
    public void setResult(Type result) {
        synchronized (this) {
            this.object = result;
            this.triggered0 = false;
            this.ready0 = true;
        }
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
    public synchronized boolean cancelled() {
        return this.cancelled0;
    }
    
    public synchronized Type result() {
        return object;
    }
    
}
