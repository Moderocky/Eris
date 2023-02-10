package mx.kenzie.eris.api.event.message;

import mx.kenzie.eris.api.Event;
import mx.kenzie.eris.api.entity.Member;
import mx.kenzie.eris.api.entity.Message;
import mx.kenzie.eris.api.entity.User;

public class UpdateMessage extends Message implements Event {

    public String guild_id;
    public Member member;
    public User[] mentions;

}
