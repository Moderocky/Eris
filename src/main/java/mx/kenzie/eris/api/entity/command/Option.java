package mx.kenzie.eris.api.entity.command;

import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.magic.OptionType;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Optional;
import org.intellij.lang.annotations.MagicConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Option extends Entity {

    public @MagicConstant(flagsFromClass = OptionType.class) int type;
    public @Optional boolean required = true, autocomplete;
    public String name, description;
    public @Optional Payload name_localizations, description_localizations;
    public @Optional Choice<?>[] choices;
    public @Optional Option[] options;
    public @Optional int[] channel_types;
    public @Optional Number min_value, max_value;
    public @Optional Integer min_length, max_length;

    public Option() {
    }

    public Option(String name, String description, int type) {
        this.name = name;
        this.description = description;
        this.type = type;
        switch (type) {
            case OptionType.SUB_COMMAND, OptionType.SUB_COMMAND_GROUP:
                this.required = false;
        }
    }

    public Option(String name, String description, int type, Choice<?>... choices) {
        this(name, description, type);
        this.choices = choices;
    }

    public Option(String name, String description, int type, Option... options) {
        this(name, description, type);
        this.options = options;
    }

    public static <Type> Choice<Type> choice(String name, Type value) {
        return new Choice<>(name, value);
    }

    @SafeVarargs
    public static Option ofBooleans(String name, String description, Choice<Boolean>... choices) {
        return new Option(name, description, OptionType.BOOLEAN, choices);
    }

    @SafeVarargs
    public static Option ofStrings(String name, String description, Choice<String>... choices) {
        return new Option(name, description, OptionType.STRING, choices);
    }

    public static Option subGroup(String name, String description, Option... options) {
        return new Option(name, description, OptionType.SUB_COMMAND_GROUP, options);
    }

    public static Option subCommand(String name, String description, Option... options) {
        return new Option(name, description, OptionType.SUB_COMMAND, options);
    }

    public static Option of(String name, String description,
                            @MagicConstant(flagsFromClass = OptionType.class) int type) {
        return new Option(name, description, type);
    }

    public Option type(int type) {
        this.type = type;
        switch (type) {
            case OptionType.SUB_COMMAND, OptionType.SUB_COMMAND_GROUP:
                this.required = false;
        }
        return this;
    }

    public Option required(boolean required) {
        this.required = required;
        return this;
    }

    public Option name(String name) {
        this.name = name;
        return this;
    }

    public Option description(String description) {
        this.description = description;
        return this;
    }

    public Option choices(Choice<?>... choices) {
        this.choices = choices;
        return this;
    }

    public Option options(Option... options) {
        this.options = options;
        return this;
    }

    public Option channel_types(int... channel_types) {
        this.channel_types = channel_types;
        return this;
    }

    public Option min_value(Number min_value) {
        this.min_value = min_value;
        return this;
    }

    public Option max_value(Number max_value) {
        this.max_value = max_value;
        return this;
    }

    public Option min_length(Integer min_length) {
        this.min_length = min_length;
        return this;
    }

    public Option max_length(Integer max_length) {
        this.max_length = max_length;
        return this;
    }

    public Option autocomplete(boolean autocomplete) {
        this.autocomplete = autocomplete;
        return this;
    }

    public Option merge(Option... others) {
        final List<Option> options = new ArrayList<>();
        String description = this.description;
        for (Option other : others) {
            if (!Objects.equals(other.name, this.name)) continue;
            if (description == null) description = other.description;
            options.addAll(List.of(other.options));
        }
        return new Option(name, description, type, options.toArray(new Option[0]));
    }

    public static class Choice<Type> extends Payload {

        public String name;
        public @Optional Payload name_localizations;
        public Type value;

        public Choice() {
        }

        public Choice(String name, Type value) {
            this.name = name;
            this.value = value;
        }

    }

}
