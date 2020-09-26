package drago;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class TrafficMatcher {

    static class Traffic {
        final String id;
        final int visits;

        Traffic(String id, int visits) {
            this.id = id;
            this.visits = visits;
        }

        @Override
        public String toString() {
            return id + " " + visits;
        }

        public String getId() {
            return id;
        }

        public int getVisits() {
            return visits;
        }
    }

    static class Tracker {
        final String id;
        final int total;
        final int valid;
        final int vpn;
        final int junk;
        final int bot;

        Tracker(String[] args) {
            this.id = args[0];
            this.total = Integer.parseInt(args[1]);
            this.valid = Integer.parseInt(args[2]);
            this.vpn = Integer.parseInt(args[3]);
            this.junk = Integer.parseInt(args[4]);
            this.bot = Integer.parseInt(args[5]);
        }

        public Tracker(String id, int total, int valid, int vpn, int junk, int bot) {
            this.id = id;
            this.total = total;
            this.valid = valid;
            this.vpn = vpn;
            this.junk = junk;
            this.bot = bot;
        }

        public int getValid() {
            return valid;
        }

        public int getVpn() {
            return vpn;
        }

        public int getJunk() {
            return junk;
        }

        public int getBot() {
            return bot;
        }

        @Override
        public String toString() {
            return id +
                    "," + total +
                    "," + valid +
                    "," + vpn +
                    "," + junk +
                    "," + bot;
        }

        public String getId() {
            return id;
        }

        public int getTotal() {
            return total;
        }
    }

    static class Result {

        private final Tracker trackerRecord;
        private final Integer visits;
        private final float loss;

        public Result(Tracker trackerRecord, Integer visits, float loss) {
            this.trackerRecord = trackerRecord;
            this.visits = visits;
            this.loss = loss;
        }

        @Override
        public String toString() {
            return trackerRecord +
                    "," + visits +
                    "," + String.format(Locale.CANADA, "%.2f", loss);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar trafficMatcher.jar traffic.csv tracker.csv > results.csv");
            System.exit(1);
        }
        String traffic = args[0];
        String tracker = args[1];

        //org.jooq.lambda.Unchecked to the rescue
        Stream<String> trafficLines = null;
        Stream<String> trackerLines = null;
        try {
            trafficLines = Files.lines(Paths.get(traffic));
            trackerLines = Files.lines(Paths.get(tracker));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Traffic> trafficRecords = trafficLines
                .skip(1)
                .map(l -> l.split(","))
                .map((String[] record) -> new Traffic(record[0], Integer.parseInt(record[1])))
                .collect(toList());

        List<Tracker> trackerRecords = trackerLines
                .skip(1)
                .map(l -> l.split(","))
                .map(Tracker::new)
                .collect(toList());

        Map<String, Integer> groupedTraffic = trafficRecords.stream()
                .limit(trafficRecords.size() - 3)
                .collect(Collectors.groupingBy(Traffic::getId,
                        Collectors.summingInt(Traffic::getVisits)));

        Map<Tracker, List<Tracker>> groupedTracker = trackerRecords.stream()
                .collect(Collectors.groupingBy(Tracker::getId)).entrySet().stream()
                .collect(Collectors.toMap(x -> {
                    final int sumTotal = x.getValue().stream().mapToInt(Tracker::getTotal).sum();
                    final int sumValid = x.getValue().stream().mapToInt(Tracker::getValid).sum();
                    final int sumVPN = x.getValue().stream().mapToInt(Tracker::getVpn).sum();
                    final int sumJunk = x.getValue().stream().mapToInt(Tracker::getJunk).sum();
                    final int sumBot = x.getValue().stream().mapToInt(Tracker::getBot).sum();
                    return new Tracker(x.getKey(), sumTotal, sumValid, sumVPN, sumJunk, sumBot);
                }, Map.Entry::getValue));

//        groupedTraffic.forEach((s, integer) -> System.out.println(s + " " + integer));
//        groupedTracker.keySet().stream().forEach(System.out::println);

        List<Result> results = groupedTracker.keySet().stream()
                .filter(trackerRecord -> groupedTraffic.keySet().contains(trackerRecord.id))
                .map(trackerRecord -> {
                    Integer visits = groupedTraffic.get(trackerRecord.id);
                    float ratio = trackerRecord.valid / visits.floatValue();
                    float loss = 1 - ratio;
                    return new Result(trackerRecord, visits, loss);
                })
                .collect(Collectors.toList());

        String fields = Arrays.stream(Tracker.class.getDeclaredFields())
                .map(Field::getName)
                .collect(joining(","));
        System.out.println(fields + ",visits,loss");
        results.forEach(System.out::println);
    }
}
