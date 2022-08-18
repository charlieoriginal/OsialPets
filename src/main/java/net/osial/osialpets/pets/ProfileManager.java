package net.osial.osialpets.pets;

import lombok.Getter;
import net.osial.osialpets.OsialPets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileManager implements Listener {

    @Getter private final List<PetsProfile> profiles = new ArrayList<>();
    @Getter private final List<Pet> pets = new ArrayList<>();
    private PetsTriggerListener triggerListener;
    private PetRedeemListener redeemListener;

    public ProfileManager() {
        triggerListener = new PetsTriggerListener(this);
        Bukkit.getPluginManager().registerEvents(triggerListener, JavaPlugin.getPlugin(OsialPets.class));
        redeemListener = new PetRedeemListener();
        Bukkit.getPluginManager().registerEvents(redeemListener, JavaPlugin.getPlugin(OsialPets.class));
    }

    //
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerConnectEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        PetsProfile profile = findByUUID(uuid);
        if (profile != null) {
            profile.reconnect();
        } else {
            profile = new PetsProfile(uuid);
            profiles.add(profile);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDisconnectEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        PetsProfile profile = findByUUID(uuid);
        if (profile != null) {
            profile.disconnect();
        }
    }

    public void onStartup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            PetsProfile profile = findByUUID(uuid);
            if (profile != null) {
                profile.reconnect();
            } else {
                profile = new PetsProfile(uuid);
                profiles.add(profile);
            }
            profile.setReinstantiate(true);
        }
    }

    //
    public PetsProfile findByUUID(UUID uuid) {
        for (PetsProfile profile : profiles) {
            if (profile.getUuid().equals(uuid)) {
                return profile;
            }
        }
        return null;
    }

    public void shutdown() {
        //Loop through all profiles, get the renderer, despawn the entity, set entity to null.
        for (PetsProfile profile : profiles) {
            profile.saveData();
            profile.disconnect();
        }
    }

    public void registerPet(Pet pet) {
        pets.add(pet);
    }

    public void unregisterPet(Pet pet) {
        pets.remove(pet);
    }

}
