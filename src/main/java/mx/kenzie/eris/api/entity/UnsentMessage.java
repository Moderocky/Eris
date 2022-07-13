package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.data.Payload;

public abstract class UnsentMessage extends Snowflake {
    public @Optional String content, payload_json;
    public @Optional Payload[] attachments, components, embeds;
    public int flags;
    public boolean tts;
    public @Optional Message.Reference reference;
    public @Optional String[] sticker_ids;
    
    // files[n] todo
    
    protected transient boolean sent0;
    public boolean sent() {
        return this.sent0;
    }
    
    protected UnsentMessage() {
        assert this instanceof Message: "This class should not be instantiated directly.";
    }
    
}
