package mx.kenzie.eris.api.utility;

import java.util.LinkedHashMap;
import java.util.Map;

public interface Query {
    static Map<String, Object> make(Object... objects) {
        final Map<String, Object> map = new LinkedHashMap<>();
        final int length = objects.length - 1;
        int x = 0;
        while (x < length) {
            final Object key = objects[x++];
            final Object value = objects[x++];
            map.put(key.toString(), value);
        }
        return map;
    }
}
