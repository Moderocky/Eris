package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;

public class TextInput extends Component {
    
    public int style = 1, min_length = 0, max_length = 4000;
    public String label;
    public @Optional String value, placeholder;
    public boolean required = true;
    
    public TextInput() {
        this.type = 4;
    }
}
