package mx.kenzie.eris.error;

import mx.kenzie.grammar.Name;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;

public class APIException extends DiscordException {

    protected int code;
    protected @Name("message") String message;
    protected Map<String, Object> errors;
    protected URI source;

    public APIException(URI source, String message) {
        this(message);
        this.source = source;
    }

    public APIException(String message) {
        super(message);
    }

    public APIException(int code, String message) {
        this(message);
        this.code = code;
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
        if (source != null) s.println("Error from " + source);
        s.println("Error code " + code + ": " + this.getStatusCode());
        s.println("Errors from API: " + errors);
        super.printStackTrace(s);
    }

    public StatusCode getStatusCode() {
        return StatusCode.get(code);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (source != null) s.println("Error from " + source);
        s.println("Error code " + code + ": " + this.getStatusCode());
        s.println("Errors from API: " + errors);
        super.printStackTrace(s);
    }
}
