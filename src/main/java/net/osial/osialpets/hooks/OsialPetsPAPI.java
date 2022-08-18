package net.osial.osialpets.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetInstance;
import net.osial.osialpets.pets.PetsProfile;
import net.osial.osialpets.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OsialPetsPAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "osialpets";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Osial";
    }

    @Override
    public @NotNull String getVersion() {
        return JavaPlugin.getPlugin(OsialPets.class).getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("version")) {
            return getVersion();
        }

        else if (params.equalsIgnoreCase("pet_id")) {
            OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
            UUID uuid = player.getUniqueId();
            PetsProfile profile = plugin.getProfileManager().findByUUID(uuid);
            if (profile != null) {
                return profile.getActive().getId();
            } else {
                return "No pet equipped";
            }
        }

        else if (params.equalsIgnoreCase("pet_level")) {
            OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
            UUID uuid = player.getUniqueId();
            PetsProfile profile = plugin.getProfileManager().findByUUID(uuid);
            UUID active = profile.getActiveInstance();
            PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(active)).findFirst().orElse(null);
            if (instance != null) {
                return instance.getLevel() + "";
            } else {
                return "No pet equipped";
            }
        }

        else if (params.equalsIgnoreCase("current_counter")) {
            OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
            UUID uuid = player.getUniqueId();
            PetsProfile profile = plugin.getProfileManager().findByUUID(uuid);
            UUID active = profile.getActiveInstance();
            PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(active)).findFirst().orElse(null);
            if (instance != null) {
                return instance.getCounter() + "";
            } else {
                return "No pet equipped";
            }
        }

        else if (params.equalsIgnoreCase("next_level_counter")) {
            OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
            UUID uuid = player.getUniqueId();
            PetsProfile profile = plugin.getProfileManager().findByUUID(uuid);
            UUID active = profile.getActiveInstance();
            PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(active)).findFirst().orElse(null);
            if (instance != null) {
                String pet_id = instance.getPetId();
                Pet pet = plugin.getProfileManager().getPets().stream().filter(p -> p.getId().equals(pet_id)).findFirst().orElse(null);
                if (pet != null) {
                    long level = instance.getLevel();
                    long nextLevel = level + 1;
                    long maxLevel = pet.getLevels().size();
                    if (nextLevel > maxLevel) {
                        return "Max level";
                    } else {
                        long nextCounter = pet.getLevels().get(nextLevel - 1).getCounterRequirement();
                        return nextCounter + "";
                    }
                } else {
                    return "No pet equipped";
                }
            } else {
                return "No pet equipped";
            }
        }

        else if (params.equalsIgnoreCase("pet_pretty_name")) {
            OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
            UUID uuid = player.getUniqueId();
            PetsProfile profile = plugin.getProfileManager().findByUUID(uuid);
            UUID active = profile.getActiveInstance();
            PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(active)).findFirst().orElse(null);
            if (instance != null) {
                String pet_id = instance.getPetId();
                Pet pet = plugin.getProfileManager().getPets().stream().filter(p -> p.getId().equals(pet_id)).findFirst().orElse(null);
                if (pet != null) {
                    String pName = pet.getPrettyName();
                    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        pName = PlaceholderAPI.setPlaceholders(player, pName);
                    }
                    return ColorUtil.color(pName);
                } else {
                    return "No pet equipped";
                }
            } else {
                return "No pet equipped";
            }
        }

        else if (params.equalsIgnoreCase("pet_rendered_name")) {
            OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
            UUID uuid = player.getUniqueId();
            PetsProfile profile = plugin.getProfileManager().findByUUID(uuid);
            UUID active = profile.getActiveInstance();
            PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(active)).findFirst().orElse(null);
            if (instance != null) {
                String pet_id = instance.getPetId();
                Pet pet = plugin.getProfileManager().getPets().stream().filter(p -> p.getId().equals(pet_id)).findFirst().orElse(null);
                if (pet != null) {
                    String rName = pet.getRenderedName();
                    rName = rName.replace("%player%", player.getName());
                    rName = rName.replace("%level%", String.valueOf(instance.getLevel()));
                    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        rName = PlaceholderAPI.setPlaceholders(player, rName);
                    }
                    return ColorUtil.color(rName);
                } else {
                    return "No pet equipped";
                }
            } else {
                return "No pet equipped";
            }
        }

        return "Unknown Placeholder";
    }
}
