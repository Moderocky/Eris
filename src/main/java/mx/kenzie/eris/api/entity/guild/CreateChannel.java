package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.api.entity.User;
import mx.kenzie.eris.api.magic.ChannelType;
import mx.kenzie.eris.data.Payload;
import org.intellij.lang.annotations.MagicConstant;

public abstract class CreateChannel extends Snowflake {
    public @MagicConstant(flagsFromClass = ChannelType.class) int type;
    public String name;
    public @Optional Integer position, bitrate, user_limit, rate_limit_per_user, video_quality_mode, default_auto_archive_duration, default_thread_rate_limit_per_user;
    public boolean nsfw;
    public @Optional String topic, last_message_id, icon, parent_id, last_pin_timestamp, rtc_region;
    public @Optional Payload[] permission_overwrites;
    public @Optional User[] recipients;
}
