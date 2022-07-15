package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.message.Component;
import mx.kenzie.eris.data.Payload;

public abstract class UnsentMessage extends Snowflake {
    public @Optional String content, payload_json;
    public @Optional Payload[] attachments;
    public @Optional @Any Component[] components;
    public @Optional Embed[] embeds;
    public int flags;
    public boolean tts;
    public @Optional Message.Reference message_reference;
    public @Optional String[] sticker_ids;
    
    // files[n] todo
    
    protected transient boolean sent0;
    
    public void setEmbeds(Embed... embeds) {
        this.embeds = embeds;
    }
    
    public void setComponents(Component... components) {
        this.components = components;
    }
    
    public boolean sent() {
        return this.sent0;
    }
    
    protected UnsentMessage() {
        assert this instanceof Message: "This class should not be instantiated directly.";
    }
    
}
