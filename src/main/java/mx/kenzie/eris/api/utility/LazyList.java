package mx.kenzie.eris.api.utility;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class LazyList<Type> extends Lazy implements List<Type> {

    protected final Class<Type> type;
    protected List<Type> list;

    public LazyList(Class<Type> type, List<Type> list) {
        this.list = list;
        this.type = type;
    }

    public static <Type> LazyList<Type> of(Class<Type> type) {
        return new LazyList<>(type, new ArrayList<>());
    }

    public void update(Json.JsonHelper helper, List<?> objects, DiscordAPI api) {
        this.unready();
        try {
            final List<Type> list = new ArrayList<>();
            for (final Object object : objects) {
                final Type type = helper.createObject(this.type);
                list.add(type);
                if (type instanceof Entity entity) entity.api = api;
                helper.mapToObject(type, this.type, (Map<?, ?>) object);
                if (type instanceof Lazy lazy) lazy.finish();
            }
            synchronized (this) {
                this.list = list;
            }
        } catch (Throwable ex) {
            this.error(ex);
        } finally {
            this.finish();
        }
    }

    public synchronized void setBacker(List<Type> list) {
        this.list = list;
    }

    @Override
    public int size() {
        this.await();
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        this.await();
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        this.await();
        return list.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Type> iterator() {
        this.await();
        return list.iterator();
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Type[] toArray() {
        this.await();
        return list.toArray((Type[]) Array.newInstance(type, 0));
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        this.await();
        return list.toArray(a);
    }

    @Override
    public boolean add(Type type) {
        this.await();
        return list.add(type);
    }

    @Override
    public boolean remove(Object o) {
        this.await();
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.await();
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Type> c) {
        this.await();
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Type> c) {
        this.await();
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.await();
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Type get(int index) {
        return null;
    }

    @Override
    public Type set(int index, Type element) {
        return null;
    }

    @Override
    public void add(int index, Type element) {

    }

    @Override
    public Type remove(int index) {
        this.await();
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        this.await();
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        this.await();
        return list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Type> listIterator() {
        this.await();
        return list.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Type> listIterator(int index) {
        this.await();
        return list.listIterator(index);
    }

    @NotNull
    @Override
    public List<Type> subList(int fromIndex, int toIndex) {
        this.await();
        return list.subList(fromIndex, toIndex);
    }

    public void forEachAsync(Consumer<? super Type> action, Bot bot) {
        for (final Type thing : this) CompletableFuture.runAsync(() -> action.accept(thing), bot.executor);
    }

    @Override
    public String toString() {
        final Iterator<Type> iterator = iterator();
        if (!iterator.hasNext()) return "[]";
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (; ; ) {
            final Type thing = iterator.next();
            builder.append(thing == this ? "(this Collection)" : thing);
            if (!iterator.hasNext()) return builder.append(']').toString();
            builder.append(',').append(' ');
        }
    }
}
