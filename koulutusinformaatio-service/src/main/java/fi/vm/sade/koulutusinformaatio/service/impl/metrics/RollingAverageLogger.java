package fi.vm.sade.koulutusinformaatio.service.impl.metrics;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collector;

@Component
public class RollingAverageLogger {

    private final static Map<String, LogEntry> logEntries = new HashMap<>();

    public void reset(){
        logEntries.keySet().removeAll(logEntries.keySet());
    }

    public void start(String key) {
        if (logEntries.containsKey(key))
            logEntries.get(key).start();
        else
            logEntries.put(key, new LogEntry(key));
    }

    public void stop(String key) {
        logEntries.get(key).stop();
    }

    public List<String> entries(){
        List<String> r = new ArrayList<>();
        for (LogEntry s : logEntries.values()) {
           r.add(s.toString());
        }
        return r;
    }

    @Override
    public String toString() {
        return "Rolling averages: [" + StringUtils.join(logEntries.values(), ",") + "]";
    }

    private class LogEntry {
        private final String key;
        private long start;
        private double average = 0;
        private long averageCount = 0;

        LogEntry(String key) {
            this.key = key;
            start();
        }

        void start() {
            this.start = System.currentTimeMillis();
        }

        void stop() {
            averageCount++;
            long diff = System.currentTimeMillis() - start;
            average = (1.0 / averageCount) * diff +
                    (averageCount - 1.0) / averageCount * average;
        }

        @Override
        public String toString() {
            return "(" + key + ";" + averageCount + ";" + String.format("%.2f", average) + ")";
        }
    }
}
