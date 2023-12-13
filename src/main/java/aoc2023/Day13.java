package aoc2023;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

record PointDay13(int x, int y) {

}

record Pattern(List<PointDay13> mirrors, int maxX, int maxY) {
    public final static char MIRROR = '#';

    public static Pattern of(List<String> lines) {
        List<PointDay13> mirrors = new ArrayList<>();
        int maxX = 0;
        int maxY = 0;

        for (int y = 1; y <= lines.size(); y++) {
            String line = lines.get(y - 1);
            for (int x = 1; x <= line.length(); x++) {
                maxX = x;
                if (line.charAt(x - 1) == MIRROR) {
                    mirrors.add(new PointDay13(x, y));
                }
            }
            maxY = y;
        }

        return new Pattern(mirrors, maxX, maxY);
    }

    public long reflection() {
        return 100L * horizontalReflection() + verticalReflection();
    }

    public long verticalReflection() {
        return verticalReflectionSkipExistingReflection(0L);
    }

    public long verticalReflectionSkipExistingReflection(long existingReflection) {
        boolean mirrorring = true;

        for (int reflectionAfterX = 1; reflectionAfterX < maxX; reflectionAfterX++) {
            if (reflectionAfterX == existingReflection) {
                continue;
            }

            mirrorring = true;
            for (int x = 1; x <= reflectionAfterX && mirrorring; x++) {
                for (int y = 1; y <= maxY && mirrorring; y++) {
                    PointDay13 firstPoint = new PointDay13(x, y);
                    int mirrorPointX = mirrorPoint(reflectionAfterX, x);
                    if (mirrorPointX > maxX) {
                        continue;
                    }
                    PointDay13 mirrorPoint = new PointDay13(mirrorPointX, y);
                    if (notMirrored(firstPoint, mirrorPoint)) {
                        mirrorring = false;
                    }
                }
            }

            if (mirrorring) {
                return reflectionAfterX;
            }
        }

        if (!mirrorring) {
            return 0;
        } else {
            throw new IllegalStateException("unexpected..");
        }
    }

    private boolean notMirrored(PointDay13 firstPoint, PointDay13 mirrorPoint) {
        return (mirrors.contains(firstPoint) && !mirrors.contains(mirrorPoint)) ||
               (!mirrors.contains(firstPoint) && mirrors.contains(mirrorPoint));
    }

    public long horizontalReflection() {
        return horizontalReflectionSkipExistingReflection(0L);
    }

    public long horizontalReflectionSkipExistingReflection(long existingReflection) {
        boolean mirrorring = true;

        for (int reflectionBelowY = 1; reflectionBelowY < maxY; reflectionBelowY++) {
            if (reflectionBelowY == existingReflection) {
                continue;
            }

            mirrorring = true;
            for (int y = 1; y <= reflectionBelowY && mirrorring; y++) {
                for (int x = 1; x <= maxX && mirrorring; x++) {
                    PointDay13 firstPoint = new PointDay13(x, y);
                    int mirrorPointY = mirrorPoint(reflectionBelowY, y);
                    if (mirrorPointY > maxY) {
                        continue;
                    }
                    PointDay13 mirrorPoint = new PointDay13(x, mirrorPointY);
                    if (notMirrored(firstPoint, mirrorPoint)) {
                        mirrorring = false;
                    }
                }
            }

            if (mirrorring) {
                return reflectionBelowY;
            }
        }

        if (!mirrorring) {
            return 0;
        } else {
            throw new IllegalStateException("unexpected..");
        }
    }

    private static int mirrorPoint(int reflectionAfter, int coordinate) {
        return 2 * reflectionAfter + 1 - coordinate;
    }

    public long reflectionWithFixedSmudge() {
        long horizontalReflection = horizontalReflection();
        long verticalReflection = verticalReflection();

        for (int x = 1; x <= maxX; x++) {
            for (int y = 1; y <= maxY; y++) {
                Pattern modifiedMirrors = flipMirror(x, y);

                long newHorizontalReflection = modifiedMirrors.horizontalReflectionSkipExistingReflection(horizontalReflection);
                long newVerticalReflection = modifiedMirrors.verticalReflectionSkipExistingReflection(verticalReflection);

                if (newHorizontalReflection != 0 || newVerticalReflection != 0) {
                    return 100L * newHorizontalReflection + newVerticalReflection;
                }
            }
        }

        throw new IllegalStateException("no reflection with smudge found");
    }

    private Pattern flipMirror(int x, int y) {
        List<PointDay13> newMirrors = new ArrayList<>(mirrors);

        PointDay13 smudgePoint = new PointDay13(x, y);
        if (mirrors.contains(smudgePoint)) {
            newMirrors.remove(smudgePoint);
        } else {
            newMirrors.add(smudgePoint);
        }

        return new Pattern(newMirrors, maxX, maxY);
    }
}

record Valley(List<Pattern> pattern) {
    public static Valley of(List<String> lines) {
        List<Pattern> patterns = new ArrayList<>();
        List<String> patternLines = new ArrayList<>();

        lines.forEach(s -> addLine(patterns, patternLines, s));
        patterns.add(Pattern.of(patternLines));

        return new Valley(patterns);
    }

    private static void addLine(List<Pattern> patterns, List<String> patternLines, String line) {
        if (StringUtils.isBlank(line)) {
            patterns.add(Pattern.of(patternLines));
            patternLines.clear();
        } else {
            patternLines.add(line);
        }
    }
}

public class Day13 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Valley valley = Valley.of(inputRaw);

        long result = valley.pattern().stream()
                .map(Pattern::reflection)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Valley valley = Valley.of(inputRaw);

        long result = valley.pattern().stream()
                .map(Pattern::reflectionWithFixedSmudge)
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
