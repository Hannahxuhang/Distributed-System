package metrics;

import java.util.ArrayList;
import java.util.Collections;

public class MetricsCalculator {
    private int successfulRequestCount;
    private int sentRequestCount;
    private long latencySum = 0;
    boolean isSorted = false;
    private ArrayList<Long> latencies = new ArrayList<>();
    
    public long getLatencySum() {
        return latencySum;
    }
    
    public int getSuccessfulRequestCount() {
        return successfulRequestCount;
    }
    
    public int getSentRequestCount() {
        return sentRequestCount;
    }
    
    synchronized public void addLatency(long latency) {
        latencies.add(latency);
        latencySum += latency;
    }
    
    synchronized public void addSentRequest() {
        sentRequestCount++;
    }
    
    synchronized public void addSuccessfulRequest(boolean isSuccessful) {
        if (isSuccessful) {
            successfulRequestCount++;
        }
    }
    
    public long getMeanLatency() {
        if (successfulRequestCount != 0) {
            return latencySum / successfulRequestCount;
        }
        return 0;
    }
    
    public long getMedianLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }
        return latencies.get(latencies.size() / 2);
    }
    
    public long get95thLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }
        return latencies.get((int)Math.floor(latencies.size() * 0.95));
    }
    
    public long get99thLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }
        return latencies.get((int)Math.floor(latencies.size() * 0.99));
    }
}
