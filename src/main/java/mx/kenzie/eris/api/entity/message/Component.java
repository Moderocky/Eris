package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.data.Payload;

public class Component extends Payload {
    public int type;
    public @Optional String custom_id;
}
