package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.data.Payload;

public class SelectMenu extends Component {
    
    public int min_values = 1, max_values = 1;
    public @Optional String placeholder;
    public Option[] options;
    public boolean disabled;
    
    public SelectMenu() {
        this.type = 3;
        this.options = new Option[0];
    }
    
    public SelectMenu(Option... options) {
        this();
        this.options = options;
    }
    
    public static class Option extends Payload {
        public String label, value;
        public @Optional String description;
        public @Optional Payload emoji;
        public @Name("default") boolean is_default;
        
    }
    
}
