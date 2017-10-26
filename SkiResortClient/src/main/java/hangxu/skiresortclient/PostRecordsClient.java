package hangxu.skiresortclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.RFIDLiftData;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import metrics.MetricsCalculator;

public class PostRecordsClient implements Runnable {
    
    RFIDLiftData data;
    SimpleClient simpleClient;
    MetricsCalculator calculator;
    
    public PostRecordsClient(RFIDLiftData data, SimpleClient simpleClient, MetricsCalculator calculator) {
        this.data = data;
        this.simpleClient = simpleClient;
        this.calculator = calculator;
    }
    
    @Override
    public void run() {
        Response response;
        
        long startTime = System.currentTimeMillis();
        // change data to Json
        ObjectMapper mapper = new ObjectMapper();
    
        try {
            String dataInJson = mapper.writeValueAsString(data);
            response = simpleClient.postRecord(dataInJson);
            calculator.addSentRequest();
            calculator.addSuccessfulRequest(response.getStatus() == 200);
            // TODO: delete it after testing
            System.out.println(">> Record is: " + dataInJson);
            // TODO: delete it after testing
            System.out.println(">> Response Status is: " + response.getStatus());
            // close response to release net connection
            response.close();
        } catch (ClientErrorException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        long latency = System.currentTimeMillis() - startTime;
        calculator.addLatency(latency);
    }
}
