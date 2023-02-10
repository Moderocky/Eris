package mx.kenzie.eris.api.entity.command.callback;

import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.entity.command.Option;

public class Autocomplete extends Entity implements Callback {

    public Option.Choice<?>[] choices;

    public Autocomplete() {
    }

    public Autocomplete(Option.Choice<?>... choices) {
        this.choices = choices;
    }

    public static Autocomplete of(String... strings) {
        assert strings.length > 1;
        assert (strings.length & 1) == 0;
        final Option.Choice<?>[] choices = new Option.Choice[strings.length / 2];
        for (int i = 0; i < strings.length; i++) {
            final String key = strings[i++];
            final String value = strings[i];
            final Option.Choice<?> choice = new Option.Choice<>(key, value);
            choices[(i - 1) / 2] = choice;
        }
        return new Autocomplete(choices);
    }

    @Override
    public int interactionResponseType() {
        return 8;
    }

}
