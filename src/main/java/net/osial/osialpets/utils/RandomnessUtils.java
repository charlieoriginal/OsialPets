package net.osial.osialpets.utils;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomnessUtils {
    public static <T> T getRandom(Map<T, Double> chances) {
        double chance = ThreadLocalRandom.current().nextDouble() * chances.values().stream().reduce(0D, Double::sum);
        AtomicDouble needle = new AtomicDouble();
        return chances.entrySet().stream().filter((ent) -> {
            return needle.addAndGet(ent.getValue()) >= chance;
        }).findFirst().map(Map.Entry::getKey).orElse(null);
    }
}
