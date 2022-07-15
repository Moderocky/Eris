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
    
}
