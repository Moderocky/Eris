package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Snowflake;

public abstract class UnsentAttachment extends Snowflake {
    public @Optional String filename, description, content_type, url;
    public @Optional Integer size, height, width;
    public transient Object content;
}
