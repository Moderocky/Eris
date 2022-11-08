package mx.kenzie.eris.api.event;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.data.Payload;

public class SocketClose extends Payload implements Event {
    
    public final int code;
    public final String message;
    private transient Reason reason = null;
    
    public SocketClose(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public boolean shouldReconnect() {
        return this.getReason().shouldReconnect();
    }
    
    public Reason getReason() {
        if (reason == null) reason = Reason.forCode(code);
        return reason;
    }
    
    public enum Reason {
        
        /*
         Discord does not document WebSocket closing codes, but they are still used.
         See https://web.archive.org/web/20220728102833/https://www.rfc-editor.org/rfc/rfc6455#section-7.4.1.
         (Archived from the original: https://www.rfc-editor.org/rfc/rfc6455#section-7.4.1, 2022-07-28T10:28:33)
        */
        WS_CLOSED(1000, true),
        WS_GOING_AWAY(1001, true),
        WS_PROTOCOL_ERROR(1002, true),
        WS_UNACCEPTABLE_DATA(1003, false),
        WS_NO_STATUS(1005, true),
        WS_CLOSED_ABNORMALLY(1006, true),
        WS_INVALID_CONTENT(1007, true),
        WS_MESSAGE_VIOLATES_POLICY(1008, true),
        WS_MESSAGE_TOO_BIG(1009, true),
        WS_EXPECTED_EXTENSIONS(1010, false),
        WS_FAILED_TO_FULFILL(1011, true),
        WS_TLS_HANDSHAKE_FAILED(1015, true),
        
        
        // Documented at https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway-gateway-close-event-codes.
        UNKNOWN_ERROR(4000, true),
        UNKNOWN_OPCODE(4001, true),
        DECODE_ERROR(4002, true),
        NOT_AUTHENTICATED(4003, true),
        AUTHENTICATION_FAILED(4004, false),
        ALREADY_AUTHENTICATE(4005, true),
        INVALID_SEQUENCE(4007, true),
        RATE_LIMITED(4008, true),
        SESSION_TIMED_OUT(4009, true),
        INVALID_SHARD(4010, false),
        SHARDING_REQUIRED(4011, false),
        INVALID_API_VERSION(4012, false),
        INVALID_INTENTS(4013, false),
        DISALLOWED_INTENTS(4014, false);
        
        private final int code;
        private final boolean shouldReconnect;
        
        Reason(int code, boolean shouldReconnect) {
            this.code = code;
            this.shouldReconnect = shouldReconnect;
        }
        
        public static Reason forCode(final int code) {
            for (final Reason reason : Reason.values()) if (reason.code == code) return reason;
            throw new IllegalArgumentException("No reason exists for unknown code " + code);
        }
        
        public int code() {
            return code;
        }
        
        public boolean shouldReconnect() {
            return shouldReconnect;
        }
    }
}
