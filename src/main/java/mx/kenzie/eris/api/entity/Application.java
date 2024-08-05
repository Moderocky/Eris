package mx.kenzie.eris.api.entity;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.magic.ApplicationFlags;
import mx.kenzie.eris.api.magic.Permission;
import mx.kenzie.eris.data.Payload;

public class Application extends Snowflake implements ApplicationFlags {
    public @Optional int flags;
    public boolean bot_public, bot_require_code_grant;
    public String name, icon, description, verify_key;
    public @Optional String terms_of_service_url, privacy_policy_url, guild_id, primary_sku_id, slug, cover_image, custom_install_url;
    public @Optional String[] rpc_origins, tags;
    public @Optional User owner;
    public @Optional Team team;
    public @Optional InstallParams install_params;
    public @Deprecated String summary;

    @Override
    public int flags() {
        return flags;
    }

    public static class InstallParams extends Payload implements Permission {
        public String[] scopes;
        public String permissions;

        private transient long permissions0;

        @Override
        public long permissions() {
            if (permissions == null) return permissions0;
            if (permissions0 == 0) permissions0 = Long.parseLong(permissions);
            return permissions0;
        }
    }

}
