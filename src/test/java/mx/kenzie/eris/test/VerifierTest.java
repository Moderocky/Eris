package mx.kenzie.eris.test;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Guild;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.data.Payload;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class VerifierTest {
    
    protected void verify(Class<? extends Payload> entity, String schema) {
        final String[] lines = schema.split("\n");
        final Set<FieldExpectation> expectations = new LinkedHashSet<>();
        final List<String> missing = new ArrayList<>();
        final List<String> optionals = new ArrayList<>();
        for (final String line : lines) {
            final String[] parts = line.split("\t");
            final boolean deprecated = parts[0].contains("(deprecated)");
            final String name = parts[0]
                .replace("(deprecated)", "")
                .replace('*', ' ')
                .replace('?', ' ').trim();
            if (name.isEmpty()) continue;
            final String type = parts[1]
                .replace("partial ", "")
                .replace('?', ' ')
                .replace('*', ' ').trim();
            final OptionalState state = parts[0].contains("?") ? OptionalState.KEY :
                parts[1].contains("?") ? OptionalState.VALUE : OptionalState.NONE;
            final Class<?> value = switch (state) {
                case KEY, VALUE -> switch (type) {
                    case "snowflake", "string", "ISO8601 timestamp" -> String.class;
                    case "user object" -> User.class;
                    case "guild object" -> Guild.class;
                    case "channel object" -> Channel.class;
                    default -> null;
                };
                case NONE -> switch (type) {
                    case "snowflake", "string", "ISO8601 timestamp" -> String.class;
                    case "boolean" -> boolean.class;
                    case "integer" -> int.class;
                    case "user object" -> User.class;
                    case "guild object" -> Guild.class;
                    case "channel object" -> Channel.class;
                    default -> null;
                };
            };
            expectations.add(new FieldExpectation(
                name, value, state, deprecated
            ));
        }
        final Map<String, Field> real = new LinkedHashMap<>();
        for (final Field field : entity.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (Modifier.isPrivate(field.getModifiers())) continue;
            if (field.isAnnotationPresent(Name.class))
                real.put(field.getAnnotation(Name.class).value(), field);
            else real.put(field.getName(), field);
        }
        for (final FieldExpectation expectation : expectations) {
            if (real.containsKey(expectation.name)) {
                final Field field = real.get(expectation.name);
                if (expectation.deprecated && !field.isAnnotationPresent(Deprecated.class))
                    optionals.add("Class " + entity.getSimpleName() + " field " + field.getName() +
                        " (" + field.getType().getSimpleName() + ") was missing deprecated status.");
                if (expectation.optional == OptionalState.KEY && !field.isAnnotationPresent(Optional.class))
                    optionals.add("Class " + entity.getSimpleName() + " field " + field.getName() +
                        " (" + field.getType().getSimpleName() + ") was missing optional status.");
                final Class<?> value = field.getType();
                if (expectation.type == null) continue;
                else if (expectation.type.isAssignableFrom(value)) continue;
                throw new AssertionError("Class " + entity.getSimpleName() + " field " + expectation.name + " " + value
                    .getSimpleName() + " did not match " + expectation.type.getSimpleName());
            } else missing.add(expectation.name);
            
        }
        if (missing.size() > 0)
            System.err.println("Class " + entity.getSimpleName() + " was missing fields " + missing);
        if (optionals.size() > 0) for (String optional : optionals) System.err.println(optional);
        assert missing.size() == 0 && optionals.size() == 0;
    }
    
    enum OptionalState {
        KEY, VALUE, NONE
    }
    
    record FieldExpectation(String name, Class<?> type, OptionalState optional, boolean deprecated) {
    }
    
}
