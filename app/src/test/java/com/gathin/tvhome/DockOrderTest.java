package com.gathin.tvhome;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DockOrderTest {

    @Test
    public void move_reorders_list() {
        List<String> in = Arrays.asList("a", "b", "c", "d");
        assertEquals(Arrays.asList("b", "a", "c", "d"), DockOrder.move(in, 0, 1));
        assertEquals(Arrays.asList("d", "a", "b", "c"), DockOrder.move(in, 3, 0));
        assertEquals(Arrays.asList("a", "b", "c", "d"), DockOrder.move(in, 2, 2));
    }

    @Test
    public void move_out_of_range_is_noop() {
        List<String> in = Arrays.asList("a", "b");
        assertEquals(in, DockOrder.move(in, -1, 0));
        assertEquals(in, DockOrder.move(in, 0, 5));
        assertEquals(in, DockOrder.move(in, 5, 0));
    }

    @Test
    public void parse_ignores_blank_and_dupes() {
        assertEquals(Arrays.asList("a", "b"), DockOrder.parse("a\n\n b \na"));
        assertTrue(DockOrder.parse(null).isEmpty());
        assertTrue(DockOrder.parse("  ").isEmpty());
    }

    @Test
    public void serialize_roundtrip() {
        List<String> in = Arrays.asList("com.a", "com.b", "com.c");
        assertEquals(in, DockOrder.parse(DockOrder.serialize(in)));
    }

    @Test
    public void resolve_prefers_prefs_and_dedupes() {
        List<String> prefs = Arrays.asList("p1", "p2", "p1");
        String[] config = {"c1", "c2"};
        assertEquals(Arrays.asList("p1", "p2"), DockOrder.resolve(prefs, config));
    }

    @Test
    public void resolve_falls_back_to_config() {
        String[] config = {"c1", "c2"};
        assertEquals(Arrays.asList("c1", "c2"), DockOrder.resolve(new ArrayList<>(), config));
        assertEquals(Arrays.asList("c1", "c2"), DockOrder.resolve(null, config));
    }
}
