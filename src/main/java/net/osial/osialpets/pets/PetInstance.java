package net.osial.osialpets.pets;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.utils.ColorUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class PetInstance {

    private UUID uuid;
    private String petId;
    private long level;
    private long counter;
    private String epicPiracy = "%%__NONCE__%%";

    public PetInstance(UUID uuid, String pet_id, long level, long counter) {
        this.uuid = uuid;
        this.petId = pet_id;
        this.level = level;
        this.counter = counter;
    }

    public PetInstance(String pet_id, long level, long counter) {
        this.petId = pet_id;
        this.level = level;
        this.counter = counter;
        this.uuid = UUID.randomUUID();
    }

    private void checkLevelup(Player player) {
        OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
        UUID uid = player.getUniqueId();
        PetsProfile profile = plugin.getProfileManager().findByUUID(uid);
        Pet pet = plugin.getProfileManager().getPets().stream().filter(p -> p.getId().equals(petId)).findFirst().orElse(null);

        if (pet == null) {
            return;
        }
        if (pet.getLevels().size() == level) {
            return;
        }

        if (profile != null) {
            Pet current = profile.getActive();
            PetLevel currentLevel = current.getLevels().get(level);
            if (currentLevel != null) {
                if (counter >= currentLevel.getCounterRequirement()) {
                    counter = 0;
                    level++;
                    String newName = current.getRenderedName();
                    newName = newName.replace("%level%", String.valueOf(level));
                    newName = newName.replace("%player%", player.getName());
                    newName = ColorUtil.color(newName);
                    profile.getPetRender().getPetEntity().setCustomName(newName);

                    boolean useTitle = plugin.getConfig().getBoolean("level_up.use_title");
                    boolean playSound = plugin.getConfig().getBoolean("level_up.play_sound");
                    boolean sendMessage = plugin.getConfig().getBoolean("level_up.send_message");
                    String message = plugin.getConfig().getString("level_up.message");
                    String title = plugin.getConfig().getString("level_up.title");
                    title = title.replace("%level%", String.valueOf(level));
                    String subtitle = plugin.getConfig().getString("level_up.subtitle");
                    subtitle = subtitle.replace("%level%", String.valueOf(level));
                    String sound = plugin.getConfig().getString("level_up.sound");
                    Sound snd = Sound.valueOf(sound);
                    if (useTitle) {
                        player.sendTitle(ColorUtil.color(title), ColorUtil.color(subtitle), 10, 40, 10);
                    }
                    if (playSound) {
                        player.playSound(player.getLocation(), snd, 1, 1);
                    }
                    if (sendMessage) {
                        message = message.replace("%level%", String.valueOf(level));
                        message = message.replace("%max_level%", String.valueOf(current.getLevels().size()));
                        player.sendMessage(ColorUtil.color(message));
                    }

                    profile.saveData();
                }
            }
        }
    }

    public void incrementCounter(Player player) {
        this.counter++;
        checkLevelup(player);
    }

}
