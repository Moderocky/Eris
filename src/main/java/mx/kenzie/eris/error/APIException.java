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
    
    public Map<String, Object> getErrors() {
        return errors;
    }
    
    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        s.println("Errors from API: " + errors);
        super.printStackTrace(s);
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        s.println("Errors from API: " + errors);
        super.printStackTrace(s);
    }
}
