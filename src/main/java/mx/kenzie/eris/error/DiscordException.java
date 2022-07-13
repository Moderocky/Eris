package mx.kenzie.eris.error;

public class DiscordException extends RuntimeException {
    public DiscordException() {
        super();
    }
    
    public DiscordException(String message) {
        super(message);
    }
    
    public DiscordException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DiscordException(Throwable cause) {
        super(cause);
    }
    
    protected DiscordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
