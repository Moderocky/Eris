package mx.kenzie.eris.api.magic;

import static mx.kenzie.eris.api.magic.Internal.CODE;

public interface Ansi {
    Ansi RESET = new Internal("0");
    Ansi
        DEFAULT = new Internal("39"),
        BLACK = new Internal("31"),
        RED = new Internal("32"),
        GREEN = new Internal("33"),
        BLUE = new Internal("34"),
        MAGENTA = new Internal("35"),
        CYAN = new Internal("36"),
        WHITE = new Internal("37");
    Ansi
        BG_DEFAULT = new Internal("39"),
        BG_BLACK = new Internal("41"),
        BG_RED = new Internal("42"),
        BG_GREEN = new Internal("43"),
        BG_BLUE = new Internal("44"),
        BG_MAGENTA = new Internal("45"),
        BG_CYAN = new Internal("46"),
        BG_WHITE = new Internal("47");
    
    Ansi add(Ansi ansi);
    
    Ansi bold();
    
    Ansi italic();
    
    Ansi underline();
    
    Ansi strikethrough();
    
}

record Internal(String code) implements Ansi {
    static final char CODE = '';
    
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
