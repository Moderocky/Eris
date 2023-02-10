package mx.kenzie.eris.api.magic;

public interface ApplicationFlags {
    int
        GATEWAY_PRESENCE = 1 << 12, // Intent required for bots in 100 or more servers to receive presence_update events
        GATEWAY_PRESENCE_LIMITED = 1 << 13, // Intent required for bots in under 100 servers to receive presence_update events, found in Bot Settings
        GATEWAY_GUILD_MEMBERS = 1 << 14, // Intent required for bots in 100 or more servers to receive member-related events like guild_member_add. See list of member-related events under GUILD_MEMBERS
        GATEWAY_GUILD_MEMBERS_LIMITED = 1 << 15, // Intent required for bots in under 100 servers to receive member-related events like guild_member_add, found in Bot Settings. See list of member-related events under GUILD_MEMBERS
        VERIFICATION_PENDING_GUILD_LIMIT = 1 << 16, // Indicates unusual growth of an app that prevents verification
        EMBEDDED = 1 << 17, // Indicates if an app is embedded within the Discord client (currently unavailable publicly)
        GATEWAY_MESSAGE_CONTENT = 1 << 18, // Intent required for bots in 100 or more servers to receive message content
        GATEWAY_MESSAGE_CONTENT_LIMITED = 1 << 19; // Intent required for bots in under 100 servers to receive message content, found in Bot Settings

    default boolean hasFlag(int flag) {
        final long flags = this.flags();
        return (flags & flag) != 0;
    }

    int flags();
}
