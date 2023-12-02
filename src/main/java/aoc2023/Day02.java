package aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Day02 extends Day {
    enum Color {
        RED, GREEN, BLUE;

        static Color of(String s) {
            return valueOf(s.toUpperCase());
        }
    }

    record Cube(Color color, int nr) {
        static Cube of(String s) {
            String[] parts = s.trim().split("\\s+");
            return new Cube(Color.of(parts[1]), Integer.parseInt(parts[0]));
        }
    }

    record Grab(List<Cube> cubes) {
        static Grab of(String s) {
            return new Grab(Arrays.stream(s.trim().split(","))
                    .map(Cube::of)
                    .collect(toList()));
        }
    }

    record Game(int id, List<Grab> grabs) {
        static Game of(String line) {
            String[] parts = line.trim().split(":");
            int id = Integer.parseInt(parts[0].split("\\s+")[1].trim());
            List<Grab> grabs = Arrays.stream(parts[1].split(";"))
                    .map(Grab::of)
                    .collect(toList());
            return new Game(id, grabs);
        }
        public Bag bagWithFewestCubes() {
            // collect ALL the grabs in a list
            ArrayList<Cube> allCubes = new ArrayList<>(grabs.stream()
                    .flatMap(grab -> grab.cubes().stream())
                    .toList());

            // find the largest pick per color
            Map<Color, Integer> largestPicks = allCubes.stream()
                    .collect(toMap(Cube::color, Cube::nr, Math::max));

            // convert to bag
            List<Cube> cubes = largestPicks.entrySet().stream()
                    .map(colorIntegerEntry -> new Cube(colorIntegerEntry.getKey(), colorIntegerEntry.getValue()))
                    .collect(toList());

            return new Bag(cubes);
        }
    }

    record Bag(List<Cube> cubes) {
        boolean gamePossible(Game game) {
            return game.grabs.stream()
                    .allMatch(grab -> grab.cubes().stream()
                            .allMatch(this::picksPossible));
        }

        private boolean picksPossible(Cube cube) {
            return cubes.stream()
                    .filter(cubeFromBag -> cubeFromBag.color.equals(cube.color()))
                    .anyMatch(cubeFromBag -> cubeFromBag.nr >= cube.nr());
        }

        public long power() {
            return cubes().stream()
                    .map(cube -> cube.nr)
                    .reduce(1, (l1, l2) -> l1 * l2);
        }
    }

    @Override
    public String doPart1(List<String> inputRaw) {
        List<Game> games = inputRaw.stream()
                .map(Game::of)
                .toList();

        Bag bag = new Bag(Arrays.asList(
                new Cube(Color.RED, 12),
                new Cube(Color.GREEN, 13),
                new Cube(Color.BLUE, 14)
        ));

        long result = games.stream()
                .filter(bag::gamePossible)
                .map(game -> game.id)
                .reduce(0, Integer::sum);

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Game> games = inputRaw.stream()
                .map(Game::of)
                .toList();

        long result = games.stream()
                .map(Game::bagWithFewestCubes)
                .map(Bag::power)
                .reduce(0L, Long::sum);

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

