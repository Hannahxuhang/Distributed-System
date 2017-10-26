package hangxu.skiresortclient;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SimpleClient {
    private final WebTarget webTarget;
    
    public SimpleClient(WebTarget webTarget) {
        this.webTarget = webTarget;
    }
    
    public Response postRecord(Object requestEntity) throws ClientErrorException {
        return webTarget.request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(requestEntity, "application/json"), Response.class);
    }
    
    public Response getMyVert() throws ClientErrorException {
        return webTarget.request().get();
    }
}
