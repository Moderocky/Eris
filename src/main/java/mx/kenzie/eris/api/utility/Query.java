package mx.kenzie.eris.api.utility;

import java.util.LinkedHashMap;
import java.util.Map;

public interface Query extends Map<String, Object> {
    static Query make(Object... objects) {
        final Query map = new MakeQuery();
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

class MakeQuery extends LinkedHashMap<String, Object> implements Query {

}
