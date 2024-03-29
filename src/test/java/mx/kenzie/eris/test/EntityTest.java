package mx.kenzie.eris.test;

import mx.kenzie.eris.data.Payload;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EntityTest {
    
    @Test
    public void test() throws Throwable {
        final Class<?>[] classes = EntityTest.classes();
        boolean pass = true;
        for (final Class<?> type : classes) {
            if (!Payload.class.isAssignableFrom(type)) continue;
            if (type.isLocalClass()) continue;
            if (type.getEnclosingClass() != type && !Modifier.isStatic(type.getModifiers())) continue;
            try {
                final Constructor<?> constructor = type.getDeclaredConstructor();
                assert constructor != null;
            } catch (NoSuchMethodException ex) {
                System.err.println("No creator function found for: '" + type.getSimpleName() + "'");
                pass = false;
            }
        }
        assert pass : "Creator functions were not identified for some payloads.";
    }
    
    private static Class<?>[] classes() throws IOException {
        final Set<Class<?>> classes = new LinkedHashSet<>();
        try (final JarFile jarFile = new JarFile(new File("target/eris-1.0.0.jar"))) {
            final Enumeration<JarEntry> thing = jarFile.entries();
            while (thing.hasMoreElements()) {
                final JarEntry entry = thing.nextElement();
                if (entry.getName().endsWith(".class")) {
                    final String name = entry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                    try {
                        classes.add(Class.forName(name));
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Unable to find '" + name + '\'');
                    }
                }
            }
            return classes.toArray(new Class[0]);
        }
    }
    
}
