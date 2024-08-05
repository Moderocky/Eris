package mx.kenzie.eris.api.entity.message;

import mx.kenzie.eris.Bot;
import mx.kenzie.eris.api.Expecting;
import mx.kenzie.eris.api.event.Interaction;
import mx.kenzie.eris.utility.Question;
import mx.kenzie.grammar.Optional;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Component extends Expecting<Interaction> {

    public int type;
    public @Optional String custom_id;
    protected transient boolean autoRegistrationSafe;

    public Component() {
    }

    protected Component(String id) {
        this.custom_id = id;
    }

    public static String safeId() {
        // It's very unlikely we could generate the same ID twice within the same millisecond
        final long most = ThreadLocalRandom.current().nextLong();
        return Long.toHexString(most) + Long.toHexString(System.currentTimeMillis());
    }

    public Interaction awaitResult(Bot bot) {
        return this.awaitResult(bot, 300);
    }

    public Interaction awaitResult(Bot bot, int seconds) {
        final long millis = seconds * 1000L;
        this.unready();
        final Question question = new Question(System.currentTimeMillis() + millis, false, this,
            interaction -> {
                Component.this.setResult(interaction);
                Component.this.finish();
            });
        bot.responder().ask(custom_id, question);
        this.trigger();
        this.await(millis);
        return this.getResult();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(custom_id);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Component component) && Objects.equals(custom_id, component.custom_id);
    }

}
