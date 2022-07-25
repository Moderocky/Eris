package mx.kenzie.eris.api.entity.command.callback;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.api.Expecting;
import mx.kenzie.eris.api.entity.message.ActionRow;
import mx.kenzie.eris.api.entity.message.Component;
import mx.kenzie.eris.api.event.Interaction;

import java.util.concurrent.atomic.AtomicInteger;

public class Modal extends Expecting<Interaction> implements Callback {
    
    public String custom_id, title;
    public @Any Component[] components;
    
    public Modal() {
    }
    
    @Override
    public int interactionResponseType() {
        return 9;
    }
    
    public Modal(String id, String title, Component... components) {
        assert components.length > 0 && components.length < 6;
        this.custom_id = id;
        this.title = title;
        this.components = components;
    }
    
    @Override
    public void expectResult() {
        Bot.INLINE_CALLBACKS.set(custom_id, this);
        this.trigger();
    }
    
    private static final AtomicInteger COUNTER = new AtomicInteger();
    
    public static Modal auto(String title, Component... components) {
        return Modal.auto(null, title, components);
    }
    
    public static Modal auto(String id, String title, Component... components) {
        if (id == null) id = "auto_modal_id_" + COUNTER.incrementAndGet();
        final Modal modal = new Modal();
        modal.custom_id = id;
        modal.title = title;
        if (components.length == 0) return modal;
        if (!(components[0] instanceof ActionRow)) { // auto-pack
            final Component[] array = new Component[components.length];
            for (int i = 0; i < array.length; i++) array[i] = new ActionRow(components[i]);
            components = array;
        }
        modal.components = components;
        modal.expectResult();
        return modal;
    }
}
