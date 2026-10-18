package com.gathin.tvhome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Pure helpers for the dock order: parse/serialize the prefs value, move an entry, resolve the
 * effective favourites list (prefs first, config.xml fallback). No Android imports: unit-testable.
 */
final class DockOrder {
    private DockOrder() {
    }

    /** Prefs value → ordered packages. Blank/malformed input yields an empty list. */
    static List<String> parse(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null) return out;
        for (String line : raw.split("\n")) {
            String p = line.trim();
            if (!p.isEmpty() && !out.contains(p)) out.add(p);   // dedupe, keep first position
        }
        return out;
    }

    static String serialize(List<String> packages) {
        StringBuilder sb = new StringBuilder();
        for (String p : packages) {
            if (p == null || p.trim().isEmpty()) continue;
            if (sb.length() > 0) sb.append('\n');
            sb.append(p.trim());
        }
        return sb.toString();
    }

    /** Move entry at {@code from} so it lands at index {@code to} (insert semantics, stable rest). */
    static List<String> move(List<String> in, int from, int to) {
        List<String> out = new ArrayList<>(in);
        if (from < 0 || from >= out.size() || to < 0 || to >= out.size() || from == to) return out;
        String item = out.remove(from);
        out.add(to, item);
        return out;
    }

    /** Effective dock order: prefs order when set, else config.xml array; deduped, blanks dropped. */
    static List<String> resolve(List<String> prefs, String[] config) {
        List<String> source = (prefs != null && !prefs.isEmpty()) ? prefs : Arrays.asList(config);
        return new ArrayList<>(new LinkedHashSet<>(source));
    }
}
