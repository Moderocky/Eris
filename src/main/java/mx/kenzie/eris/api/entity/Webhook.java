package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;

public class Webhook extends Snowflake {

    public int type;
    public @Optional String guild_id, channel_id, token, url;
    public @Optional User user;
    public String name, avatar, application_id;
    public @Optional Guild source_guild;
    public @Optional Channel source_channel;

}
