package net.osial.osialpets.pets;

import lombok.Getter;

@Getter
public class PetLevel {

    private long level;
    private long counterRequirement;
    private double chanceAddition;

    public PetLevel(long level, long counterRequirement, double chanceAddition) {
        this.level = level;
        this.counterRequirement = counterRequirement;
        this.chanceAddition = chanceAddition;
    }

}
