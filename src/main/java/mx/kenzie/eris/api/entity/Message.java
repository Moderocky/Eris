package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.message.*;
import mx.kenzie.eris.api.utility.RequestBuilder;
import mx.kenzie.eris.data.Payload;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message extends UnsentMessage {
    public String channel_id, timestamp, edited_timestamp;
    public @Optional String webhook_id, application_id;
    public User author;
    public boolean mention_everyone, pinned;
    public User[] mentions;
    public String[] mention_roles;
    public @Optional Channel[] mention_channels;
    public @Optional Payload[] reactions, sticker_items;
    public @Optional Object nonce;
    public int type;
    public @Optional Integer position;
    public @Optional Sticker[] stickers;
    public @Optional Interaction interaction;
    public @Optional Activity activity;
    public @Optional Application application;
    public @Optional Message referenced_message;
    public @Optional Thread thread;

    public Message() {
    }

    public Message(String content) {
        this.content = content;
    }

    public Message(String content, Embed... embeds) {
        this.content = content;
        this.embeds = embeds;
    }

    public Message(String content, ActionRow... rows) {
        this.content = content;
        this.components = rows;
    }

    public Message(String content, Button... buttons) {
        this.content = content;
        this.components = new Component[]{new ActionRow(buttons)};
    }

    public Message(Embed... embeds) {
        this.embeds = embeds;
    }

    public Message(ActionRow... rows) {
        this.components = rows;
    }

    public Message send(Channel channel) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.sendMessage(channel, this);
    }

    public void addAttachment(String filename, String content) {
        this.addAttachment0(filename, content);
    }

    private void addAttachment0(String name, Object content) {
        final List<Attachment> list;
        final boolean has = attachments != null;
        if (has) list = new ArrayList<>(Arrays.asList(attachments));
        else list = new ArrayList<>();
        final Attachment attachment = new Attachment();
        attachment.id = String.valueOf(list.size());
        attachment.filename = name;
        attachment.content = content;
        list.add(attachment);
        this.attachments = list.toArray(new Attachment[0]);
    }

    public void addAttachment(File file) {
        this.addAttachment0(file.getName(), file);
    }

    public Message pin() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.request("PUT", "/channels/" + channel_id + "/pins/" + id, null, null)
            .exceptionally(this::error).thenRun(this::finish);
        return this;
    }

    public Message unpin() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.api.request("DELETE", "/channels/" + channel_id + "/pins/" + id, null, null)
            .exceptionally(this::error).thenRun(this::finish);
        return this;
    }

    public RequestBuilder<Thread> createThread(String name) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        final Thread thread = new Thread();
        final RequestBuilder<Thread> builder = new RequestBuilder<>(api, "POST", "/channels/" + channel_id + "/messages/" + id + "/threads", thread);
        builder.set("name", name);
        return builder;
    }

    public Message edit() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.patch("/channels/" + channel_id + "/messages/" + id, Json.toJson(this, UnsentMessage.class, null), this)
            .exceptionally(this::error).thenAccept(Lazy::finish);
        return this;
    }

    public void delete() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        this.unready();
        this.api.delete("/channels/" + channel_id + "/messages/" + id).exceptionally(this::error0)
            .thenRun(this::finish);
    }

    public Message denyAllMentions() {
        this.allowed_mentions = new Mentions();
        this.allowed_mentions.parse = new String[0];
        return this;
    }

    public Message withFlag(int flag) {
        this.flags |= flag;
        return this;
    }

    public Message withoutFlag(int flag) {
        this.flags &= flag;
        return this;
    }

    public Message reply(String message) {
        return this.reply(new Message(message));
    }

    public Message reply(Message message) {
        message.message_reference = new Reference();
        message.message_reference.message_id = id;
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.sendMessage(channel_id, message);
    }

    public boolean isFromSelf() {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return (author.equals(api.getSelf()));
    }

    public static class Interaction extends Snowflake {
        public int type;
        public String name;
        public User user;
        public @Optional Member member;
    }

    public static class Activity extends Payload {
        public int type;
        public @Optional String party_id;
    }

    public static class Reference extends Payload {
        public boolean fail_if_not_exists;
        public @Optional String message_id, channel_id, guild_id;
    }

    public static class Mentions extends Payload {
        public @Optional String[] parse, users;
    }

}
