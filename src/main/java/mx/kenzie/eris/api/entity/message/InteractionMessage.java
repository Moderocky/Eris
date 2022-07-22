package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Embed;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.entity.command.callback.Callback;
import mx.kenzie.eris.data.Payload;

public abstract class InteractionMessage extends Snowflake implements Callback {
    
    public int flags;
    public @Optional Boolean tts;
    public @Optional String content;
    public @Optional
    @Any Component[] components;
    public @Optional Payload[] attachments;
    public @Optional Payload allowed_mentions;
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
