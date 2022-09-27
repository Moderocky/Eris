package mx.kenzie.eris.api.entity.voice;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.data.Payload;

public class VoiceState extends Payload {
    public @Optional String guild_id;
    public String channel_id, user_id, session_id;
    public @Optional Member member;
    public boolean deaf, mute, self_deaf, self_mute, self_stream, self_video, suppress;
    public String request_to_speak_timestamp;
}
