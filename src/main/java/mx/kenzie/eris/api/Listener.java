package mx.kenzie.eris.api;

import mx.kenzie.eris.data.Payload;

public interface Listener<Event extends Payload> {

    void on(Event event) throws Throwable;

}
