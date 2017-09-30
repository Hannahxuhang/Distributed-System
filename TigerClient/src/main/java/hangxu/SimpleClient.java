package hangxu;



import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;

public class SimpleClient {

    private WebTarget webTarget;

    public SimpleClient(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    public Response postText(Object requestEntity) throws ClientErrorException {
        return webTarget.request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(requestEntity,
                        javax.ws.rs.core.MediaType.TEXT_PLAIN),
                        Response.class);
    }

    public Response getStatus() throws ClientErrorException {
        WebTarget resource = webTarget;

        return resource.request(MediaType.TEXT_PLAIN)
                .get(Response.class);
    }
}
