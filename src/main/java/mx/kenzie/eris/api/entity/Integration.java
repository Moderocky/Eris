package mx.kenzie.eris.api.entity;

import mx.kenzie.grammar.Optional;

public class Integration extends Snowflake {
    public String name, type;
    public @Optional String role_id, synced_at;
    public @Optional boolean enabled, syncing, enable_emoticons, revoked;
    public @Optional int expire_grace_period, expire_behavior, subscriber_count;
    public @Optional User user;
    public Account account;
    public Application application;
    public String[] scopes = new String[0];

    public static class Account extends Snowflake {
        public String name;
    }

    public static class Application extends Snowflake {
        public String name, icon, description;
        public @Optional User bot;
    }
}
