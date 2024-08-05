package mx.kenzie.eris.api.entity.message;

import mx.kenzie.grammar.Any;

public class ActionRow extends Component {
    public @Any Component[] components;

    public ActionRow() {
        this(new Component[0]);
    }

    public ActionRow(Component... components) {
        this.components = components;
        this.type = 1;
    }

}
