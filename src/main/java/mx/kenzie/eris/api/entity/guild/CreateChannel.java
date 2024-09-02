package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.eris.api.entity.Role;
import mx.kenzie.eris.api.magic.PermissionOverwriteType;
import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.data.Payload;
import org.intellij.lang.annotations.MagicConstant;

public abstract class CreateChannel extends Snowflake {
    public @MagicConstant(flagsFromClass = ChannelType.class) int type;
    public @Optional String name;
    public @Optional Integer position, bitrate, user_limit, rate_limit_per_user, video_quality_mode, default_auto_archive_duration, default_thread_rate_limit_per_user;
    public @Optional boolean nsfw;
    public @Optional String topic, last_message_id, icon, parent_id, last_pin_timestamp, rtc_region;
    public @Optional PermissionOverwrite[] permission_overwrites;
    public @Optional User[] recipients;

    public static class PermissionOverwrite extends Payload {
        public String id;
        public int type;
        public String allow;
        public String deny;

        public static PermissionOverwrite ofMember(long id, long allow, long deny) {
            return new PermissionOverwrite(id, PermissionOverwriteType.MEMBER, allow, deny);
        }

        public static PermissionOverwrite ofMember(String id, long allow, long deny) {
            return new PermissionOverwrite(id, PermissionOverwriteType.MEMBER, allow, deny);
        }

        public static PermissionOverwrite ofRole(long id, long allow, long deny) {
            return new PermissionOverwrite(id, PermissionOverwriteType.ROLE, allow, deny);
        }

        public static PermissionOverwrite ofRole(String id, long allow, long deny) {
            return new PermissionOverwrite(id, PermissionOverwriteType.ROLE, allow, deny);
        }

        public PermissionOverwrite() {
        }

        public PermissionOverwrite(User user, long allow, long deny) {
            this.id = user.id;
            this.type = PermissionOverwriteType.MEMBER;
            this.allow = Long.toString(allow);
            this.deny = Long.toString(deny);
        }

        public PermissionOverwrite(Role role, long allow, long deny) {
            this.id = role.id;
            this.type = PermissionOverwriteType.ROLE;
            this.allow = Long.toString(allow);
            this.deny = Long.toString(deny);
        }

        public PermissionOverwrite(String id, int type, long allow, long deny) {
            this.id = id;
            this.type = type;
            this.allow = Long.toString(allow);
            this.deny = Long.toString(deny);
        }

        public PermissionOverwrite(long id, int type, long allow, long deny) {
            this.id = Long.toString(id);
            this.type = type;
            this.allow = Long.toString(allow);
            this.deny = Long.toString(deny);
        }
    }
}
