package aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

record History(List<Long> values) {
    public Long extrapolatedValue() {
        List<History> historicDifferences = getHistoricDifferences();

        return historicDifferences.stream()
                .map(diff -> diff.values().get(diff.values().size() - 1))
                .reduce(0L, Long::sum);
    }

    private List<History> getHistoricDifferences() {
        List<History> historicDifferences = new ArrayList<>(List.of(this));

        History differences = getDifferences(this);
        historicDifferences.add(differences);

        while (!differences.areAllZeros()) {
            differences = getDifferences(differences);
            historicDifferences.add(differences);
        }
        return historicDifferences;
    }

    private static History getDifferences(History history) {
        return new History(IntStream.range(0, history.values().size() - 1)
                .mapToObj(i -> history.values.get(i + 1) - history.values.get(i))
                .toList());
    }

    public boolean areAllZeros() {
        return values.stream()
                .allMatch(l -> l == 0L);
    }

    public long extrapolatedBackwardsValue() {
        List<History> historicDifferences = getHistoricDifferences();

        long firstValue = 0;

        for (int i = historicDifferences.size() - 1; i >= 0; i--) {
            firstValue = historicDifferences.get(i).values.get(0) - firstValue;
        }

        return firstValue;
    }
}

public class Day09 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<History> histories = parseInput(inputRaw);

        Long result = histories.stream()
                .map(History::extrapolatedValue)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<History> histories = parseInput(inputRaw);

        Long result = histories.stream()
                .map(History::extrapolatedBackwardsValue)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    private List<History> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
                .map(line -> Arrays.stream(line.split("\\s+"))
                        .map(Long::valueOf)
                        .toList())
                .map(History::new)
                .collect(toList());
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
