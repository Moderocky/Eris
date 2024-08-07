package mx.kenzie.eris.api.entity.connection;

import mx.kenzie.eris.api.Lazy;
import mx.kenzie.eris.api.entity.Application;
import mx.kenzie.eris.api.entity.User;

public class Access extends Lazy {

    public String expires;
    public String[] scopes;
    public Application application;
    public User user;

    @Override
    public void finish() {
        this.user.api = this.application.api = this.api;
        super.finish();
    }

    public User user() {
        if (user == null) return null;
        if (user.api == null) user.api = this.api;
        return user;
    }

    public long id() {
        return user.id();
    }

    public boolean valid() {
        return user != null;
    }

}
