package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.api.Expecting;
import mx.kenzie.eris.api.event.Interaction;

public class Component extends Expecting<Interaction> {
    public int type;
    public @Optional String custom_id;

    @Override
    public void expectResult() {
        Bot.INLINE_CALLBACKS.set(custom_id, this);
        this.trigger();
    }
}
