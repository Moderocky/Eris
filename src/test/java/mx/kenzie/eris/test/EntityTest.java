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
        try (JarFile jarFile = new JarFile(new File("target/eris-1.0.0.jar"))) {
            final Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                final JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    final String className = jarEntry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Unable to find '" + className + '\'');
                    }
                }
            }
            return classes.toArray(new Class[0]);
        }
    }
    
}
