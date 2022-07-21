package mx.kenzie.eris.error;

import mx.kenzie.argo.meta.Name;

import java.util.Map;

public class APIException extends DiscordException {
    
    protected int code;
    protected @Name("message") String message;
    protected Map<String, Object> errors;
    
    public APIException(String message) {
        super(message);
    }
    
    public Map<String, Object> getErrors() {
        return errors;
    }
    
    @Override
    public void printStackTrace() {
        System.err.println("Errors from API: " + errors);
        super.printStackTrace();
    }
}
