package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.Bot;
import mx.kenzie.eris.DiscordAPI;

public class User extends Snowflake {
    
    public volatile boolean verified, mfa_enabled, bot, system;
    public volatile String username, email, discriminator, avatar, banner, locale;
    public volatile int flags, accent_color, premium_type, public_flags;
    private transient int discriminator0;
    
    public int discriminator() {
        this.await();
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
    
    public Member getMember(Guild guild) {
        return this.getMember(guild.id);
    }
    
    public Member getMember(String guild) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.getMember(guild, this);
    }
    
    public Member getMember(long guild) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        return api.getMember(guild, this);
    }
    
    public Message sendMessage(Message message) {
        if (api == null) throw DiscordAPI.unlinkedEntity(this);
        message.unready();
        this.api.createDirectChannel(id).<Channel>whenReady().thenAccept(channel -> this.api.sendMessage(channel, message));
        return message;
    }
    
}
