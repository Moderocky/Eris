package mx.kenzie.eris.network;

import mx.kenzie.eris.Bot;
import mx.kenzie.eris.api.event.SocketClose;
import mx.kenzie.eris.data.incoming.Incoming;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class SocketListener implements WebSocket.Listener {
    
    protected final NetworkController network;
    private volatile StringBuilder builder = new StringBuilder();
    
    public SocketListener(NetworkController network) {
        this.network = network;
    }
    
    @Override
    public void onOpen(WebSocket socket) {
        if (Bot.DEBUG_MODE) System.out.println("Opening");
        this.network.socket = socket;
        WebSocket.Listener.super.onOpen(socket);
    }
    
    @Override
    public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
        if (builder == null) builder = new StringBuilder();
        this.builder.append(data);
        if (last) try {
            final Incoming payload = this.network.getPayload(builder.toString());
            if (Bot.DEBUG_MODE) System.out.println("Incoming: " + payload.op + "/" + payload.key);
            this.builder = null;
            payload.network = network;
            this.network.triggerEvent(payload);
        } catch (Throwable ex) {
            Bot.handle(ex);
        }
        return WebSocket.Listener.super.onText(socket, data, last);
    }
    
    @Override
    public CompletionStage<?> onClose(WebSocket socket, int statusCode, String reason) {
        if (Bot.DEBUG_MODE) System.out.println("Closing: " + statusCode + "/" + reason);
        final SocketClose close = new SocketClose(statusCode, reason);
        if (network.socket == socket) this.network.triggerEvent(close); // don't dispatch events if socket was replaced
        return WebSocket.Listener.super.onClose(socket, statusCode, reason);
    }
}
