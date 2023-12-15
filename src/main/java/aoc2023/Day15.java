package aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

record InitializationStep(String step) {
    private long hash(String s) {
        int currentValue = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            currentValue += c;
            currentValue *= 17;
            currentValue %= 256;
        }

        return currentValue;
    }

    public long hashStep() {
        return hash(step);
    }

    public long hashLabel() {
        String[] split = step.split("[=-]");
        return hash(split[0]);
    }

    public String label() {
        return step.split("[=-]")[0];
    }
}

record InitializationSequence(List<InitializationStep> steps) {
    public static InitializationSequence of(List<String> lines) {
        return new InitializationSequence(lines.stream()
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(InitializationStep::new)
                .toList());
    }

    public long sumHashes() {
        return steps.stream()
                .map(InitializationStep::hashStep)
                .reduce(0L, Long::sum);
    }
}

record Lens(String label, long focalLength) {
}

record Box(List<Lens> lenses) {
    public static Box newBox() {
        return new Box(new ArrayList<>());
    }

    public long focusingPower(int boxNr) {
        return IntStream.range(0, lenses().size())
                .mapToObj(lensNr -> (boxNr + 1) * (lensNr + 1) * lenses.get(lensNr).focalLength())
                .reduce(0L, Long::sum);
    }
}

record Facility(Map<Integer, Box> boxes) {
    private static final int NR_BOXES = 256;

    public static Facility newFacility() {
        return new Facility(IntStream.range(0, NR_BOXES)
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> Box.newBox())));
    }

    public long totalFocusingPower() {
        return boxes.entrySet().stream()
                .map(integerBoxEntry -> integerBoxEntry.getValue().focusingPower(integerBoxEntry.getKey()))
                .reduce(0L, Long::sum);
    }

    public void applyLensOperations(InitializationSequence initializationSequence) {
        initializationSequence.steps().forEach(this::applyLensOperation);
    }

    private void applyLensOperation(InitializationStep s) {
        String[] split = s.step().split("[=-]");
        int boxNr = Math.toIntExact(s.hashLabel());
        String lensLabel = s.label();
        char operation = s.step().charAt(lensLabel.length());
        Box box = boxes.get(boxNr);
        switch (operation) {
            case '-':
                box.lenses().removeIf(lens -> lens.label().equals(lensLabel));
                break;
            case '=':
                int focalLength = Integer.parseInt(split[1]);
                Lens newLens = new Lens(lensLabel, focalLength);
                if (box.lenses().stream()
                        .noneMatch(lens -> lens.label().equals(lensLabel))) {
                    box.lenses().add(newLens);
                } else {
                    for (int i = 0; i < box.lenses().size(); i++) {
                        if (box.lenses().get(i).label().equals(lensLabel)) {
                            box.lenses().set(i, newLens);
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException("unexpected operation: " + operation);
        }
    }
}

public class Day15 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        InitializationSequence initializationSequence = InitializationSequence.of(inputRaw);

        long result = initializationSequence.sumHashes();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        InitializationSequence initializationSequence = InitializationSequence.of(inputRaw);

        Facility facility = Facility.newFacility();
        facility.applyLensOperations(initializationSequence);

        long result = facility.totalFocusingPower();

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
