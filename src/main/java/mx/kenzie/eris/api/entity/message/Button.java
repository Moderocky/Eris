package mx.kenzie.eris.api.entity.message;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.magic.ButtonStyle;
import mx.kenzie.eris.data.Payload;

import java.util.concurrent.atomic.AtomicInteger;

public class Button extends Component {

    protected static final AtomicInteger COUNTER = new AtomicInteger();
    public int style = 1;
    public @Optional String label, url;
    public @Optional Payload emoji;
    public boolean disabled;

    public Button(String id, String label) {
        this(id, label, 1);
    }

    public Button(String id, String label, int style) {
        this();
        this.style = style;
        this.custom_id = id;
        this.label = label;
    }

    public Button() {
        this.type = 2;
    }

    public static Button auto(String label) {
        return Button.auto(null, label, ButtonStyle.PRIMARY);
    }

    public static Button auto(String id, String label, int type) {
        if (id == null) id = "auto_button_id_" + COUNTER.incrementAndGet();
        final Button button = new Button(id, label, type);
        button.expectResult();
        return button;
    }

    public static Button auto(String label, int type) {
        return Button.auto(null, label, type);
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
