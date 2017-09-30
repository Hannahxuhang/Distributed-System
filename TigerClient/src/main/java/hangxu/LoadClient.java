package hangxu;



import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// -thread 10 -iteration 100 -ip 18.221.235.123 -port 80
public class LoadClient {
    @Parameter(names = "-thread", description = "number of threads")
    private int thread = 5;

    @Parameter(names = "-iteration", description = "number of iteration")
    private int iteration = 100;

    @Parameter(names = "-ip", description = "the ip address of server")
    private String ipAddress = "18.221.235.123";

    @Parameter(names = "-port", description = "the port used on server")
    private String port = "80";

    HashMap<String, Long> executionTimeMap = new HashMap<String, Long>();

    private int failedCalls = 0;

    public static void main(String[] args) throws ParseException {
        LoadClient loadClient = new LoadClient();
        loadClient.createThreads(args);
    }

    public void createThreads(String[] args) {
        LoadClient loadClient = new LoadClient();

        JCommander.newBuilder()
                .addObject(loadClient)
                .build()
                .parse(args);

        Client client = ClientBuilder.newClient();
        WebTarget localhostTarget = client.target("http://" + ipAddress + ":" + port + "/Tiger-1.0-SNAPSHOT/rest/myserver");

        SimpleClient simpleClient = new SimpleClient(localhostTarget);
        ExecutorService executor = Executors.newFixedThreadPool(thread);

        Instant startTime = Instant.now();
        System.out.println("Client start at: " + startTime);

        for (int i = 0; i < thread; i++) {
            final int threadNum = i;
            executor.execute(() -> {
                for (int j = 0; j < iteration; j++) {
                    long runtimeGetMethodLatency = calculateMethodTime("GET", simpleClient);
                    String threadInfoKeyGet = "Thread Number : " + threadNum + " Thread Iteration :" + j + " HTTP GET";
                    executionTimeMap.put(threadInfoKeyGet, runtimeGetMethodLatency);

                    String threadInfoKeyPost = "Thread Number : " + threadNum + " Thread Iteration :" + j + " HTTP POST";
                    long runtimePostMethodLatency = calculateMethodTime("POST", simpleClient);
                    executionTimeMap.put(threadInfoKeyPost, runtimePostMethodLatency);
                }
            });
        }
        System.out.println("All threads start to run!");
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Instant endTime = Instant.now();
        System.out.println("All threads complete :" + endTime);
        long totalRuntime = endTime.toEpochMilli() - startTime.toEpochMilli();

        calculateLatency(totalRuntime);
        System.exit(0);
    }

    private void calculateLatency(long totalRuntimeMs) {
        int numberOfRequest = thread * iteration * 2;


        System.out.println("Total number of request :" +  numberOfRequest);
        System.out.println("Total number of Successful calls: " + (numberOfRequest - failedCalls));
        System.out.println("Total run time for all threads to complete :" + totalRuntimeMs + " ms");

        Collection<Long> latencyValues = executionTimeMap.values();
        System.out.println("Median latency: " + calculateMedian(latencyValues) + " ms");
        System.out.println("Mean latency: " + calculateMean(latencyValues) + " ms");
        System.out.println("95th percentile :" + calculatePercentile(latencyValues, 0.95) + " ms");
        System.out.println("99th percentile :" + calculatePercentile(latencyValues, 0.99) + " ms");
    }

    private long calculateMethodTime(String methodName, SimpleClient simpleClient) {
        Response response;

        Instant startTime = Instant.now();
        if (methodName.equals("GET")) {
            response = simpleClient.getStatus();
        } else {
            response = simpleClient.postText("hello world");
        }
        Instant endTime = Instant.now();

        if (response.getStatus() != 200) {
            synchronized (this) {
                failedCalls++;
            }
        }
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    public static synchronized long calculateMedian(Collection<Long> values) {
        int length = values.size();
        Long[] array = values.toArray(new Long[length]);

        if (array != null) {
            Arrays.sort(array);
        }

        if (length % 2 == 0) {
            return (array[length / 2] + array[length / 2 - 1]) / 2;
        } else {
            return array[length / 2];
        }
    }

    public static synchronized long calculateMean(Collection<Long> values) {
        int length = values.size();
        Long[] array = values.toArray(new Long[length]);

        double sum = 0;
        for (Long d : array)
            sum += d;
        return Math.round(sum/length);

    }

    public static synchronized long calculatePercentile(Collection<Long> values, double percentile) {
        int length = values.size();
        Long[] array = values.toArray(new Long[length]);
        if (array != null) {
            Arrays.sort(array);
        }

        double n = (length - 1) * percentile + 1;
        if (n == 1d) {
            return array[0];
        } else if (n == length) {
            return array[length - 1];
        } else {
            int k = (int)n;
            double d = n - k;
            double result = array[k - 1] + d * (array[k] - array[k - 1]);
            return Math.round(result);
        }
    }
}
