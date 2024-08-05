package mx.kenzie.eris.api.entity;

public interface Replied {

    Message reply(Message message);

    Message reply(String message);

}
