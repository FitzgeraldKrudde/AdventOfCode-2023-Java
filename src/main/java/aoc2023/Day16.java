package aoc2023;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static aoc2023.DirectionDay16.D;
import static aoc2023.DirectionDay16.L;
import static aoc2023.DirectionDay16.R;
import static aoc2023.DirectionDay16.U;

enum DirectionDay16 {U, D, L, R}

record PointDay16(int x, int y) {
    public PointDay16 move(DirectionDay16 direction) {
        return switch (direction) {
            case U -> new PointDay16(x, y - 1);
            case D -> new PointDay16(x, y + 1);
            case L -> new PointDay16(x - 1, y);
            case R -> new PointDay16(x + 1, y);
        };
    }
}

record PointWithDirection(PointDay16 point, DirectionDay16 direction) {
    public final static char EMPTY = '.';
    public final static char BEAM_LEFT = '\\';
    public final static char BEAM_RIGHT = '/';
    public final static char SPLITTER_HORIZONTAL = '-';
    public final static char SPLITTER_VERTICAL = '|';

    public List<PointWithDirection> move(char laser) {

        if (laser == EMPTY) return List.of(new PointWithDirection(point().move(direction), direction));

        if (laser == BEAM_LEFT && direction == L ||
            laser == BEAM_RIGHT && direction == R
        ) return List.of(new PointWithDirection(point().move(U), U));

        if (laser == BEAM_LEFT && direction == R ||
            laser == BEAM_RIGHT && direction == L
        ) return List.of(new PointWithDirection(point().move(D), D));

        if (laser == BEAM_LEFT && direction == U ||
            laser == BEAM_RIGHT && direction == D
        ) return List.of(new PointWithDirection(point().move(L), L));

        if (laser == BEAM_LEFT && direction == D ||
            laser == BEAM_RIGHT && direction == U
        ) return List.of(new PointWithDirection(point().move(R), R));


        if (laser == SPLITTER_HORIZONTAL && (direction == R || direction == L))
            return List.of(new PointWithDirection(point().move(direction), direction));

        if (laser == SPLITTER_VERTICAL && (direction == D || direction == U))
            return List.of(new PointWithDirection(point().move(direction), direction));

        if (laser == SPLITTER_VERTICAL && (direction == L || direction == R))
            return List.of(
                    new PointWithDirection(point().move(U), U),
                    new PointWithDirection(point().move(D), D)
            );

        if (laser == SPLITTER_HORIZONTAL && (direction == D || direction == U))
            return List.of(
                    new PointWithDirection(point().move(L), L),
                    new PointWithDirection(point().move(R), R)
            );

        throw new IllegalStateException("move error");
    }
}

record Contraption(Map<PointDay16, Character> map) {

    public static Contraption of(List<String> lines) {
        Map<PointDay16, Character> map = new HashMap<>();

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                map.put(new PointDay16(x, y), line.charAt(x));
            }
        }

        return new Contraption(map);
    }

    public long nrEnergizedTiles() {
        return nrEnergizedTiles(new PointWithDirection(new PointDay16(0, 0), R));
    }

    public long nrEnergizedTiles(PointWithDirection currentPoint) {
        Set<PointWithDirection> visitedPoints = new HashSet<>();
        Set<PointDay16> energizedTiles = new HashSet<>();

        beam(visitedPoints, energizedTiles, currentPoint);

        return energizedTiles.size();
    }

    private void beam(Set<PointWithDirection> visitedPoints, Set<PointDay16> energizedTiles, PointWithDirection pointWithDirection) {
        while (map.containsKey(pointWithDirection.point()) && !visitedPoints.contains(pointWithDirection)) {
            energizedTiles.add(pointWithDirection.point());
            visitedPoints.add(pointWithDirection);

            List<PointWithDirection> nextPoints = pointWithDirection.move(map.get(pointWithDirection.point()));
            nextPoints.forEach(nextPoint -> beam(visitedPoints, energizedTiles, nextPoint));
        }
    }

    public long maxNrEnergizedTilesForAllEdgeTiles() {
        int maxX = map.keySet().stream()
                .map(PointDay16::x)
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("no max"));
        int maxY = map.keySet().stream()
                .map(PointDay16::y)
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("no max"));

        return Stream.of(
                        IntStream.range(0, maxX)
                                .boxed()
                                .map(x -> new PointWithDirection(new PointDay16(x, 0), D)),

                        IntStream.range(0, maxX)
                                .boxed()
                                .map(x -> new PointWithDirection(new PointDay16(x, maxY), U)),

                        IntStream.range(0, maxY)
                                .boxed()
                                .map(y -> new PointWithDirection(new PointDay16(0, y), R)),

                        IntStream.range(0, maxY)
                                .boxed()
                                .map(y -> new PointWithDirection(new PointDay16(maxX, y), L))
                )
                .flatMap(pointWithDirectionStream -> pointWithDirectionStream)
                .map(this::nrEnergizedTiles)
                .max(Long::compare)
                .orElseThrow(() -> new IllegalStateException("no max found"));
    }
}

public class Day16 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Contraption contraption = Contraption.of(inputRaw);

        long result = contraption.nrEnergizedTiles();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Contraption contraption = Contraption.of(inputRaw);

        long result = contraption.maxNrEnergizedTilesForAllEdgeTiles();

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
