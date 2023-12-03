package aoc2023;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

public class Day01 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Long result = inputRaw.stream()
                .map(this::getFirstAndLastDigit)
                .map(Long::valueOf)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    private String getFirstAndLastDigit(String line) {
        Character firstDigit = getFirstDigit(line);
        Character lastDigit = getLastDigit(line);

        return String.valueOf(firstDigit) + lastDigit;
    }

    private Character getFirstDigit(String line) {
        return line.chars()
                .mapToObj(c -> (char) c)
                .filter(Character::isDigit)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no digit found"));
    }

    private Character getLastDigit(String line) {
        return line.chars()
                .mapToObj(c -> (char) c)
                .filter(Character::isDigit)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalArgumentException("no digit found"));
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Long result = inputRaw.stream()
                .map(this::getFirstAndLastDigitAlsoVerbally)
                .map(Long::valueOf)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    private String getFirstAndLastDigitAlsoVerbally(String line) {
        String firstDigit = getFirstDigitAlsoVerbally(line);
        String lastDigit = getLastDigitAlsoVerbally(line);

        return firstDigit + lastDigit;
    }

    private String getLastDigitAlsoVerbally(String line) {
        int indexFirstDigit = Stream.of(
                        line.lastIndexOf('1'),
                        line.lastIndexOf('2'),
                        line.lastIndexOf('3'),
                        line.lastIndexOf('4'),
                        line.lastIndexOf('5'),
                        line.lastIndexOf('6'),
                        line.lastIndexOf('7'),
                        line.lastIndexOf('8'),
                        line.lastIndexOf('9'),
                        line.lastIndexOf("one"),
                        line.lastIndexOf("two"),
                        line.lastIndexOf("three"),
                        line.lastIndexOf("four"),
                        line.lastIndexOf("five"),
                        line.lastIndexOf("six"),
                        line.lastIndexOf("seven"),
                        line.lastIndexOf("eight"),
                        line.lastIndexOf("nine")
                )
                .filter(i -> i >= 0)
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("no digit found"));

        return getDigitFromFront(StringUtils.substring(line, indexFirstDigit, indexFirstDigit + 5));
    }

    private String getFirstDigitAlsoVerbally(String line) {
        int indexFirstDigit = Stream.of(
                        line.indexOf('1'),
                        line.indexOf('2'),
                        line.indexOf('3'),
                        line.indexOf('4'),
                        line.indexOf('5'),
                        line.indexOf('6'),
                        line.indexOf('7'),
                        line.indexOf('8'),
                        line.indexOf('9'),
                        line.indexOf("one"),
                        line.indexOf("two"),
                        line.indexOf("three"),
                        line.indexOf("four"),
                        line.indexOf("five"),
                        line.indexOf("six"),
                        line.indexOf("seven"),
                        line.indexOf("eight"),
                        line.indexOf("nine")
                )
                .filter(i -> i >= 0)
                .min(Integer::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("no digit found"));

        return getDigitFromFront(StringUtils.substring(line, indexFirstDigit, indexFirstDigit + 5));
    }

    private String getDigitFromFront(String s) {
        if (Character.isDigit(s.charAt(0))) {
            return String.valueOf(s.charAt(0));
        }

        if (s.startsWith("one")) return "1";
        if (s.startsWith("two")) return "2";
        if (s.startsWith("three")) return "3";
        if (s.startsWith("four")) return "4";
        if (s.startsWith("five")) return "5";
        if (s.startsWith("six")) return "6";
        if (s.startsWith("seven")) return "7";
        if (s.startsWith("eight")) return "8";
        if (s.startsWith("nine")) return "9";

        throw new RuntimeException("no digit found");
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
