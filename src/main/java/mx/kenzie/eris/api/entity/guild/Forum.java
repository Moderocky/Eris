package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Channel;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.data.Payload;

public class Forum extends Channel {
    
    public @Optional Tag[] available_tags = new Tag[0];
    public @Optional Integer default_sort_order;
    public @Optional Payload default_reaction_emoji;
    
    public static class Tag extends Snowflake {
        public String name;
        public boolean moderated;
        public String emoji_id;
        public String emoji_name;
    }
    
}
