package mx.kenzie.eris.api.command;

import mx.kenzie.eris.api.event.Interaction;

public interface CommandHandler {
    
    void on(Interaction interaction) throws Throwable;
    
}
