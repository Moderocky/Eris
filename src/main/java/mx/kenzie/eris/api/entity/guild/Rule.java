package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.data.Payload;

public class Rule extends Snowflake {
    public String guild_id, name, creator_id;
    public int event_type, trigger_type;
    public Trigger trigger_metadata;
    public Action[] actions; // todo
    public boolean enabled;
    public String[] exempt_roles, exempt_channels;


    public static class Trigger extends Payload {
        public String[] keyword_filter, allow_list;
        public int[] presets;
        public int mention_total_limit;
    }

    public static class Action extends Payload {
        public int type;
        public @Optional Metadata metadata;
    }

    public static class Metadata extends Payload {
        public String channel_id;
        public int duration_seconds;
    }
}
