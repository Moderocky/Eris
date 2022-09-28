package mx.kenzie.eris.api.magic;

public interface GuildScheduledEvent {
    interface Status {
        int
            SCHEDULED = 1,
            ACTIVE = 2,
            COMPLETED = 3,
            CANCELED = 4;
    }
    
    interface EntityTypes {
        int
            STAGE_INSTANCE = 1,
            VOICE = 2,
            EXTERNAL = 3;
    }
}
