package mx.kenzie.eris.http;

import mx.kenzie.eris.data.incoming.Incoming;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class SocketListener implements WebSocket.Listener { // TODO
    
    protected final NetworkController network;
    
    public SocketListener(NetworkController network) {
        this.network = network;
    }
    
    @Override
    public void onOpen(WebSocket socket) {
        this.network.socket = socket;
        System.out.println("Opened socket."); // todo
        WebSocket.Listener.super.onOpen(socket);
    }
    
    private volatile StringBuilder builder = new StringBuilder();
    
    @Override
    public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
        if (builder == null) builder = new StringBuilder();
        this.builder.append(data);
        if (last) try {
            final Incoming payload = this.network.getPayload(builder.toString());
            this.builder = null;
            System.out.println("Received " + payload.getClass().getSimpleName()); // todo
            payload.network = network;
            this.network.triggerEvent(payload);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return WebSocket.Listener.super.onText(socket, data, last);
    }
    
    @Override
    public CompletionStage<?> onClose(WebSocket socket, int statusCode, String reason) {
        System.out.println("Closed socket: " + reason); // todo
        return WebSocket.Listener.super.onClose(socket, statusCode, reason);
    }
}
