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
    
    public SelectMenu(String id, Option... options) {
        this();
        this.custom_id = id;
        this.options = options;
    }
    
    public SelectMenu(String id, String placeholder, Option... options) {
        this();
        this.placeholder = placeholder;
        this.custom_id = id;
        this.options = options;
    }
    
    public SelectMenu(String id, int min, int max, String placeholder, Option... options) {
        this();
        this.custom_id = id;
        this.placeholder = placeholder;
        this.min_values = min;
        this.max_values = max;
        this.options = options;
    }
    
    public static Option option() {
        return new Option();
    }
    
    public static Option option(String label, String value) {
        return new Option(label, value);
    }
    
    public static Option option(String label, String value, String description) {
        final Option option = new Option(label, value);
        option.description = description;
        return option;
    }
    
    public static class Option extends Payload {
        public String label, value;
        public @Optional String description;
        public @Optional Payload emoji;
        public @Name("default") boolean is_default;
        
        public Option() {
        }
        
        public Option(String label, String value) {
            this.label = label;
            this.value = value;
        }
        
        public Option label(String label) {
            this.label = label;
            return this;
        }
        
        public Option value(String value) {
            this.value = value;
            return this;
        }
        
        public Option description(String description) {
            this.description = description;
            return this;
        }
        
        public Option isDefault(boolean is_default) {
            this.is_default = is_default;
            return this;
        }
    }
    
}