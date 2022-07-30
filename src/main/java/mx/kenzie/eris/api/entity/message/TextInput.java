package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;

public class TextInput extends Component {
    
    public int style = 1, min_length = 0, max_length = 4000;
    public String label;
    public @Optional String value, placeholder;
    public boolean required = true;
    
    public TextInput(String custom_id, String label) {
        this();
        this.custom_id = custom_id;
        this.label = label;
    }
    
    public TextInput() {
        this.type = 4;
    }
    
    public TextInput style(int style) {
        this.style = style;
        return this;
    }
    
    public TextInput min_length(int min_length) {
        this.min_length = min_length;
        return this;
    }
    
    public TextInput max_length(int max_length) {
        this.max_length = max_length;
        return this;
    }
    
    public TextInput label(String label) {
        this.label = label;
        return this;
    }
    
    public TextInput value(String value) {
        this.value = value;
        return this;
    }
    
    public TextInput placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }
    
    public TextInput required(boolean required) {
        this.required = required;
        return this;
    }
}
