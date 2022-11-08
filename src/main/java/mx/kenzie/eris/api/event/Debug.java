package mx.kenzie.eris.api.event;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class Debug extends Payload implements Event {
    
    public final String message;
    public final StackTraceElement[] trace;
    
    public Debug(String message, StackTraceElement... trace) {
        this.message = message;
        this.trace = trace;
    }
    
}
