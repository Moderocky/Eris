package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.data.Payload;

public abstract class UnsentMessage extends InteractionMessage {
    public @Optional String payload_json;
    public @Optional Payload[] attachments;
    public @Optional Message.Reference message_reference;
    public @Optional String[] sticker_ids;
    
    // files[n] todo
    
    protected transient boolean sent0;
    
    protected UnsentMessage() {
        assert this instanceof Message : "This class should not be instantiated directly.";
    }
    
}
