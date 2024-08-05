package mx.kenzie.eris.utility;

import mx.kenzie.eris.api.Listener;
import mx.kenzie.eris.api.entity.message.Component;
import mx.kenzie.eris.api.event.Interaction;

public record Question(long expiry, boolean allowMultipleResponses, Component component, Listener<Interaction> listener) {

    public boolean hasExpired() {
        return expiry < System.currentTimeMillis();
    }

}
