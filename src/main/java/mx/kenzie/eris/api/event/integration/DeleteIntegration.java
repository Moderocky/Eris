package mx.kenzie.eris.api.event.integration;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Integration;

public class DeleteIntegration extends Integration implements Event {
    public String guild_id, application_id;
}
