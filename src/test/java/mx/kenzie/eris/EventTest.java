package mx.kenzie.eris;

import mx.kenzie.eris.api.Event;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EventTest {
    
    @Test
    public void test() throws Throwable {
        final Class<?>[] classes = EventTest.classes();
        for (final Class<?> type : classes) {
            assert type.getEnclosingClass() != type || Event.class.isAssignableFrom(type) : "Non-event class in event package.";
            if (!Event.class.isAssignableFrom(type)) continue;
            assert Bot.EVENT_LIST.containsValue(type) : "Unregistered event " + type.getSimpleName();
        }
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
                    if (!name.startsWith("mx.kenzie.eris.api.event")) continue;
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
