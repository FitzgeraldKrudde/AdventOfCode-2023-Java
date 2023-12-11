package aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static aoc2023.Direction.E;
import static aoc2023.Direction.N;
import static aoc2023.Direction.S;
import static aoc2023.Direction.W;

enum Direction {N, S, E, W}

record PointDay10(int x, int y) {
    PointDay10 nextPoint(Direction direction) {
        return switch (direction) {
            case N -> new PointDay10(x, y - 1);
            case E -> new PointDay10(x + 1, y);
            case S -> new PointDay10(x, y + 1);
            case W -> new PointDay10(x - 1, y);
        };
    }

    boolean isAbove(PointDay10 other) {
        return x == other.x() && y + 1 == other.y();
    }

    boolean isRightOf(PointDay10 other) {
        return x + 1 == other.x() && y == other.y();
    }

    boolean isLeftOf(PointDay10 other) {
        return x - 1 == other.x() && y == other.y();
    }
}

record Field(Map<PointDay10, Character> map, PointDay10 start) {
    final public static char GROUND = '.';
    final public static char VERTICAL = '|';
    final public static char HORIONTAL = '-';
    final public static char NORTH_EAST = 'L';
    final public static char NORTH_WEST = 'J';
    final public static char SOUTH_WEST = '7';
    final public static char SOUTH_EAST = 'F';

    public static Field of(List<String> lines) {
        Map<PointDay10, Character> map = new HashMap<>();
        PointDay10 start;

        IntStream.range(0, lines.size())
                .forEach(y -> IntStream.range(0, lines.get(y).length())
                        .forEach(x -> map.put(new PointDay10(x, y), lines.get(y).charAt(x)))
                );

        start = map.entrySet().stream()
                .filter(pointDay10CharacterEntry -> pointDay10CharacterEntry.getValue() == 'S')
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("start not found"));

        return new Field(map, start);
    }

    public long findLargestNrStepsInLoop() {
        List<PointDay10> loop = findLoop();

        return loop.size() / 2;
    }

    private List<PointDay10> findLoop() {
        return Arrays.stream(Direction.values())
                .map(direction -> findLoop(start, direction))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no loop found"));
    }

    private List<PointDay10> findLoop(PointDay10 start, Direction direction) {
        PointDay10 currentPoint = start;
        Direction currentDirection = direction;

        List<PointDay10> loop = new ArrayList<>(List.of(start));
        PointDay10 nextPoint = currentPoint.nextPoint(direction);

        while (!nextPoint.equals(start)) {
            // off the field (maybe not needed)
            if (!map.containsKey(nextPoint)) {
                return null;
            }

            // next point is ground
            if (map.get(nextPoint) == GROUND) {
                return null;
            }

            // some internal loop not ending at start (maybe not needed)
            if (loop.contains(nextPoint)) {
                return null;
            }

            try {
                currentDirection = switch (currentDirection) {
                    case N -> nextDirectionGoingNorth(map.get(nextPoint));
                    case E -> nextDirectionGoingEast(map.get(nextPoint));
                    case S -> nextDirectionGoingSouth(map.get(nextPoint));
                    case W -> nextDirectionGoingWest(map.get(nextPoint));
                };

                loop.add(nextPoint);
                currentPoint = nextPoint;
                nextPoint = currentPoint.nextPoint(currentDirection);
            } catch (IllegalStateException ise) {
                return null;
            }
        }

        return loop;
    }

    private Direction nextDirectionGoingNorth(Character pipe) {
        return switch (pipe) {
            case VERTICAL -> N;
            case SOUTH_WEST -> W;
            case SOUTH_EAST -> E;
            default -> throw new IllegalStateException("Unexpected value: " + pipe);
        };
    }

    private Direction nextDirectionGoingSouth(Character pipe) {
        return switch (pipe) {
            case VERTICAL -> S;
            case NORTH_EAST -> E;
            case NORTH_WEST -> W;
            default -> throw new IllegalStateException("Unexpected value: " + pipe);
        };
    }

    private Direction nextDirectionGoingEast(Character pipe) {
        return switch (pipe) {
            case HORIONTAL -> E;
            case SOUTH_WEST -> S;
            case NORTH_WEST -> N;
            default -> throw new IllegalStateException("Unexpected value: " + pipe);
        };
    }

    private Direction nextDirectionGoingWest(Character pipe) {
        return switch (pipe) {
            case HORIONTAL -> W;
            case NORTH_EAST -> N;
            case SOUTH_EAST -> S;
            default -> throw new IllegalStateException("Unexpected value: " + pipe);
        };
    }

    public long findNrEnclosedTiles() {
        List<PointDay10> loop = findLoop();

        replaceStartWithActualPipe(loop);

        // used a hint for Reddit, could not figure out a nice way
        return map.keySet().stream()
                .filter(point -> !loop.contains(point))
                .filter(point1 -> isInsideLoop(loop, point1))
                .count();
    }

    private void replaceStartWithActualPipe(List<PointDay10> loop) {
        PointDay10 pointAfterStart = loop.get(1);
        PointDay10 pointBeforeStart = loop.getLast();

        if (pointBeforeStart.x() == pointAfterStart.x()) {
            map.put(start, VERTICAL);
        } else if (pointBeforeStart.isAbove(start) && pointAfterStart.isRightOf(start) ||
                   pointAfterStart.isAbove(start) && pointBeforeStart.isRightOf(start)
        ) {
            map.put(start, NORTH_EAST);
        } else if (pointBeforeStart.isLeftOf(start) && pointAfterStart.isAbove(start) ||
                   pointAfterStart.isLeftOf(start) && pointBeforeStart.isAbove(start)
        )
            map.put(start, NORTH_WEST);
    }

    private boolean isInsideLoop(List<PointDay10> loop, PointDay10 point) {
        // looking from the left: if the number of walls is odd then it is inside
        // only consider: | L J
        int nrWalls = 0;

        for (int x = 0; x < point.x(); x++) {
            PointDay10 pointBefore = new PointDay10(x, point.y());

            // skip laying around junk
            if (!loop.contains(pointBefore)) {
                continue;
            }

            char c = map.get(pointBefore);
            if (c == VERTICAL) {
                nrWalls++;
            }
            if (c == NORTH_EAST || c == NORTH_WEST) {
                nrWalls++;
            }
        }

        return nrWalls % 2 != 0;
    }
}

public class Day10 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Field field = Field.of(inputRaw);

        long result = field.findLargestNrStepsInLoop();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Field field = Field.of(inputRaw);

        long result = field.findNrEnclosedTiles();

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
