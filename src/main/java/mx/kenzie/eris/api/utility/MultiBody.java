package mx.kenzie.eris.api.utility;

import mx.kenzie.eris.api.entity.message.Attachment;
import mx.kenzie.eris.error.DiscordException;
import mx.kenzie.jupiter.stream.impl.MemoryOutputStream;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class MultiBody implements AutoCloseable {

    private final MemoryOutputStream write;

    public MultiBody() {
        this.write = new MemoryOutputStream(1024, true);
        this.write("");
    }

    protected void write(String line) {
        this.write.write((line + "\n").getBytes());
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

    public void section(String name, Attachment attachment) {
        this.write("--boundary");
        this.write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + attachment.filename + "\"");
        final String contentType;
        if (attachment.content_type != null) contentType = attachment.content_type;
        else if (attachment.filename.endsWith(".png")) contentType = "image/png";
        else if (attachment.filename.endsWith(".gif")) contentType = "image/gif";
        else contentType = "text/plain; charset=UTF-8";
        this.write("Content-Type: " + contentType);
        this.write("");
        final InputStream stream;
        try {
            if (attachment.content instanceof InputStream input) stream = input;
            else if (attachment.content instanceof URI uri) {
                final URLConnection connection = uri.toURL().openConnection();
                connection.connect();
                stream = connection.getInputStream();
            } else if (attachment.content instanceof File file) stream = new FileInputStream(file);
            else stream = new ByteArrayInputStream((attachment.content.toString() + "\r\n")
                    .getBytes(StandardCharsets.UTF_8));
            this.writeStream(stream);
        } catch (IOException ex) {
            throw new DiscordException(ex);
        }
    }

    private void writeStream(InputStream stream) throws IOException {
        try (stream) {
            stream.transferTo(write);
        }
        this.write("");
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
