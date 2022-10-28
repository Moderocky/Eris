package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;

public class Sticker extends Snowflake {
    public @Optional String pack_id, asset, guild_id;
    public String name, description, tags;
    public int type, format_type;
    public @Optional int sort_value = -1;
    public @Optional boolean available;
    public @Optional User user;
}
