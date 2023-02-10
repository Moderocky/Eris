package mx.kenzie.eris.api.magic;

public interface Ansi {
    Ansi RESET = new Internal("0");
    Ansi
        DEFAULT = new Internal("39"),
        BLACK = new Internal("30"),
        RED = new Internal("31"),
        GREEN = new Internal("32"),
        YELLOW = new Internal("33"),
        BLUE = new Internal("34"),
        MAGENTA = new Internal("35"),
        CYAN = new Internal("36"),
        WHITE = new Internal("37");
    Ansi
        BG_DEFAULT = new Internal("49"),
        BG_BLACK = new Internal("40"),
        BG_RED = new Internal("41"),
        BG_GREY = new Internal("42"),
        BG_PALE = new Internal("44"),
        BG_LILAC = new Internal("45"),
        BG_WHITE = new Internal("47");

    Ansi add(Ansi ansi);

    Ansi bold();

    Ansi italic();

    Ansi underline();

    Ansi strikethrough();

}

record Internal(String code) implements Ansi {
    //    static final char CODE = ''; // 
    static final String CODE = "\\u001b";

    @Override
    public String toString() {
        return CODE + '[' + code + 'm';
    }

    @Override
    public Ansi add(Ansi ansi) {
        return new Internal(this.code + ';' + ((Internal) ansi).code);
    }

    public Ansi bold() {
        return new Internal(this.code + ';' + 1);
    }

    @Override
    public Ansi italic() {
        return new Internal(this.code + ';' + 2);
    }

    @Override
    public Ansi underline() {
        return new Internal(this.code + ';' + 3);
    }

    @Override
    public Ansi strikethrough() {
        return new Internal(this.code + ';' + 9);
    }

}
