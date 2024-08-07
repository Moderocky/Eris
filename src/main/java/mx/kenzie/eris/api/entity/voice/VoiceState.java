package mx.kenzie.eris.api.entity.voice;

import mx.kenzie.grammar.Optional;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.data.Payload;

public class VoiceState extends Payload {
    public @Optional String guild_id;
    public String channel_id, user_id, session_id;
    public @Optional Member member;
    public boolean deaf, mute, self_deaf, self_mute, self_video, suppress;
    public @Optional boolean self_stream;
    public String request_to_speak_timestamp;
}
