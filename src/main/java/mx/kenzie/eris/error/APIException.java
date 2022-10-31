package mx.kenzie.eris.error;

import mx.kenzie.argo.meta.Name;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

public class APIException extends DiscordException {
    
    protected int code;
    protected @Name("message") String message;
    protected Map<String, Object> errors;
    
    public APIException(String message) {
        super(message);
    }
    
    public APIException(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Map<String, Object> getErrors() {
        return errors;
    }
    
    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        s.println("Error code " + code + ": " + this.getStatusCode());
        s.println("Errors from API: " + errors);
        super.printStackTrace(s);
    }
    
    public StatusCode getStatusCode() {
        return StatusCode.get(code);
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        s.println("Error code " + code + ": " + this.getStatusCode());
        s.println("Errors from API: " + errors);
        super.printStackTrace(s);
    }
}
