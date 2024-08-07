package mx.kenzie.eris.api.entity.connection;

import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.error.DiscordException;

import java.util.concurrent.CompletableFuture;

public class AccessToken extends Lazy {

    public String access_token, token_type, refresh_token, scope;
    public int expires_in;
    protected transient long expiry;
    protected transient Access user;

    @Override
    public void finish() {
        assert expires_in != 0;
        this.expiry = System.currentTimeMillis() + expires_in * 1000L;
        super.finish();
    }

    public <Type> CompletableFuture<Type> request(String mode, String path, String body, Type result) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return this.api.requestRaw(mode, path, body, result,
            "Authorization", "Bearer " + this.accessToken());
    }

    public Connection getConnection() {
        final Connection connection = new Connection();
        this.request("GET", "/users/@me/applications/" + api.getApplicationID() + "/role-connection", null, connection)
            .exceptionally(connection::error).thenAccept(Lazy::finish);
        return connection;
    }

    public Connection updateMetadata(Connection connection) {
        connection.unready();
        this.request("PUT", "/users/@me/applications/" + api.getApplicationID() + "/role-connection",
                connection.toJson(null), connection)
            .exceptionally(connection::error).thenAccept(Lazy::finish);
        return connection;
    }

    public synchronized Access getUserData() {
        if (user != null) return user;
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Access user = this.user = new Access();
        this.request("GET", "/oauth2/@me", null, user)
            .exceptionally(user::error)
            .thenAccept(Lazy::finish);
        return user;
    }

    public void foo() {
    }

    public void refresh() {
        if (!this.hasExpired()) return;
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        if (!api.hasClientSecret()) throw new DiscordException("No client secret registered with bot");
        this.unready();
        this.api.post("/oauth2/token", new Payload() {
                public final String
                    client_id = api.getSelf().id, client_secret = api.getClientSecret(),
                    grant_type = "refresh_token", refresh_token = AccessToken.this.refresh_token;
            }.toJson(null), this, "Content-Type", "application/json")
            .exceptionally(this::error).thenAccept(Lazy::finish);
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() >= expiry;
    }

    public String accessToken() {
        this.refresh();
        return access_token;
    }

}
