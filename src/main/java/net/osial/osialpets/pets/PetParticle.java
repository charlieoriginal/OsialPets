package net.osial.osialpets.pets;

import lombok.Getter;
import org.bukkit.Particle;

@Getter
public class PetParticle {

    private double offsetY;
    private int count;
    private double randomOffsetXMax;
    private double randomOffsetZMax;
    private double randomOffsetYMax;
    private Particle type;

    public PetParticle(double offsetY, int count, double randomOffsetXMax, double randomOffsetZMax, double randomOffsetYMax, String type) {
        this.offsetY = offsetY;
        this.count = count;
        this.randomOffsetXMax = randomOffsetXMax;
        this.randomOffsetZMax = randomOffsetZMax;
        this.randomOffsetYMax = randomOffsetYMax;
        this.type = Particle.valueOf(type);
    }

}
