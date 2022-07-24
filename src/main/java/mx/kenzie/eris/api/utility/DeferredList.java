package mx.kenzie.eris.api.utility;

import mx.kenzie.argo.Json;
import mx.kenzie.eris.DiscordAPI;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * This is not actually a list.
 * It looks like a list, but it is not.
 * This takes the added entries and immediately gives them to something else.
 * <p>
 * This is designed for incoming JSON streams of ~1000 objects, which should not be held in memory at once.
 * Once an object is assembled it is immediately given to the consumer.
 * <p>
 * Unless the consumer keeps a reference, the data can be discarded at once.
 */
public class DeferredList<Type> implements List<Object> {
    private final Json.JsonHelper helper = new Json.JsonHelper();
    private final Class<Type> type;
    private final Consumer<? super Type> consumer;
    private final DiscordAPI api;
    
    public DeferredList(Class<Type> type, Consumer<? super Type> consumer, DiscordAPI api) {
        this.type = type;
        this.consumer = consumer;
        this.api = api;
    }
    
    @Override
    public boolean add(Object object) {
        if (object instanceof Map<?, ?> map) {
            final Type thing = helper.createObject(type);
            if (thing instanceof Entity entity) entity.api = api;
            this.helper.mapToObject(thing, type, map);
            if (thing instanceof Lazy lazy) lazy.finish();
            this.consumer.accept(thing);
        }
        return true;
    }
    
    //<editor-fold desc="Fake Methods" defaultstate="collapsed">
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public boolean contains(Object o) {
        return false;
    }
    
    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return null;
    }
    
    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }
    
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return null;
    }
    
    @Override
    public boolean remove(Object o) {
        return false;
    }
    
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }
    
    @Override
    public boolean addAll(@NotNull Collection<?> c) {
        return false;
    }
    
    @Override
    public boolean addAll(int index, @NotNull Collection<?> c) {
        return false;
    }
    
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }
    
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }
    
    @Override
    public void clear() {
    
    }
    
    @Override
    public Object get(int index) {
        return null;
    }
    
    @Override
    public Object set(int index, Object element) {
        return null;
    }
    
    @Override
    public void add(int index, Object element) {
    
    }
    
    @Override
    public Object remove(int index) {
        return null;
    }
    
    @Override
    public int indexOf(Object o) {
        return 0;
    }
    
    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }
    
    @NotNull
    @Override
    public ListIterator<Object> listIterator() {
        return null;
    }
    
    @NotNull
    @Override
    public ListIterator<Object> listIterator(int index) {
        return null;
    }
    
    @NotNull
    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return null;
    }
    //</editor-fold>
}
