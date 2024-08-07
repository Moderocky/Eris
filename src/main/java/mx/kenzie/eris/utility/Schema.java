package mx.kenzie.eris.utility;

import mx.kenzie.eris.Bot;
import mx.kenzie.grammar.Grammar;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public record Schema(Field... fields) {

    private static final Map<Class<?>, Schema> cache = new WeakHashMap<>();

    public void copy(Object from, Object to) {
        try {
            for (Field field : fields) {
                if (!field.getType().isInstance(from)) continue;
                if (!field.getType().isInstance(to)) continue;
                field.set(to, field.get(from));
            }
        } catch (IllegalAccessException exception) {
            Bot.handle(exception);
        }
    }

    public static Schema of(Class<?> type) {
        if (cache.containsKey(type)) return cache.get(type);
        var grammar = new Grammar() {
            @Override
            public boolean shouldSkip(Field field) {
                return super.shouldSkip(field);
            }
        };
        final Set<Field> fields = new LinkedHashSet<>();
        for (Field field : type.getFields()) {
            if (grammar.shouldSkip(field)) continue;
            fields.add(field);
        }
        Class<?> current = type;
        do for (Field field : type.getDeclaredFields()) {
            if (grammar.shouldSkip(field)) continue;
            fields.add(field);
        } while ((current = current.getSuperclass()) != Object.class);
        for (Field field : fields) field.trySetAccessible();
        final Schema schema = new Schema(fields.toArray(new Field[0]));
        Schema.cache.put(type, schema);
        return schema;
    }

}
