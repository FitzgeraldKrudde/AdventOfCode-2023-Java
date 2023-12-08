package aoc2023;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

enum HandType {
    HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
}

record Hand(HandType type, String cards) {
    public final static String CARD_STRENGTH = new StringBuilder("AKQJT98765432").reverse().toString();

    public static Hand of(String s) {
        HandType handType = getHandType(s);

        return new Hand(handType, s);
    }

    public static Hand ofWithJokers(String cards) {
        List<String> jokersReplaced = replaceJokers(cards, 0);

        String bestCards = jokersReplaced.stream()
                .max(Comparator.comparingInt(hand -> getHandType(hand).ordinal()))
                .orElseThrow(() -> new IllegalStateException("no best card found"));

        return new Hand(getHandType(bestCards), cards);
    }

    private static List<String> replaceJokers(String cards, int startPos) {
        if (startPos >= cards.length()) {
            return List.of(cards);
        } else if (cards.charAt(startPos) == 'J') {
            return CARD_STRENGTH.chars()
                    .mapToObj(e -> cards.substring(0, startPos) + (char) e + cards.substring(startPos + 1))
                    .flatMap(cardsWithOneJokerReplaced -> replaceJokers(cardsWithOneJokerReplaced, startPos + 1).stream())
                    .toList();
        } else {
            return replaceJokers(cards, startPos + 1);
        }
    }

    private static HandType getHandType(String cards) {
        Map<Character, Long> map = cards.chars()
                .mapToObj(e -> (char) e)
                .collect(groupingBy(identity(),
                        counting()));

        return switch (map.keySet().size()) {
            case 1 -> HandType.FIVE_OF_A_KIND;
            case 2 -> map.containsValue(4L) ? HandType.FOUR_OF_A_KIND : HandType.FULL_HOUSE;
            case 3 -> map.containsValue(3L) ? HandType.THREE_OF_A_KIND : HandType.TWO_PAIR;
            case 4 -> HandType.ONE_PAIR;
            case 5 -> HandType.HIGH_CARD;
            default -> throw new IllegalStateException("Unexpected value: " + map.keySet().size());
        };
    }

    public int cardStrength(boolean withJokers, int i) {
        if (withJokers && cards.charAt(i) == 'J') {
            return -1;
        } else {
            return CARD_STRENGTH.indexOf(cards.charAt(i));
        }
    }
}

record HandWithBid(Hand hand, long bid) {
    public static HandWithBid of(String line) {
        String[] split = line.trim().split("\\s+");
        return new HandWithBid(Hand.of(split[0]), Long.parseLong(split[1]));
    }
}

record CamelCard(List<HandWithBid> handWithBids) {
    public static CamelCard of(List<String> lines) {
        return new CamelCard(lines.stream()
                .map(HandWithBid::of)
                .collect(toList()));
    }

    public CamelCard useJokers() {
        return new CamelCard(handWithBids.stream()
                .map(handWithBid -> new HandWithBid(Hand.ofWithJokers(handWithBid.hand().cards()), handWithBid.bid()))
                .toList());
    }

    public long totalWinnings() {
        return totalWinnings(false);
    }

    public long totalWinningsWithJokers() {
        return totalWinnings(true);
    }

    public long totalWinnings(boolean withJokers) {
        AtomicLong rank = new AtomicLong(0L);

        return handWithBids.stream()
                .sorted(Comparator.<HandWithBid, HandType>comparing(handWithBid -> handWithBid.hand().type())
                        .thenComparing(handWithBid -> handWithBid.hand().cardStrength(withJokers, 0))
                        .thenComparing(handWithBid -> handWithBid.hand().cardStrength(withJokers, 1))
                        .thenComparing(handWithBid -> handWithBid.hand().cardStrength(withJokers, 2))
                        .thenComparing(handWithBid -> handWithBid.hand().cardStrength(withJokers, 3))
                        .thenComparing(handWithBid -> handWithBid.hand().cardStrength(withJokers, 4))
                )
                .map(handWithBid -> {
                    rank.getAndIncrement();
                    return handWithBid.bid() * rank.get();
                })
                .reduce(0L, Long::sum);
    }
}

public class Day07 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        CamelCard camelCard = CamelCard.of(inputRaw);

        long result = camelCard.totalWinnings();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        CamelCard camelCard = CamelCard.of(inputRaw);
        camelCard = camelCard.useJokers();

        long result = camelCard.totalWinningsWithJokers();

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
