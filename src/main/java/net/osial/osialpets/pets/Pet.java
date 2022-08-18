package net.osial.osialpets.pets;

import lombok.Getter;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.api.events.PetActivateEvent;
import net.osial.osialpets.pets.sub.ProcEffect;
import net.osial.osialpets.pets.sub.TriggerCondition;
import net.osial.osialpets.pets.sub.TriggerConditionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Pet {

    private String id;
    private String base64;
    private String renderedName;
    private List<ProcEffect> procEffects;
    private List<TriggerCondition> triggerConditions;
    private HashMap<Long, PetLevel> levels;
    private ItemStack giveItem;
    private PetParticle petParticle;
    private String prettyName;

    public Pet(String id, String base64, String renderedName, PetParticle petParticle, String prettyName, ItemStack giveItem) {
        this.id = id;
        this.base64 = base64;
        this.renderedName = renderedName;
        this.prettyName = prettyName;
        this.giveItem = giveItem;
        this.petParticle = petParticle;
        this.levels = new HashMap<>();
        this.procEffects = new ArrayList<>();
        this.triggerConditions = new ArrayList<>();
    }

    public void addProcEffect(ProcEffect procEffect) {
        this.procEffects.add(procEffect);
    }

    public void executeProcEffects(Player player) {
        PetsProfile profile = JavaPlugin.getPlugin(OsialPets.class).getProfileManager().findByUUID(player.getUniqueId());
        PetActivateEvent event = new PetActivateEvent(this, profile);
        Bukkit.getPluginManager().callEvent(event);
        for (ProcEffect procEffect : procEffects) {
            double chance = procEffect.getChance();
            if (chance > 0 && chance <= 100) {
                if (Math.random() * 100 < chance) {
                    procEffect.execute(player);
                }
            } else {
                procEffect.execute(player);
            }
        }
    }

    public void addLevel(long level, PetLevel value) {
        this.levels.put(level, value);
    }

    public void addTriggerCondition(TriggerCondition triggerCondition) {
        this.triggerConditions.add(triggerCondition);
    }

    public void triggerCheck(TriggerConditionType type, String value, Player player, long petLevel) {
        try {
            UUID uuid = player.getUniqueId();
            PetsProfile profile = JavaPlugin.getPlugin(OsialPets.class).getProfileManager().findByUUID(uuid);
            if (profile != null) {
                PetInstance currentInstance = profile.getOwnedPets().stream().filter(petInstance -> petInstance.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
                if (currentInstance != null) {
                    currentInstance.incrementCounter(player);
                }
            }
        } catch (Exception e) {}

        for (TriggerCondition triggerCondition : triggerConditions) {
            if (triggerCondition.getType() == type && triggerCondition.getValue().equals(value)) {
                if (triggerCondition.chanceCheck(this, petLevel)) {
                    executeProcEffects(player);
                }
            }
        }
    }

    /*
    Debug function, used for testing configuration had loaded important data correctly.
     */
    public void printDetails() {
        Bukkit.getLogger().info("============================================================");
        Bukkit.getLogger().info("Pet id: " + id);
        Bukkit.getLogger().info("Pet base64: " + base64);
        for (ProcEffect procEffect : procEffects) {
            Bukkit.getLogger().info("ProcEffect type: " + procEffect.getType());
            Bukkit.getLogger().info("ProcEffect value: " + procEffect.getValue());
        }
        for (TriggerCondition triggerCondition : triggerConditions) {
            Bukkit.getLogger().info("TriggerCondition type: " + triggerCondition.getType());
            Bukkit.getLogger().info("TriggerCondition value: " + triggerCondition.getValue());
            Bukkit.getLogger().info("TriggerCondition chance: " + triggerCondition.getChance());
        }
        Bukkit.getLogger().info("============================================================");
    }

}
