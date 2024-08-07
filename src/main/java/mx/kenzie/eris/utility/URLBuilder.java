package mx.kenzie.eris.utility;

import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Grammar;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class URLBuilder implements Supplier<URL> {

    protected final String base;
    protected final Map<String, String> query;

    public URLBuilder(String base) {
        this.base = base;
        this.query = new LinkedHashMap<>();
    }

    public static URLBuilder from(URI existing) {
        final String string = existing.toString();
        final int queryStart = string.indexOf('?');
        if (queryStart == -1) return new URLBuilder(string);
        final String query = string.substring(queryStart + 1);
        final URLBuilder builder = new URLBuilder(string.substring(0, queryStart));
        int start = 0, next = query.indexOf('&', start);
        if (next == -1) next = query.length();
        do {
            final String part = query.substring(start, next);
            final int split = part.indexOf('=');
            assert split != -1;
            builder.query(part.substring(0, split), part.substring(split + 1));
            start = next + 1;
        } while ((next = query.indexOf('&', start)) != -1);
        return builder;
    }

    @Override
    public URL get() {
        try {
            return URI.create(this.toString()).toURL();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public URLBuilder query(String key, String value) {
        this.query.put(key, value);
        return this;
    }

    public URLBuilder query(Payload data) {
        final var grammar = new Grammar() {
            @Override
            public Map<String, Object> marshal(Object object) {
                return super.marshal(object);
            }
        };
        final Map<String, Object> map = grammar.marshal(data);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.query(entry.getKey(), Objects.toString(entry.getValue()));
        }
        return this;
    }

    public Map<String, String> getQuery() {
        return new LinkedHashMap<>(this.query);
    }

    public <Query extends Payload> Query getQuery(Query payload) {
        final var grammar = new Grammar() {
            @Override
            protected <Type, Container extends Map<?, ?>> Type unmarshal(Type object, Container container) {
                return super.unmarshal(object, container);
            }
        };
        return grammar.unmarshal(payload, query);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(base);
        if (!query.isEmpty()) {
            builder.append("?").append(this.getQueryString());
        }
        return builder.toString();
    }

    public String getQueryString() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : query.entrySet()) {
            builder.append(entry.getKey()).append("=").append(this.encode(entry.getValue()));
            builder.append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static String toQueryString(Payload payload) {
        final URLBuilder builder = new URLBuilder("/");
        return builder.query(payload).getQueryString();
    }

    public static String[] mergeHeaders(String[] a, String[] b) {
        if (a == null || a.length == 0) return b;
        if (b == null || b.length == 0) return a;
        final Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < a.length; i++) map.put(a[i], b[++i]);
        for (int i = 0; i < b.length; i++) map.putIfAbsent(b[i], b[++i]);
        final String[] result = new String[map.size() * 2];
        int index = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result[index++] = entry.getKey();
            result[index++] = entry.getValue();
        }
        return result;
    }

}
