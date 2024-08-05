package mx.kenzie.eris.api.entity;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.Lazy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public class Snowflake extends Lazy implements Comparable<Snowflake> {

    public @Optional String id;
    private transient long id0;

    protected Snowflake() {
    }

    public Snowflake(long id) {
        this.id0 = id;
        this.id = Long.toString(id);
    }

    public Snowflake(String id) {
        this.id = id;
    }

    public static Snowflake from(Instant instant) {
        final long timestamp = instant.toEpochMilli();
        final long snowflake = (timestamp - 1420070400000L) << 22;
        return new Snowflake(snowflake);
    }

    public String mention() {
        if (this instanceof Role) return "<@&" + id + ">";
        if (this instanceof Channel) return "<#" + id + ">";
        return "<@" + id + ">";
    }

    @Override
    public String debugName() {
        return "<" + this.getClass().getSimpleName() + ":" + this.id + ">";
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Snowflake snowflake) && Objects.equals(this.id, snowflake.id);
    }

    @Override
    public int hashCode() {
        if (id != null) return Long.hashCode(this.id());
        return super.hashCode();
    }

    public Instant getInstant() {
        final long timestamp = this.timestamp();
        return Instant.ofEpochMilli(timestamp);
    }

    public long timestamp() {
        final long snowflake = this.id();
        return (snowflake >> 22) + 1420070400000L;
    }

    @Contract(pure = true)
    public long id() {
        if (id0 < 1) return id0 = Long.parseLong(id);
        return id0;
    }

    @Override
    public int compareTo(@NotNull Snowflake other) {
        final long x = this.timestamp(), y = other.timestamp();
        return Long.compare(x, y);
    }

}
