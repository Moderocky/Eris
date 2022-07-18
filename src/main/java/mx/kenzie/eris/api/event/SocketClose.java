package mx.kenzie.eris.api.event;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class SocketClose extends Payload implements Event {
    
    public final int code;
    public final String reason;
    
    public SocketClose(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }
}
