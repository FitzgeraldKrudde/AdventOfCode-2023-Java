package aoc2023;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

enum Category {
    SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION
}

record RangeMapper(long sourceRangeStart, long destinationRangeStart, long rangeLength) {
    public long findDestination(long source) {
        return destinationRangeStart() + source - sourceRangeStart;
    }
}

record CategoryMapper(Category categorySource, Category categoryDestination, List<RangeMapper> rangeMappers) {
    public long findDestination(long source) {
        Optional<RangeMapper> optionalRangeMapper = findRangeMapper(source);

        return optionalRangeMapper.map(rangeMapper -> rangeMapper.findDestination(source)).orElse(source);
    }

    private Optional<RangeMapper> findRangeMapper(double source) {
        return rangeMappers.stream()
                .filter(rangeMapper -> source >= rangeMapper.sourceRangeStart())
                .filter(rangeMapper -> source <= rangeMapper.sourceRangeStart() + rangeMapper.rangeLength())
                .findFirst();
    }
}

record Almanac(List<Long> seeds, List<CategoryMapper> categoryMappers) {
    public static Almanac of(List<String> lines) {
        List<Long> seeds = Arrays.stream(lines.get(0).split(":")[1].trim().split("\\s+"))
                .map(Long::valueOf)
                .toList();

        List<CategoryMapper> categoryMappers = new ArrayList<>();

        // sort of a hack to use state in the stream
        AtomicReference<CategoryMapper> currentMapperAtomicReference = new AtomicReference<>();

        lines.stream()
                .skip(2)
                .filter(StringUtils::isNotBlank)
                .forEach(line -> addLineToCategoryMappers(categoryMappers, currentMapperAtomicReference, line));
        categoryMappers.add(currentMapperAtomicReference.get());

        return new Almanac(seeds, categoryMappers);
    }

    public long getLowestLocation() {
        return seeds.stream()
                .map(this::getLocationForSeed)
                .min(Long::compareTo)
                .orElseThrow(() -> new IllegalStateException("no minimum location found"));
    }

    private Long getLocationForSeed(long seed) {
        long destination = seed;
        CategoryMapper categoryMapper = findMapperForSourceCategory(Category.SEED);

        while (categoryMapper.categoryDestination() != Category.LOCATION) {
            destination = categoryMapper.findDestination(destination);
            categoryMapper = findMapperForSourceCategory(categoryMapper.categoryDestination());
        }

        return categoryMapper.findDestination(destination);
    }

    private CategoryMapper findMapperForSourceCategory(Category source) {
        return categoryMappers.stream()
                .filter(categoryMapper -> categoryMapper.categorySource().equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no mapper found"));
    }

    private static void addLineToCategoryMappers(List<CategoryMapper> categoryMappers, AtomicReference<CategoryMapper> currentMapper, String line) {
        if (line.contains("map")) {
            String[] split = line.split("\\s+")[0].split("-");
            currentMapper.set(new CategoryMapper(Category.valueOf(split[0].toUpperCase()), Category.valueOf(split[2].toUpperCase()), new ArrayList<>()));
            categoryMappers.add(currentMapper.get());
        } else {
            List<Long> rangeParams = Arrays.stream(line.split("\\s+")).map(Long::valueOf).toList();
            currentMapper.get().rangeMappers().add(new RangeMapper(rangeParams.get(1), rangeParams.get(0), rangeParams.get(2)));
        }
    }

}

public class Day05 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Almanac almanac = Almanac.of(inputRaw);
        long locationForSeed = almanac.getLowestLocation();

        return String.valueOf(locationForSeed);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
//        List<Long> input = parseInput(inputRaw);


        long result = 0;

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
