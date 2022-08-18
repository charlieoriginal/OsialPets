package net.osial.osialpets.pets.sub;

import lombok.Getter;
import net.osial.osialpets.pets.Pet;

@Getter
public class TriggerCondition {

        private TriggerConditionType type;
        private String value;
        private double chance;

        public TriggerCondition(TriggerConditionType type, String value, double chance) {
            this.type = type;
            this.value = value;
            this.chance = chance;
        }

        public boolean chanceCheck(Pet pet, long level) {
            double compare = Math.random() * 100;
            double addition = pet.getLevels().get(level).getChanceAddition();
            return compare <= (chance+addition);
        }

}
