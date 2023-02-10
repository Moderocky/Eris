package mx.kenzie.eris.api.utility;

import mx.kenzie.eris.error.DiscordException;
import mx.kenzie.jupiter.stream.impl.MemoryOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultiBody implements AutoCloseable {

    private final MemoryOutputStream write;

    public MultiBody() {
        this.write = new MemoryOutputStream(1024, true);
        this.write("");
    }

    protected void write(String line) {
        this.write.write((line + "\r\n").getBytes());
    }

    public void sectionMessage(String data) {
        this.write("--boundary");
        this.write("Content-Disposition: form-data; name=\"payload_json\"");
        this.write("Content-Type: application/json");
        this.write("");
        this.write(data);
    }

    public void finish() {
        this.write("--boundary--");
    }

    public void section(String name, String data) {
        this.write("--boundary");
        this.write("Content-Disposition: form-data; name=\"" + name + "\"");
        this.write("Content-Type: text/plain; charset=UTF-8");
        this.write("");
        this.write(data);
    }

    public void section(String name, String filename, Object data) {
        this.write("--boundary");
        this.write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"");
        if (filename.endsWith(".png")) this.write("Content-Type: image/png");
        else if (filename.endsWith(".gif")) this.write("Content-Type: image/gif");
        else this.write("Content-Type: text/plain; charset=UTF-8");
        this.write("");
        if (data instanceof InputStream stream) {
            try {
                stream.transferTo(write);
            } catch (IOException ex) {
                throw new DiscordException(ex);
            }
        } else if (data instanceof File file) {
            try (final InputStream stream = new FileInputStream(file)) {
                stream.transferTo(write);
            } catch (IOException e) {
                throw new DiscordException(e);
            }
        } else this.write(data.toString());
    }

    public void section(String name, File file) {
        final String key = file.getName();
        this.write("--boundary");
        this.write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\"");
        if (key.endsWith(".png")) this.write("Content-Type: image/png");
        else if (key.endsWith(".gif")) this.write("Content-Type: image/gif");
        else this.write("Content-Type: text/plain; charset=UTF-8");
        try (final InputStream stream = new FileInputStream(file)) {
            stream.transferTo(write);
        } catch (IOException e) {
            throw new DiscordException(e);
        }
    }

    public InputStream stream() { // this needs to be closed
        return write.createInputStream();
    }

    @Override
    public void close() throws Exception {
        this.write.freeMemory();
    }
}
