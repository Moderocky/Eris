package mx.kenzie.eris.api.utility;

import mx.kenzie.eris.error.DiscordException;

import java.io.*;

public class MultiBody {
    
    private final OutputStream write;
    private final InputStream read;
    
    private final PrintWriter writer;
    
    public MultiBody(OutputStream write, InputStream read) {
        this.write = write;
        this.read = read;
        this.writer = new PrintWriter(write);
    }
    
    public void sectionMessage(String data) {
        this.writer.println("--section");
        this.writer.println("Content-Disposition: form-data; name=\"payload_json\"");
        this.writer.println("Content-Type: application/json");
    }
    
    public void section(String name, String data) {
        this.writer.println("--section");
        this.writer.println("Content-Disposition: form-data; name=\"" + name + "\"");
        this.writer.println("Content-Type: text/plain; charset=UTF-8");
    }
    
    public void section(String name, File file) {
        final String key = file.getName();
        this.writer.println("--section");
        this.writer.println("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"");
        if (key.endsWith(".png")) writer.println("Content-Type: image/png");
        else if (key.endsWith(".gif")) writer.println("Content-Type: image/gif");
        else writer.println("Content-Type: text/plain; charset=UTF-8");
        try (final InputStream stream = new FileInputStream(file)) {
            stream.transferTo(write);
        } catch (IOException e) {
            throw new DiscordException(e);
        }
    }
    
    public InputStream stream() {
        return read;
    }
    
}
