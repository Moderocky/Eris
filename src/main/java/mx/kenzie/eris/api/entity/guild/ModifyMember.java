package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;

import java.time.Instant;

public class ModifyMember extends Lazy {

    public @Optional String[] roles;
    public @Optional String nick, channel_id;
    public @Optional Boolean deaf, mute;
    public @Optional String communication_disabled_until;

    public Instant timeOutExpiry() {
        return DiscordAPI.getInstant(communication_disabled_until);
    }

}
