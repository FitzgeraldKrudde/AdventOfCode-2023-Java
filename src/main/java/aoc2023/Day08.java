package aoc2023;

import java.util.List;

record Node(String name, String left, String right) {
    public static Node of(String line) {
        line = line
                .replaceAll("=", "")
                .replaceAll(",", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
        ;

        String[] split = line.trim().split("\\s+");
        return new Node(split[0], split[1], split[2]);
    }
}

record Network(String instructions, List<Node> nodes) {
    public static Network of(List<String> lines) {
        String instructions = lines.get(0).trim();
        List<Node> nodes = lines.stream()
                .skip(2)
                .map(Node::of)
                .toList();

        return new Network(instructions, nodes);
    }

    public long nrStepsToZZZ() {
        long nrSteps = 0;
        Node currentNode = findNode("AAA");

        while (!currentNode.name().equals("ZZZ")) {
            currentNode = nextNode(currentNode, nrSteps);
            nrSteps++;
        }

        return nrSteps;
    }

    private Node findNode(String targetNode) {
        return nodes.stream()
                .filter(node -> node.name().equals(targetNode))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("node not found"));
    }

    public long nrStepsToGhostedZ() {
        List<Node> startNodes = findNodesEndingWithA();

        List<Long> listNrStepsToEndingNode = startNodes.stream()
                .map(this::nrStepsToNodeEndingWithZ)
                .toList();

        return leastCommonMultiple(listNrStepsToEndingNode);
    }

    private long leastCommonMultiple(List<Long> listNrStepsToEndingNode) {
        long lcm = 1;

        for (Long l : listNrStepsToEndingNode) {
            lcm = lcm(lcm, l);
        }

        return lcm;
    }

    public long lcm(long l1, long l2) {
        return l1 * l2 / gcd(l1, l2);
    }

    public long gcd(long l1, long l2) {
        if (l1 == 0) {
            return l2;
        }
        if (l2 == 0) {
            return l1;
        }

        long min = Math.min(l1, l2);
        return gcd(Math.max(l1, l2) % min, min);
    }

    private long nrStepsToNodeEndingWithZ(Node node) {
        long step = 0;

        while (!nodeEndsWithZ(node)) {
            node = nextNode(node, step++);
        }

        return step;
    }

    private Node nextNode(Node currentNode, long steps) {
        char i = instructions.charAt((int) (steps % instructions.length()));
        return findNode(i == 'L' ? currentNode.left() : currentNode.right());
    }

    private boolean nodeEndsWithZ(Node node) {
        return node.name().charAt(node.name().length() - 1) == 'Z';
    }

    private List<Node> findNodesEndingWithA() {
        return nodes.stream()
                .filter(node -> node.name().charAt(node.name().length() - 1) == 'A')
                .toList();
    }
}

public class Day08 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Network network = Network.of(inputRaw);

        long result = network.nrStepsToZZZ();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Network network = Network.of(inputRaw);

        long result = network.nrStepsToGhostedZ();

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
