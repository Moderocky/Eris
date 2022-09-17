package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Snowflake;

public class Forum extends Channel {
    
    public Tag[] available_tags = new Tag[0];
    
    public static class Tag extends Snowflake {
        public String name;
        public boolean moderated;
        public String emoji_id;
        public String emoji_name;
    }
    
}
