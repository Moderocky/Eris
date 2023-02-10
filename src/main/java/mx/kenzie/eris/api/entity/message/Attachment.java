package mx.kenzie.eris.api.entity.message;

import mx.kenzie.argo.meta.Optional;

public class Attachment extends UnsentAttachment {
    public @Optional String proxy_url;
    public @Optional Boolean ephemeral;

}
