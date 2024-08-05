package mx.kenzie.eris.api.entity.message;

import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Name;
import mx.kenzie.grammar.Optional;

public class SelectMenu extends Component {

    public int min_values = 1, max_values = 1;
    public @Optional String placeholder;
    public Option[] options;
    public boolean disabled;

    public SelectMenu(Option... options) {
        this(safeId(), options);
        this.autoRegistrationSafe = true;
    }

    public SelectMenu(String id, Option... options) {
        this(id);
        this.options = options;
    }

    public SelectMenu(String id) {
        super(id);
        this.type = 3;
        this.options = new Option[0];
    }

    public SelectMenu() {
        super();
    }

    public SelectMenu(String id, String placeholder, Option... options) {
        this(id);
        this.placeholder = placeholder;
        this.custom_id = id;
        this.options = options;
    }

    public SelectMenu(String id, int min, int max, String placeholder, Option... options) {
        this(id);
        this.placeholder = placeholder;
        this.min_values = min;
        this.max_values = max;
        this.options = options;
    }

    public SelectMenu(int min, int max, String placeholder, Option... options) {
        this(safeId(), min, max, placeholder, options);
        this.autoRegistrationSafe = true;
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

    public static SelectMenu any(int min, String placeholder, Option... options) {
        final SelectMenu menu = new SelectMenu(min, options.length, placeholder, options);
        menu.autoRegistrationSafe = true;
        return menu;
    }

    public SelectMenu min_values(int min_values) {
        this.min_values = min_values;
        return this;
    }

    public SelectMenu max_values(int max_values) {
        this.max_values = max_values;
        return this;
    }

    public SelectMenu placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public SelectMenu options(Option... options) {
        this.options = options;
        return this;
    }

    public SelectMenu disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
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
