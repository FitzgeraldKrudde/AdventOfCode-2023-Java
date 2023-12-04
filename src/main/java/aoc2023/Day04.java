package aoc2023;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day04 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        ScratchCards cards = ScratchCards.of(inputRaw);

        long result = cards.sumPoints();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        ScratchCards cards = ScratchCards.of(inputRaw);

        cards.winCards();
        long result = cards.sumCards();

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

record Card(int cardNr, List<Integer> winningNumbers, List<Integer> myNumbers) {
    static Card of(String line) {
        String[] cardParts = line.split(":");
        int cardNr = Integer.parseInt(cardParts[0].split("\\s+")[1]);
        String[] listNumbers = cardParts[1].split("\\|");
        List<Integer> winningNumbers = Arrays.stream(listNumbers[0].trim().split("\\s+"))
                .map(Integer::parseInt)
                .toList();
        List<Integer> myNumbers = Arrays.stream(listNumbers[1].trim().split("\\s+"))
                .map(Integer::parseInt)
                .toList();

        return new Card(cardNr, winningNumbers, myNumbers);
    }

    public long points() {
        long count = countWinningNumbers();

        if (count == 0) {
            return 0;
        } else {
            return (long) Math.pow(2, count - 1);
        }
    }

    public long countWinningNumbers() {
        return myNumbers.stream()
                .filter(winningNumbers::contains)
                .count();
    }
}

record ScratchCards(List<Card> cards, Map<Integer, Long> cardOccurrences) {
    static ScratchCards of(List<String> lines) {
        List<Card> cards = lines.stream()
                .map(Card::of)
                .toList();

        Map<Integer, Long> cardOccurrences = new HashMap<>();
        cards.forEach(card -> cardOccurrences.put(card.cardNr(), 1L));

        return new ScratchCards(cards, cardOccurrences);
    }

    public long sumPoints() {
        return cards.stream()
                .map(Card::points)
                .reduce(0L, Long::sum);
    }

    public long sumCards() {
        return cards.stream()
                .map(card -> cardOccurrences.get(card.cardNr()))
                .reduce(0L, Long::sum);
    }

    public void winCards() {
        cards.forEach(card -> doWinCards(card, card.countWinningNumbers()));
    }

    private void doWinCards(Card card, long nrCards) {
        for (int cardNr = card.cardNr() + 1; cardNr <= card.cardNr() + nrCards; cardNr++) {
            cardOccurrences.compute(cardNr, (k, v) -> v + cardOccurrences.get(card.cardNr()));
        }
    }
}
