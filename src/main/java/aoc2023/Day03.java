package aoc2023;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Day03 extends Day {
    enum Type {
        SYMBOL, POTENTIAL_PARTNR
    }

    record Point(int x, int y) {
        List<Point> getNeighbours() {
            return List.of(
                    new Point(x, y - 1),
                    new Point(x + 1, y - 1),
                    new Point(x + 1, y),
                    new Point(x + 1, y + 1),
                    new Point(x, y + 1),
                    new Point(x - 1, y + 1),
                    new Point(x - 1, y),
                    new Point(x - 1, y - 1)
            );
        }
    }

    record EnginePart(Type type, String value) {
    }

    record Engine(Map<Point, EnginePart> parts) {
        private boolean isPartNr(Point point, EnginePart enginePart) {
            List<Point> neightbours = getNeightboursOfPartNr(point, Integer.parseInt(enginePart.value()));

            return neightbours.stream()
                    .filter(parts::containsKey)
                    .map(parts::get)
                    .anyMatch(enginePart1 -> enginePart1.type == Type.SYMBOL);
        }

        private List<Point> getNeightboursOfPartNr(Point point, int partnr) {
            return IntStream.of(point.x(), point.x() + String.valueOf(partnr).length() - 1)
                    .mapToObj(position -> new Point(position, point.y()))
                    .flatMap(points -> points.getNeighbours().stream())
                    .distinct()
                    .toList();
        }

        public Long sumGearRatios() {
            Map<Point, EnginePart> partsMap = partNrs();

            return parts.entrySet().stream()
                    .filter(pointEnginePartEntry -> pointEnginePartEntry.getValue().value().equals("*"))
                    .map(Map.Entry::getKey)
                    .map(point -> adjacentPartNrs(point, partsMap))
                    .filter(partNrPoints -> partNrPoints.size() == 2)
                    .map(partNrPoints -> Long.parseLong(parts.get(partNrPoints.get(0)).value) * Long.parseLong(parts.get(partNrPoints.get(1)).value()))
                    .reduce(0L, Long::sum);
        }

        private List<Point> adjacentPartNrs(Point gearPoint, Map<Point, EnginePart> partsMap) {
            return partsMap.entrySet().stream()
                    .filter(pointEnginePartEntry -> getNeightboursOfPartNr(pointEnginePartEntry.getKey(), Integer.parseInt(pointEnginePartEntry.getValue().value())).contains(gearPoint))
                    .map(Map.Entry::getKey)
                    .collect(toList());
        }

        static Engine of(List<String> lines) {
            Map<Point, EnginePart> engineParts = new HashMap<>();

            for (int y = 0; y < lines.size(); y++) {
                String line = lines.get(y);
                for (int x = 0; x < line.length(); x++) {
                    if (line.charAt(x) == '.') {
                        continue;
                    }

                    if (Character.isDigit(line.charAt(x))) {
                        String numberString = line.substring(x).splitWithDelimiters("[0-9]+", 0)[1];
                        engineParts.put(new Point(x, y), new EnginePart(Type.POTENTIAL_PARTNR, numberString));
                        x += numberString.length() - 1;
                    } else {
                        // symbol
                        engineParts.put(new Point(x, y), new EnginePart(Type.SYMBOL, String.valueOf(line.charAt(x))));
                    }
                }
            }
            return new Engine(engineParts);
        }

        public long sumPartNrs() {
            return partNrs().values().stream()
                    .map(EnginePart::value)
                    .map(Long::parseLong)
                    .reduce(0L, Long::sum);
        }

        private Map<Point, EnginePart> partNrs() {
            return parts.entrySet().stream()
                    .filter(pointEnginePartEntry -> pointEnginePartEntry.getValue().type() == Type.POTENTIAL_PARTNR)
                    .filter(pointEnginePartEntry -> isPartNr(pointEnginePartEntry.getKey(), pointEnginePartEntry.getValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    @Override
    public String doPart1(List<String> inputRaw) {
        Engine engine = Engine.of(inputRaw);

        long result = engine.sumPartNrs();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Engine engine = Engine.of(inputRaw);

        long result = engine.sumGearRatios();

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
