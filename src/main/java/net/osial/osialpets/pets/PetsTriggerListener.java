package net.osial.osialpets.pets;

import net.osial.osialpets.pets.sub.TriggerConditionType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

public class PetsTriggerListener implements Listener {

    private ProfileManager profileManager;

    public PetsTriggerListener(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        PetsProfile profile = profileManager.findByUUID(uuid);
        PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
        if (profile != null && instance != null) {
            profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_BLOCK_BREAK", player, instance.getLevel());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityKillEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity en = (LivingEntity) e.getEntity();

            if (en.getHealth() < e.getFinalDamage()) {
                if (e.getDamager() instanceof Player) {
                    Player player = (Player) e.getDamager();
                    UUID uuid = player.getUniqueId();
                    PetsProfile profile = profileManager.findByUUID(uuid);
                    PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
                    if (profile != null && instance != null) {
                        if (!(e.getEntity() instanceof Player)) {
                            profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_KILL_ENTITY", player, instance.getLevel());
                            return;
                        } else {
                            profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_KILL_PLAYER", player, instance.getLevel());
                            return;
                        }
                    }
                }
            }

            if (e.getDamager() instanceof Player) {
                Player player = (Player) e.getDamager();
                if (e.getEntity() instanceof Player) {
                    UUID uuid = player.getUniqueId();
                    PetsProfile profile = profileManager.findByUUID(uuid);
                    PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
                    if (profile != null && instance != null) {
                        profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_DAMAGE_PLAYER", player, instance.getLevel());
                        return;
                    }
                } else {
                    UUID uuid = player.getUniqueId();
                    PetsProfile profile = profileManager.findByUUID(uuid);
                    PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
                    if (profile != null && instance != null) {
                        profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_DAMAGE_ENTITY", player, instance.getLevel());
                        return;
                    }
                }
            }

            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                UUID uuid = player.getUniqueId();
                PetsProfile profile = profileManager.findByUUID(uuid);
                PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
                if (profile != null && instance != null) {
                    profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_DAMAGE_BY_ENTITY", player, instance.getLevel());
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFishCatch(PlayerFishEvent e) {
        PlayerFishEvent.State state = e.getState();
        if (state.equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            PetsProfile profile = profileManager.findByUUID(uuid);
            PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
            if (profile != null && instance != null) {
                profile.getActive().triggerCheck(TriggerConditionType.EVENT, "PLAYER_FISH_CATCH", player, instance.getLevel());
            }
        }
    }

}
