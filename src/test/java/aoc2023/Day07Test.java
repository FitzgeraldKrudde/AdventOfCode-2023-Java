package aoc2023;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Day07Test {

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("6440");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("5905");
    }

    // @formatter:off
    private String getInputFilename() {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        return clazz.getSimpleName().toLowerCase().replace("test","").replace("day0", "day") + ".txt";
        // @formatter:on
    }

    private Day getDay() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // get our Test class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // get the classname of the class under test
        final String fullClassName = clazz.getCanonicalName().replace("Test","");

        // create instance
        return (Day) Class.forName(fullClassName).getDeclaredConstructor().newInstance();
    }
    // @formatter:on
}
