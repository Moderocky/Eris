package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.data.Payload;

public class Application extends Snowflake {
    public int flags;
    public boolean bot_public, bot_require_code_grant;
    public String name, icon, description, summary, verify_key;
    public @Optional String terms_of_service_url, privacy_policy_url, guild_id, primary_sku_id, slug, cover_image, custom_install_url;
    public @Optional String[] rpc_origins, tags;
    public @Optional User owner;
    public @Optional Payload team, install_params;
    
    
}
