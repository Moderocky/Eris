package mx.kenzie.eris.api.event.guild;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Emoji;
import mx.kenzie.eris.api.entity.Sticker;
import mx.kenzie.eris.data.Payload;

public class UpdateGuildStickers extends Payload implements Event {
    public String guild_id;
    public Sticker[] stickers;
}
