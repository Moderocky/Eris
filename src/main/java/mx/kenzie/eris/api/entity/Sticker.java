package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;

public class Sticker extends Snowflake {
    public @Optional String pack_id, asset, guild_id;
    public String name, description, tags;
    public int type, format_type, sort_value = -1;
    public boolean available;
    public User user;
}
