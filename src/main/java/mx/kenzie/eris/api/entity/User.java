package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.Bot;
import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.error.DiscordException;

import java.net.URI;

public class User extends Snowflake {
    
    public volatile boolean verified, mfa_enabled, bot, system;
    public volatile String username, email, discriminator, avatar, banner, locale;
    public volatile int flags, accent_color, premium_type, public_flags;
    private transient int discriminator0;
    
    public int discriminator() {
        if (discriminator0 < 1) discriminator0 = Integer.parseInt(discriminator);
        return discriminator0;
    }
    
    public String getTag() {
        this.await();
        return username + "#" + discriminator;
    }
    
    public boolean hasFlag(int flag) {
        this.await();
        return (flags & flag) != 0;
    }
    
    public boolean hasPublicFlag(int flag) {
        this.await();
        return (flags & flag) != 0;
    }
    
    public String getBannerURL() {
        this.await();
        return Bot.CDN_URL + "/banners/" + id + "/" + banner + ".png";
    }
    
    public String getAvatarURL() {
        this.await();
        if (avatar == null) return this.getDefaultAvatarURL();
        return Bot.CDN_URL + "/avatars/" + id + "/" + avatar + ".png";
    }
    
    public String getDefaultAvatarURL() {
        this.await();
        return Bot.CDN_URL + "/embed/avatars/" + discriminator + ".png";
    }
    
    public String getGuildAvatarURL(Guild guild) {
        this.await();
        return Bot.CDN_URL + "guilds/" + guild.id + "/users/" + id + "/avatars/" + null + ".png"; // todo
    }
    
}
