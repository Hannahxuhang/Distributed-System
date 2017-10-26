package hangxu.skiresortclient;

import data.MyVert;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import metrics.MetricsCalculator;

public class GetMyVertClient implements Callable<MyVert> {
    
    String protocol;
    String host;
    int port;
    int skierId;
    int dayNum;
    Client client;
    MetricsCalculator calculator;
    
    public GetMyVertClient(String protocol, String host, int port, int skierId, int dayNum, Client client, MetricsCalculator calculator) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.skierId = skierId;
        this.dayNum = dayNum;
        this.client = client;
        this.calculator = calculator;
    }
    
    public MyVert call() {
        URL url = null;
        String address = "/SkiResortServer-1.0-SNAPSHOT/webresources/myserver/myvert/" + skierId + "&" + dayNum;
        
        try {
            url = new URL(protocol, host, port, address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WebTarget webTarget = client.target(url.toString());
        Response response;
        MyVert res = null;
        long startTime = System.currentTimeMillis();
        try {
            response = webTarget.request().get();;
            res = response.readEntity(MyVert.class);
            response.close();
            calculator.addSentRequest();
            calculator.addSuccessfulRequest(response.getStatus() == 200);
        } catch (ProcessingException e) {
            e.printStackTrace();
        }
        long laytency = System.currentTimeMillis() - startTime;
        calculator.addLatency(laytency);
        return res;
    }
}
