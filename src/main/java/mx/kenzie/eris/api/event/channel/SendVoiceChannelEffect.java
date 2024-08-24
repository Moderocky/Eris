package mx.kenzie.eris.api.event.channel;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Emoji;
import mx.kenzie.eris.data.Payload;
import mx.kenzie.grammar.Optional;

public class SendVoiceChannelEffect extends Payload implements Event {

    public String channel_id; // ID of the channel the effect was sent in
    public String guild_id; // ID of the guild the effect was sent in
    public String user_id; // ID of the user who sent the effect
    public @Optional Emoji emoji; // The emoji sent, for emoji reaction and soundboard effects
    public int animation_type; // The type of emoji animation, for emoji reaction and soundboard effects
    public int animation_id; // The ID of the emoji animation, for emoji reaction and soundboard effects
    public String sound_id; // The ID of the soundboard sound, for soundboard effects
    public double sound_volume; // The volume of the soundboard sound, from 0 to 1, for soundboard effects

}
