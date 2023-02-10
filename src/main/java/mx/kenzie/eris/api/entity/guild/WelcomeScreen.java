package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.data.Payload;

public class WelcomeScreen extends Lazy {
    public boolean enabled = true;
    public String description;
    public Channel[] welcome_channels = new Channel[0];

    public static class Channel extends Payload {
        public String channel_id, description, emoji_id, emoji_name;
    }

}
