package mx.kenzie.eris.api.entity.command.callback;

import mx.kenzie.argo.meta.Any;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.message.Component;

public class Modal extends Entity implements Callback {
    
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
    
}
