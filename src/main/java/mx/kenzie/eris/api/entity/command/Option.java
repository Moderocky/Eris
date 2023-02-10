package mx.kenzie.eris.api.entity.command;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Entity;
import mx.kenzie.eris.api.magic.OptionType;
import mx.kenzie.eris.data.Payload;
import org.intellij.lang.annotations.MagicConstant;

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
    }

    public Option(String name, String description, int type, Choice<?>... choices) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.choices = choices;
    }

    public Option(String name, String description, int type, Option... options) {
        this.name = name;
        this.description = description;
        this.type = type;
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

    public static Option of(String name, String description, @MagicConstant(flagsFromClass = OptionType.class) int type) {
        return new Option(name, description, type);
    }

    public Option type(int type) {
        this.type = type;
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

//    type	one of application command option type	Type of option
//    name	string	1-32 character name
//    name_localizations?	?dictionary with keys in available locales	Localization dictionary for the name field. Values follow the same restrictions as name
//    description	string	1-100 character description
//    description_localizations?	?dictionary with keys in available locales	Localization dictionary for the description field. Values follow the same restrictions as description
//    required?	boolean	If the parameter is required or optional--default false
//    choices?	array of application command option choice	Choices for STRING, INTEGER, and NUMBER types for the user to pick from, max 25
//    options?	array of application command option	If the option is a subcommand or subcommand group type, these nested options will be the parameters
//    channel_types?	array of channel types	If the option is a channel type, the channels shown will be restricted to these types
//    min_value?	integer for INTEGER options, double for NUMBER options	If the option is an INTEGER or NUMBER type, the minimum value permitted
//    max_value?	integer for INTEGER options, double for NUMBER options	If the option is an INTEGER or NUMBER type, the maximum value permitted
//    min_length?	integer	For option type STRING, the minimum allowed length (minimum of 0)
//    max_length?	integer	For option type STRING, the maximum allowed length (minimum of 1)
//    autocomplete? *	boolean	If autocomplete interactions are enabled for this STRING, INTEGER, or NUMBER type option

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
