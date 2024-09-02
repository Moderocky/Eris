package mx.kenzie.eris.api.entity;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.eris.utility.ArrayMerge;
import mx.kenzie.grammar.Optional;

import java.io.File;
import java.io.InputStream;

public class Embed extends Entity {

    public @Optional String title, type, description, url, timestamp;
    public @Optional Integer color;

    public @Optional Footer footer = new Footer();
    public @Optional Image image = new Image();
    public @Optional Thumbnail thumbnail = new Thumbnail();
    public @Optional Video video = new Video();
    public @Optional Provider provider = new Provider();
    public @Optional Author author = new Author();
    public @Optional Field[] fields = new Field[0];

    public Embed() {
    }

    public Embed(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Embed(Json json) {
        json.toObject(this, Embed.class);
    }

    public Embed(InputStream stream) {
        new Json(stream).toObject(this, Embed.class);
    }

    public Embed(File file) {
        try (final Json json = new Json(file)) {
            json.toObject(this, Embed.class);
        }
    }

    public Embed addField(Field... fields) {
        if (this.fields == null) this.fields = fields;
        else if (this.fields.length == 0) this.fields = fields;
        else {
            this.fields = ArrayMerge.merge(this.fields, fields);
        }
        return this;
    }

    @Override
    public boolean verify() {
        int maximum = 0;
        if (title != null) {
            maximum += title.length();
            if (title.length() > 256) return false;
        }
        if (description != null) {
            maximum += description.length();
            if (description.length() > 4096) return false;
        }
        if (fields != null) {
            if (fields.length > 25) return false;
            for (final Field field : fields) {
                maximum += field.name.length();
                maximum += field.value.length();
                if (field.name.length() > 256) return false;
                if (field.value.length() > 1024) return false;
            }
        }
        if (footer != null) {
            maximum += footer.text.length();
            if (footer.text.length() > 2048) return false;
        }
        if (author != null) {
            maximum += author.name.length();
            if (author.name.length() > 256) return false;
        }
        return maximum <= 6000;
    }

    public Embed title(String title) {
        this.title = title;
        return this;
    }

    public Embed type(String type) {
        this.type = type;
        return this;
    }

    public Embed description(String description) {
        this.description = description;
        return this;
    }

    public Embed url(String url) {
        this.url = url;
        return this;
    }

    public Embed timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Embed color(Integer color) {
        this.color = color;
        return this;
    }

    public Embed color(int r, int g, int b) {
        this.color = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        return this;
    }

    public Embed footer(String footer) {
        this.footer.text = footer;
        return this;
    }

    public Embed footer(String footer, String iconUrl, String proxyIconUrl) {
        this.footer.text = footer;
        this.footer.icon_url = iconUrl;
        this.footer.proxy_icon_url = proxyIconUrl;
        return this;
    }

    public Embed image(Image image) {
        this.image = image;
        return this;
    }

    public Embed image(String url) {
        this.image.url = url;
        return this;
    }

    public Embed thumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public Embed thumbnail(String url) {
        this.thumbnail.url = url;
        return this;
    }

    public Embed video(Video video) {
        this.video = video;
        return this;
    }

    public Embed video(String url) {
        this.video.url = url;
        return this;
    }

    public Embed provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public Embed provider(String name, String url) {
        this.provider.name = name;
        this.provider.url = url;
        return this;
    }

    public Embed author(Author author) {
        this.author = author;
        return this;
    }

    public Embed author(String name) {
        this.author.name = name;
        return this;
    }

    public Embed author(String name, String url, String iconUrl, String proxyIconUrl) {
        this.author.name = name;
        this.author.url = url;
        this.author.icon_url = iconUrl;
        this.author.proxy_icon_url = proxyIconUrl;
        return this;
    }

    public static class Footer extends Payload {

        public String text;
        public @Optional String icon_url, proxy_icon_url;

    }

    public static class Image extends Media {

        public String url;

    }

    public static class Thumbnail extends Image {
    }

    public static class Video extends Media {

        public @Optional String url;

    }

    public static class Provider extends Payload {

        public @Optional String name, url;

    }

    public static class Author extends Payload {

        public String name;
        public @Optional String url, icon_url, proxy_icon_url;

    }

    public static class Field extends Payload {

        public String name, value;
        public boolean inline;

        public Field() {
        }

        public Field(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

    }

    static class Media extends Payload {

        public @Optional String proxy_url;
        public @Optional Integer height, width;

    }

}
