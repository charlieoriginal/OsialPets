package net.osial.osialpets.pets.sub;

import lombok.Getter;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.pets.PetsProfile;
import net.osial.osialpets.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@Getter
public class ProcEffect {

    private ProcEffectType type;
    private String value;
    private double chance;

    public ProcEffect(ProcEffectType type, String value, double chance) {
        this.type = type;
        this.value = value;
        this.chance = chance;
    }

    public void execute(Player player) {
        switch (type) {
            case CONSOLE_COMMAND:
                value = value.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
                break;
            case MESSAGE:
                OsialPets pets = JavaPlugin.getPlugin(OsialPets.class);
                UUID uuid = player.getUniqueId();
                PetsProfile profile = pets.getProfileManager().findByUUID(uuid);
                if (profile != null) {
                    value = value.replace("%pet_id%", profile.getActive().getId());
                    value = value.replace("%player%", player.getName());
                    value = ColorUtil.color(value);
                    player.sendMessage(value);
                }
                break;
            case ADD_POTION_EFFECT:
                //Potions are in the format of POTION_EFFECT_ID:LEVEL:DURATION;POTION_EFFECT_ID:LEVEL:DURATION;...
                String[] potions = value.split(";");
                for (String s : potions) {
                    System.out.println(s);
                    String[] potion = s.split(":");
                    if (potion.length == 3) {
                        System.out.println(potion[0] + " " + potion[1] + " " + potion[2]);
                        System.out.println(PotionEffectType.getByName(potion[0]));
                        player.addPotionEffect(
                                new PotionEffect(
                                        PotionEffectType.getByName(potion[0]),
                                        Integer.parseInt(potion[2]),
                                        Integer.parseInt(potion[1])
                                )
                        );
                    }
                }
            case REMOVE_POTION_EFFECTS:
                //Effects are in the format of POTION_ID;POTION_ID;...
                String[] effects = value.split(";");
                for (String s : effects) {
                    PotionEffectType effect = PotionEffectType.getByName(s);
                    if (effect != null) {
                        player.removePotionEffect(effect);
                    }
                }
        }
    }

}
