package mx.kenzie.eris.api.entity.connection;

import mx.kenzie.eris.api.Lazy;
import mx.kenzie.grammar.Optional;

import java.util.LinkedHashMap;
import java.util.Map;

public class Connection extends Lazy {

    public @Optional String platform_name, platform_username;
    public @Optional Map<String, Object> metadata = new LinkedHashMap<>();
    public transient Map<String, Object> __data;

}
