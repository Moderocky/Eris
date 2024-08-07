package mx.kenzie.eris.api.entity.connection;

import mx.kenzie.eris.api.Lazy;

public class Metadata extends Lazy {

    public String key, name, description;
    public int type;

    public Metadata(String key, String name, String description, int type) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Metadata() {
    }

    public interface Type {

        int INTEGER_LESS_THAN_OR_EQUAL = 1; // the metadata value (integer) is less than or equal to the guild's
        // configured value (integer)
        int INTEGER_GREATER_THAN_OR_EQUAL = 2; // the metadata value (integer) is greater than or equal to the
        // guild's configured value (integer)
        int INTEGER_EQUAL = 3; // the metadata value (integer) is equal to the guild's configured value (integer)
        int INTEGER_NOT_EQUAL = 4; // the metadata value (integer) is not equal to the guild's configured value
        // (integer)
        int DATETIME_LESS_THAN_OR_EQUAL = 5; // the metadata value (ISO8601 string) is less than or equal to the
        // guild's configured value (integer; days before current date)
        int DATETIME_GREATER_THAN_OR_EQUAL = 6; // the metadata value (ISO8601 string) is greater than or equal to
        // the guild's configured value (integer; days before current date)
        int BOOLEAN_EQUAL = 7; // the metadata value (integer) is equal to the guild's configured value (integer; 1)
        int BOOLEAN_NOT_EQUAL = 8; // the metadata value (integer) is not equal to the guild's configured value
        // (integer; 1)

    }

}
