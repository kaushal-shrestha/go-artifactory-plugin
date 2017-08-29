package com.google.copy;

import static java.util.Arrays.asList;
import static org.truth0.Truth.ASSERT;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class GuavaTests {
    @Test
    public void shouldSplitAStringBasedOnDelimiterAndNumberOfTimes() {
        String testString = "a/b/c.txt";
        List<String> splits = Splitter.on("/").omitEmptyStrings().limit(2).splitToList(testString);

        ASSERT.that(splits).has().exactlyAs(asList("a", "b/c.txt"));
    }

    @Test
    public void shouldStringifyIterables() {
        List<String> strings = Arrays.asList("a", "b", "c");
        ASSERT.that(Iterables.toString(strings)).isEqualTo("[a, b, c]");
    }

    @Test
    public void shouldPerformSplitsMultipleTimes() {
        String toBeSplit = "a=b\n\nc=d\n\n\n";
        Map<String, String> split = Splitter.on("\n").omitEmptyStrings().withKeyValueSeparator("=").split(toBeSplit);

        ASSERT.that(split.size()).is(2);
        ASSERT.that(split).hasKey("a").withValue("b");
        ASSERT.that(split).hasKey("c").withValue("d");
    }
}

