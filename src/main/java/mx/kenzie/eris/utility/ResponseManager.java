package mx.kenzie.eris.utility;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.event.Interaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ResponseManager {

    protected final Bot bot;
    protected final DiscordAPI api;
    protected final Map<String, Question> responses;

    public ResponseManager(Bot bot, DiscordAPI api) {
        this.bot = bot;
        this.api = api;
        this.responses = new ConcurrentHashMap<>();
        this.bot.scheduler().schedule(this::clean, 60, TimeUnit.SECONDS);
    }

    protected void clean() {
        final long now = System.currentTimeMillis();
        this.responses.values().removeIf(question -> question.expiry() < now);
    }

    public boolean consume(Interaction interaction) throws Throwable {
        if (interaction.data == null) return false;
        final String id = interaction.data.custom_id;
        if (id == null) return false;
        final Question question = responses.get(id);
        if (question == null || question.hasExpired()) return false;
        if (!question.allowMultipleResponses()) responses.remove(id);
        question.listener().on(interaction);
        return true;
    }

    public void ask(String id, Question question) {
        this.responses.put(id, question);
    }

}
