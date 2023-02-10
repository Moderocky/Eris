package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Embed;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.entity.command.callback.Callback;

public abstract class InteractionMessage extends Snowflake implements Callback {

    public @Optional int flags;
    public @Optional boolean tts;
    public @Optional String content;
    public @Optional
    @Any Component[] components;
    public @Optional Attachment[] attachments;
    public @Optional Message.Mentions allowed_mentions;
    public @Optional Embed[] embeds;

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

    @Override
    public int interactionResponseType() {
        return 4;
    }
}
