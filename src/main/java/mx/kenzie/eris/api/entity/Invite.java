package mx.kenzie.eris.api.entity;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.guild.ScheduledEvent;
import mx.kenzie.eris.api.utility.Query;

import java.time.Instant;

public class Invite extends Lazy {
    public String code, created_at;
    public @Optional String expires_at;
    public @Optional Guild guild;
    public @Optional Channel channel;
    public @Optional User inviter, target_user;
    public @Optional int approximate_presence_count, approximate_member_count;
    public @Optional Stage stage_instance;
    public @Optional ScheduledEvent guild_scheduled_event;
    public int max_age, max_uses, uses;
    public @Optional Integer target_type; // STREAM = 1, EMBEDDED_APPLICATION = 2
    public boolean temporary;
    public @Optional Application target_application;

    protected Invite() {
        this.finish();
    }

    public Invite(DiscordAPI api, String code) {
        this.api = api;
        this.api.get("/invites/" + code,
                Query.make("with_counts", true, "with_expiration", true), this)
            .exceptionally(this::error).thenAccept(Invite::finish);
    }

    public Invite delete() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.delete("/invites/" + code).exceptionally(this::error0).thenAccept(this::finish0);
        return this;
    }

    private void finish0(Void unused) {
        this.finish();
    }

    public boolean hasCreation() {
        return created_at != null;
    }

    public boolean expired() {
        return this.hasExpiry() && DiscordAPI.getInstant(expires_at).isBefore(Instant.now());
    }

    public boolean hasExpiry() {
        return expires_at != null;
    }
}
