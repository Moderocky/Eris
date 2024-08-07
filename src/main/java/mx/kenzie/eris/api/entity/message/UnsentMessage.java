package mx.kenzie.eris.api.entity.message;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.entity.Message;

public abstract class UnsentMessage extends InteractionMessage {
    public @Optional String payload_json;
    public @Optional Message.Reference message_reference;
    public @Optional String[] sticker_ids;

    // files[n]

    protected transient boolean sent0;

    protected UnsentMessage() {
        assert this instanceof Message : "This class should not be instantiated directly.";
    }

}
