package aoc2023;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

record PointDay14(int x, int y) {

}

record Platform(Map<PointDay14, Character> rocks, int maxX, int maxY) {
    public final static char ROUND_ROCK = 'O';
    public final static char CUBE_ROCK = '#';

    public static Platform of(List<String> lines) {
        Map<PointDay14, Character> rocks = new HashMap<>();
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                maxX = x;
                char c = line.charAt(x);
                if (c == ROUND_ROCK || c == CUBE_ROCK) {
                    rocks.put(new PointDay14(x, y), c);
                }
            }
            maxY = y;
        }

        return new Platform(rocks, maxX, maxY);
    }

    public Platform tiltNorth() {
        Map<PointDay14, Character> newRocks = new HashMap<>();
        // copy top row
        rocks.keySet().stream()
                .filter(point -> point.y() == 0)
                .forEach(point -> newRocks.put(point, rocks.get(point)));

        for (int y = 1; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                PointDay14 point = new PointDay14(x, y);
                if (handledNotRoundRock(point, newRocks)) continue;

                // find the highest point where we can move the rock to
                PointDay14 newPoint = null;
                for (int yNew = y - 1; yNew >= 0; yNew--) {
                    newPoint = new PointDay14(x, yNew);
                    if (newRocks.containsKey(newPoint)) {
                        newPoint = new PointDay14(x, yNew + 1);
                        break;
                    }
                }

                newRocks.put(newPoint, ROUND_ROCK);
            }
        }

        return new Platform(newRocks, maxX, maxY);
    }

    public Platform tiltSouth() {
        Map<PointDay14, Character> newRocks = new HashMap<>();
        // copy bottom row
        rocks.keySet().stream()
                .filter(point -> point.y() == maxY)
                .forEach(point -> newRocks.put(point, rocks.get(point)));

        for (int y = maxY - 1; y >= 0; y--) {
            for (int x = 0; x <= maxX; x++) {
                PointDay14 point = new PointDay14(x, y);
                if (handledNotRoundRock(point, newRocks)) continue;

                // find the lowest point where we can move the rock to
                PointDay14 newPoint = null;
                for (int yNew = y + 1; yNew <= maxY; yNew++) {
                    newPoint = new PointDay14(x, yNew);
                    if (newRocks.containsKey(newPoint)) {
                        newPoint = new PointDay14(x, yNew - 1);
                        break;
                    }
                }

                newRocks.put(newPoint, ROUND_ROCK);
            }
        }

        return new Platform(newRocks, maxX, maxY);
    }

    public Platform tiltWest() {
        Map<PointDay14, Character> newRocks = new HashMap<>();
        // copy most west row
        rocks.keySet().stream()
                .filter(point -> point.x() == 0)
                .forEach(point -> newRocks.put(point, rocks.get(point)));

        for (int x = 1; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                PointDay14 point = new PointDay14(x, y);
                if (handledNotRoundRock(point, newRocks)) continue;

                // find the most west point where we can move the rock to
                PointDay14 newPoint = null;
                for (int xNew = x - 1; xNew >= 0; xNew--) {
                    newPoint = new PointDay14(xNew, y);
                    if (newRocks.containsKey(newPoint)) {
                        newPoint = new PointDay14(xNew + 1, y);
                        break;
                    }
                }

                Character c = newRocks.putIfAbsent(newPoint, ROUND_ROCK);
                if (c != null) {
                    System.out.println();
                }
            }
        }

        return new Platform(newRocks, maxX, maxY);
    }

    public Platform tiltEast() {
        Map<PointDay14, Character> newRocks = new HashMap<>();
        // copy most east row
        rocks.keySet().stream()
                .filter(point -> point.x() == maxX)
                .forEach(point -> newRocks.put(point, rocks.get(point)));

        for (int x = maxX - 1; x >= 0; x--) {
            for (int y = 0; y <= maxY; y++) {
                PointDay14 point = new PointDay14(x, y);
                if (handledNotRoundRock(point, newRocks)) continue;

                // find the most east point where we can move the rock to
                PointDay14 newPoint = null;
                for (int xNew = x + 1; xNew <= maxX; xNew++) {
                    newPoint = new PointDay14(xNew, y);
                    if (newRocks.containsKey(newPoint)) {
                        newPoint = new PointDay14(xNew - 1, y);
                        break;
                    }
                }

                newRocks.put(newPoint, ROUND_ROCK);
            }
        }

        return new Platform(newRocks, maxX, maxY);
    }


    private boolean handledNotRoundRock(PointDay14 point, Map<PointDay14, Character> newRocks) {
        if (!rocks.containsKey(point)) {
            return true;
        }

        if (rocks.get(point) == CUBE_ROCK) {
            newRocks.put(point, CUBE_ROCK);
            return true;
        }

        return false;
    }

    public long totalLoad() {
        return rocks.entrySet().stream()
                .filter(pointRock -> pointRock.getValue() == ROUND_ROCK)
                .map(this::load)
                .reduce(0, Integer::sum);

    }

    private int load(Map.Entry<PointDay14, Character> pointRock) {
        return maxY + 1 - pointRock.getKey().y();
    }

    public Platform cycle() {
        return tiltNorth().tiltWest().tiltSouth().tiltEast();
    }

    public Long totalLoadAfterManyCycles() {
        List<Long> totalLoads = new ArrayList<>();

        Platform platform = this;
        for (int cycle = 0; cycle < 10_000; cycle++) {
            platform = platform.cycle();
            totalLoads.add(platform.totalLoad());

            if (cycle < 100) {
                continue;
            }

            // try to find a repetition, starting from the back
            // the beginning has some initial, non-repetitive tilting
            for (int lengthRepetition = 5; lengthRepetition < cycle / 2; lengthRepetition++) {
                List<Long> repetition = totalLoads.subList(totalLoads.size() - lengthRepetition, totalLoads.size());
                List<Long> partBeforeRepetition = totalLoads.subList(totalLoads.size() - 2 * lengthRepetition, totalLoads.size() - lengthRepetition);

                if (!repetition.equals(partBeforeRepetition)) {
                    continue;
                }

                // keep removing the repetition from the back
                List<Long> firstPart = totalLoads.subList(0, totalLoads.size() - lengthRepetition);
                while (firstPart.subList(firstPart.size() - lengthRepetition, firstPart.size()).equals(repetition)) {
                    firstPart = firstPart.subList(0, firstPart.size() - lengthRepetition);
                }

                System.out.println("cycle = " + cycle);
                System.out.println("repetition = " + repetition);
                System.out.println("repetition.size() = " + repetition.size());
                System.out.println("firstPart.size() = " + firstPart.size());

                // so the list consists of 'firstPart' and the repeating of 'repetition'
                int index = 1_000_000_000 % repetition.size() - 1;
                while (index < firstPart.size()) {
                    index += repetition.size();
                }

                return totalLoads.get(index);
            }
        }

        throw new IllegalStateException("no solution");
    }
}

public class Day14 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Platform platform = Platform.of(inputRaw);

        platform = platform.tiltNorth();

        long result = platform.totalLoad();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {

        Platform platform = Platform.of(inputRaw);

        long result = platform.totalLoadAfterManyCycles();

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
