package hangxu.skiresortclient;

import data.MyVert;
import data.RFIDLiftData;
import hangxu.skiresortclient.PostRecordsClient;
import hangxu.skiresortclient.SimpleClient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import metrics.MetricsCalculator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;



public class MainClient {
    
    private final static int THREAD_NUM = 8;
    private final static String PROTOCOL = "http";
    private final static String HOST = "18.221.235.123";
    //private final static String HOST = "localhost";
    private final static int PORT = 80;
    //private final static int PORT = 8081;
    private final static String MID_PATH1 = "/SkiResortServer-1.0-SNAPSHOT/webresources/myserver/load";
    //private final static String MID_PATH = "/SkiResortServer/webresources/myserver/load";
    private final static String FILE_PATH = "/Users/hannah/NetBeansProjects/SkiResortClient/file/BSDSAssignment2Day1.csv";

    
    public static void main(String[] args) {
        MainClient mainClient = new MainClient();
        ArrayList<RFIDLiftData> records = readFile(FILE_PATH);
        // TODO: delete if after testing
        System.out.println(">> The Size of Records: " + records.size());
        postRecords(THREAD_NUM, records);
    }
    
    public static ArrayList<RFIDLiftData> readFile(String filePath) {
        ArrayList<RFIDLiftData> records = new ArrayList<>();
        
        try {
            // TODO: delete it after testing
            System.out.println("========reading file data======");
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            // ignore the first line
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                String[] recordFields = line.split(",");
                RFIDLiftData record = new RFIDLiftData(
                        Integer.parseInt(recordFields[0]),
                        Integer.parseInt(recordFields[1]),
                        Integer.parseInt(recordFields[2]),
                        Integer.parseInt(recordFields[3]),
                        Integer.parseInt(recordFields[4])
                );
                records.add(record);
                line = reader.readLine();
            }
            reader.close();
            
            // TODO: delete it after testing
            System.out.println("======reading file data completed======");
            
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    // use multi-threading to post records to server
    public static void postRecords(int threadNum, ArrayList<RFIDLiftData> records) {
        System.out.println("======Start Post Records======");
        long startTime = System.currentTimeMillis();
        
        LinkedBlockingQueue<RFIDLiftData> recordsQueue = new LinkedBlockingQueue<>(records);
        MetricsCalculator calculator = new MetricsCalculator();
        URL url = null;
        
        try {
            url = new URL(PROTOCOL, HOST, PORT, MID_PATH1);
            // TODO: delete it after testing
            System.out.println("url is: " + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        // reigster JacksonJsonProvider to client config
        ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);  
        // create new client
        Client client = ClientBuilder.newClient(config);      
        WebTarget webTarget = client.target(url.toString());
        SimpleClient simpleClient = new SimpleClient(webTarget);
        ArrayList<PostRecordsClient> listOfPostClients = new ArrayList<>();
        
        while (!recordsQueue.isEmpty()) {
            RFIDLiftData record = recordsQueue.poll();
            PostRecordsClient postClient = new PostRecordsClient(record, simpleClient, calculator);
            listOfPostClients.add(postClient);
        }
        
        // TODO: delete it after testing
        int count = 1;
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadNum);
        for (PostRecordsClient postClient : listOfPostClients) {
            executor.execute(postClient);
            // TODO: delete it after testing
            System.out.println(">> execute a postClient! " + count);
            count++;
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // close the client
        client.close();
        
        System.out.println("======Post Records Completed======");
        
        // print the statistics
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("======Print Statistics======");
        System.out.println(">> Thread Number: " + threadNum);
        System.out.println(">> Total Time: " + totalTime);
        System.out.println(">> Sent Request Number: " + calculator.getSentRequestCount());
        System.out.println(">> Successful Request Number: " + calculator.getSuccessfulRequestCount());
        System.out.println(">> Total Latency: " + calculator.getLatencySum());
        System.out.println(">> Median of Latency: " + calculator.getMeanLatency());
        System.out.println(">> Mean of Latency: " + calculator.getMeanLatency());
        System.out.println(">> 95th Percentile of Latency: " + calculator.get95thLatency());
        System.out.println(">> 99th Percentile of Latency: " + calculator.get99thLatency());
    }
    
    public void getMyVert(int dayNum) {
        System.out.println("======Starting GET Requests======");
        
        long startTime = System.currentTimeMillis();
        
        // build new client
        Client client = ClientBuilder.newClient();
        
        ArrayList<GetMyVertClient> myVertClients = new ArrayList<>();
        MetricsCalculator calculator = new MetricsCalculator();
        
        for (int skierId = 0; skierId < 40000; skierId++) {
            myVertClients.add(new GetMyVertClient(PROTOCOL, HOST, PORT, skierId, dayNum, client, calculator));           
        }
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try {
            List<Future<MyVert>> futures = pool.invokeAll(myVertClients);
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.close();
        
        System.out.println("======GET Ends======");
        
        // print the statistics
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("======Print Statistics======");
        System.out.println(">> Thread Number: " + 100);
        System.out.println(">> Total Time: " + totalTime);
        System.out.println(">> Sent Request Number: " + calculator.getSentRequestCount());
        System.out.println(">> Successful Request Number: " + calculator.getSuccessfulRequestCount());
        System.out.println(">> Total Latency: " + calculator.getLatencySum());
        System.out.println(">> Median of Latency: " + calculator.getMeanLatency());
        System.out.println(">> Mean of Latency: " + calculator.getMeanLatency());
        System.out.println(">> 95th Percentile of Latency: " + calculator.get95thLatency());
        System.out.println(">> 99th Percentile of Latency: " + calculator.get99thLatency());
    }
}
