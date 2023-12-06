package aoc2023;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

record Race(long time, long distance) {
    public static Race of(List<String> lines) {
        long time = Long.parseLong(lines.get(0).split(":")[1].replaceAll("\\s+", ""));
        long distance = Long.parseLong(lines.get(1).split(":")[1].replaceAll("\\s+", ""));

        return new Race(time, distance);
    }

    public long nrWins() {
        int nrWins = 0;

        for (int i = 0; i <= time; i++) {
            if ((time - i) * i > distance) {
                nrWins++;
            }
        }

        return nrWins;
    }
}

record Races(List<Race> races) {
    public static Races of(List<String> lines) {
        List<Long> times = Arrays.stream(lines.get(0).split(":")[1].trim().split("\\s+"))
                .map(Long::valueOf)
                .toList();
        List<Long> distances = Arrays.stream(lines.get(1).split(":")[1].trim().split("\\s+"))
                .map(Long::valueOf)
                .toList();

        return new Races(
                IntStream.range(0, times.size())
                        .mapToObj(i -> new Race(times.get(i), distances.get(i)))
                        .collect(toList())
        );
    }

    public long winsMultiplied() {
        return races.stream()
                .map(Race::nrWins)
                .reduce(1L, (l1, l2) -> l1 * l2);
    }
}

public class Day06 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Races races = Races.of(inputRaw);

        long result = races.winsMultiplied();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Race race = Race.of(inputRaw);

        long result = race.nrWins();

        return String.valueOf(result);
    }

    // @formatter:off
    static public void main(String[] args) throws Exception {
        // get our class
        final Class<?> clazz = new Object() {
        }.getClass().getEnclosingClass();

        // construct filename with input
        final String filename = clazz.getSimpleName().toLowerCase().replace("day0", "day") + ".txt";

        // get the classname
        final String fullClassName = clazz.getCanonicalName();

        // create instance
        Day day = (Day) Class.forName(fullClassName).getDeclaredConstructor().newInstance();

        // invoke "main" from the base Day class
        day.main(filename);
    }
    // @formatter:on
}
