package mx.kenzie.eris.api.entity.message;

import mx.kenzie.grammar.Optional;

public class Attachment extends UnsentAttachment {
    public @Optional String proxy_url;
    public @Optional Boolean ephemeral;

}
