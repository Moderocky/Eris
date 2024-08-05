package mx.kenzie.eris.utility;

public class ArrayMerge {

    public static <Type> Type[] merge(Type[] a, Type[] b) {
        if (a.length == 0) return b;
        if (b.length == 0) return a;
        final Type[] result = java.util.Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
