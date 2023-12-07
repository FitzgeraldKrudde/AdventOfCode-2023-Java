package aoc2023;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Optional<RangeMapper> findRangeMapper(long source) {
        return rangeMappers.stream()
                .filter(rangeMapper -> source >= rangeMapper.sourceRangeStart())
                .filter(rangeMapper -> source < rangeMapper.sourceRangeStart() + rangeMapper.rangeLength())
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

    public long getLowestLocationForSeedRanges() {
        List<SeedRange> seedRanges = new ArrayList<>();

        for (int i = 0; i < seeds.size(); i += 2) {
            seedRanges.add(new SeedRange(seeds.get(i), seeds.get(i + 1)));
        }

        CategoryMapper categoryMapper = findMapperForSourceCategory(Category.SEED);

        while (categoryMapper.categoryDestination() != Category.LOCATION) {
            seedRanges = destinationSeedRanges(categoryMapper, seedRanges);
            categoryMapper = findMapperForSourceCategory(categoryMapper.categoryDestination());
        }

        seedRanges = destinationSeedRanges(categoryMapper, seedRanges);

        return seedRanges.stream()
                .map(SeedRange::start)
                .reduce(Long.MAX_VALUE, Math::min);
    }

    private List<SeedRange> destinationSeedRanges(CategoryMapper categoryMapper, List<SeedRange> seedRanges) {
        return seedRanges.stream()
                .flatMap(seedRange -> destinationRanges(categoryMapper, seedRange))
                .collect(Collectors.toList());
    }

    private Stream<SeedRange> destinationRanges(CategoryMapper categoryMapper, SeedRange seedRange) {
        // split the seed range into multiple parts based on which parts are mapped by a range mapper

        if (seedRange.length() == 0) {
            return Stream.empty();
        }

        // find mapper for the start of the seed range
        Optional<RangeMapper> optionalRangeMapper = categoryMapper.findRangeMapper(seedRange.start());
        if (optionalRangeMapper.isPresent()) {
            RangeMapper rangeMapper = optionalRangeMapper.get();

            long destinationSubRangeStart = rangeMapper.findDestination(seedRange.start());
            long destinationSubRangeLength = Math.min(seedRange.length(), rangeMapper.sourceRangeStart() + rangeMapper.rangeLength() - seedRange.start() - 1);

            SeedRange destinationSubRange = new SeedRange(destinationSubRangeStart, destinationSubRangeLength);
            SeedRange remainingSeedRange = new SeedRange(seedRange.start() + destinationSubRangeLength + 1, seedRange.length() - destinationSubRangeLength);

            return Stream.concat(Stream.of(destinationSubRange), destinationRanges(categoryMapper, remainingSeedRange));
        } else {
            // find next rangemapper AFTER our seedrange start
            Optional<RangeMapper> optionalNextRangeMapper = categoryMapper.rangeMappers().stream()
                    .filter(rangeMapper -> rangeMapper.sourceRangeStart() > seedRange.start())
                    .min((rm1, rm2) -> Math.toIntExact(rm1.sourceRangeStart() - rm2.sourceRangeStart()));

            if (optionalNextRangeMapper.isEmpty()) {
                return Stream.of(seedRange);
            } else {
                RangeMapper nextRangeMapper = optionalNextRangeMapper.get();

                long destinationSubRangeStart = seedRange.start();
                long destinationSubRangeLength = (Math.min(seedRange.start() + seedRange.length(), nextRangeMapper.sourceRangeStart() - 1)) - seedRange.start();

                SeedRange destinationSubRange = new SeedRange(destinationSubRangeStart, destinationSubRangeLength);
                SeedRange remainingSeedRange = new SeedRange(seedRange.start() + destinationSubRangeLength + 1, seedRange.length() - destinationSubRangeLength);

                return Stream.concat(Stream.of(destinationSubRange), destinationRanges(categoryMapper, remainingSeedRange));
            }
        }
    }
}

record SeedRange(long start, long length) {

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
        Almanac almanac = Almanac.of(inputRaw);

        long locationForSeed = almanac.getLowestLocationForSeedRanges();

        return String.valueOf(locationForSeed);
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
