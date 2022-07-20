package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.data.Payload;

public class Button extends Component {
    
    public int style = 1;
    public @Optional String label, url;
    public @Optional Payload emoji;
    public boolean disabled;
    
    
    public Button() {
        this.type = 2;
    }
    
    public Button(String id, String label) {
        this(id, label, 1);
    }
    
    public Button(String id, String label, int style) {
        this();
        this.style = style;
        this.custom_id = id;
        this.label = label;
    }
    
    public Button style(int style) {
        this.style = style;
        return this;
    }
    
    public Button label(String label) {
        this.label = label;
        return this;
    }
    
    public Button url(String url) {
        this.url = url;
        return this;
    }
    
    public Button emoji(Payload emoji) {
        this.emoji = emoji;
        return this;
    }
    
    public Button disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }
}
