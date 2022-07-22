package mx.kenzie.eris.api.entity;

import mx.kenzie.eris.data.Payload;

public class Team extends Snowflake {
    public String icon, name, owner_user_id;
    public Member[] members;
    
    public static class Member extends Payload {
        public int membership_state; // 1 = INVITED, 2 = ACCEPTED
        public String permissions, team_id;
        public User user;
    }
}
