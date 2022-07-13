package mx.kenzie.eris.data.incoming.http;

import mx.kenzie.eris.data.Payload;

public class GatewayConnection extends Payload {
    
    public String url;
    public int shards;
    public SessionDetails session_start_limit;
    public static class SessionDetails {
        public int total, remaining, reset_after, max_concurrency;
    }
    
}
