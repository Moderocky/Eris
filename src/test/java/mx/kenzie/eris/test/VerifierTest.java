package mx.kenzie.eris.test;

import mx.kenzie.argo.meta.Name;
import mx.kenzie.eris.data.Payload;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class VerifierTest {
    
    protected void verify(Class<? extends Payload> entity, String schema) {
        final String[] lines = schema.split("\n");
        final Map<String, Class<?>> fields = new LinkedHashMap<>();
        final List<String> missing = new ArrayList<>();
        for (final String line : lines) {
            final String[] parts = line.split("\t");
            final String name = parts[0]
                .replace('*', ' ')
                .replace('?', ' ').trim();
            if (name.isEmpty()) continue;
            final String type = parts[1];
            fields.put(name, switch (type) {
                case "snowflake", "string", "?string" -> String.class;
                default -> null;
            });
        }
        final Map<String, Class<?>> real = new LinkedHashMap<>();
        for (final Field field : entity.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (Modifier.isPrivate(field.getModifiers())) continue;
            if (field.isAnnotationPresent(Name.class))
                real.put(field.getAnnotation(Name.class).value(), field.getType());
            else real.put(field.getName(), field.getType());
        }
        for (final Map.Entry<String, Class<?>> entry : fields.entrySet()) {
            if (real.containsKey(entry.getKey())) {
                if (entry.getValue() == null) continue;
                else if (entry.getValue().isAssignableFrom(real.get(entry.getKey()))) continue;
                throw new AssertionError("Class " + entity.getSimpleName() + " field " + real.get(entry.getKey())
                    .getSimpleName() + " did not match " + entry.getValue().getSimpleName());
            } else missing.add(entry.getKey());
        }
        assert missing.size() == 0 : "Class " + entity.getSimpleName() + " was missing fields " + missing;
    }
    
}
