package mx.kenzie.eris.api.entity.message;

import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.grammar.Optional;

public abstract class UnsentMessage extends InteractionMessage {

    public @Optional String payload_json;
    public @Optional Message.Reference message_reference;
    public @Optional String[] sticker_ids;

    protected transient boolean sent0;

    protected UnsentMessage() {
        assert this instanceof Message : "This class should not be instantiated directly.";
    }

    public void append(String text) {
        if (content == null) content = text;
        else content += text;
    }

}
