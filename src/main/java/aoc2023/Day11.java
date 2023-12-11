package aoc2023;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


record PointDay11(int x, int y) {
    public long distance(PointDay11 other) {
        return Math.max(x, other.x()) - Math.min(x, other.x()) +
               Math.max(y, other.y()) - Math.min(y, other.y());
    }
}

record Pair(PointDay11 p1, PointDay11 p2) {
    public long distance() {
        return p1.distance(p2);
    }
}

record Image(Map<PointDay11, Character> map, List<Integer> emptyRows, List<Integer> emptyColumns) {
    final public static char GALAXY = '#';

    public static Image of(List<String> lines) {
        Map<PointDay11, Character> map = new HashMap<>();

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                map.put(new PointDay11(x, y), line.charAt(x));
            }
        }

        // find rows without galaxies
        List<Integer> emptyRows = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            boolean galaxiesFound = false;
            for (int x = 0; x < line.length(); x++) {
                if (map.get(new PointDay11(x, y)) == GALAXY) {
                    galaxiesFound = true;
                }
            }

            if (!galaxiesFound) {
                emptyRows.add(y);
            }
        }

        // find columns without galaxies
        List<Integer> emptyColumns = new ArrayList<>();
        for (int x = 0; x < lines.get(0).length(); x++) {
            boolean galaxiesFound = false;
            for (int y = 0; y < lines.size(); y++) {
                if (map.get(new PointDay11(x, y)) == GALAXY) {
                    galaxiesFound = true;
                }
            }

            if (!galaxiesFound) {
                emptyColumns.add(x);
            }
        }

        return new Image(map, emptyRows, emptyColumns);
    }

    public long sumShortestPaths(int factor) {
        List<PointDay11> galaxies = galaxies();

        List<Pair> pairs = galaxies.stream()
                .flatMap(point1 -> galaxies.stream()
                        .map(point2 -> new Pair(point1, point2))
                )
                .toList();

        long sumShortestPaths = pairs.stream()
                .map(pair -> distance(pair, factor))
                .reduce(0L, Long::sum);

        return sumShortestPaths / 2;
    }

    private long distance(Pair pair, int factor) {
        PointDay11 p1 = pair.p1();
        int emptyRowsBefore1 = getEmptyRowsBefore(p1);
        int emptyColumnsBefore1 = getEmptyColumnsBefore(p1);

        PointDay11 p2 = pair.p2();
        int emptyRowsBefore2 = getEmptyRowsBefore(p2);
        int emptyColumnsBefore2 = getEmptyColumnsBefore(p2);

        int adjustedFactor = factor == 1 ? 1 : factor - 1;
        return new Pair(
                new PointDay11(p1.x() + adjustedFactor * emptyColumnsBefore1, p1.y() + adjustedFactor * emptyRowsBefore1),
                new PointDay11(p2.x() + adjustedFactor * emptyColumnsBefore2, p2.y() + adjustedFactor * emptyRowsBefore2)
        ).distance();
    }

    private int getEmptyColumnsBefore(PointDay11 point) {
        return (int) emptyColumns.stream()
                .filter(column -> column < point.x())
                .count();
    }

    private int getEmptyRowsBefore(PointDay11 point) {
        return (int) emptyRows.stream()
                .filter(row -> row < point.y())
                .count();
    }

    public List<PointDay11> galaxies() {
        return map.entrySet().stream()
                .filter(pointDay11CharacterEntry -> pointDay11CharacterEntry.getValue() == GALAXY)
                .map(Map.Entry::getKey)
                .toList();
    }
}

public class Day11 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Image image = Image.of(inputRaw);

        long result = image.sumShortestPaths(1);

        return String.valueOf(result);
    }

    public String doPart2(List<String> inputRaw, int factor) {
        Image image = Image.of(inputRaw);

        long result = image.sumShortestPaths(factor);

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        return doPart2(inputRaw, 1_000_000);
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
