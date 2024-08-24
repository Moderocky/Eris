package mx.kenzie.eris.api.entity;

import mx.kenzie.grammar.Optional;
import org.intellij.lang.annotations.MagicConstant;

public class Entitlement extends Snowflake {

    public String sku_id, application_id;
    public @Optional String user_id, guild_id;
    public @MagicConstant(valuesFromClass = Type.class) int type;
    public boolean deleted, consumed;
    public @Optional String starts_at, ends_at;

    public static interface Type {

        int PURCHASE = 1; // Entitlement was purchased by user
        int PREMIUM_SUBSCRIPTION = 2; // Entitlement for Discord Nitro subscription
        int DEVELOPER_GIFT = 3; // Entitlement was gifted by developer
        int TEST_MODE_PURCHASE = 4; // Entitlement was purchased by a dev in application test mode
        int FREE_PURCHASE = 5; // Entitlement was granted when the SKU was free
        int USER_GIFT = 6; // Entitlement was gifted by another user
        int PREMIUM_PURCHASE = 7; // Entitlement was claimed by user for free as a Nitro Subscriber
        int APPLICATION_SUBSCRIPTION = 8; // Entitlement was purchased as an app subscription

    }

}
