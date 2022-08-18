package net.osial.osialpets.pets;

import net.osial.osialpets.OsialPets;
import net.osial.osialpets.utils.NBTEditor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PetRedeemListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        ItemStack hand = e.getItemInHand();
        if (hand != null) {
            if (hand.hasItemMeta()) {
                if (NBTEditor.contains(hand, "osialpets.pet")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        ItemStack hand = e.getItem();
        if (hand != null) {
            if (hand.hasItemMeta()) {
                if (NBTEditor.contains(hand, "osialpets.pet")) {
                    int level = NBTEditor.getInt(hand, "osialpets.level");
                    String pet = NBTEditor.getString(hand, "osialpets.pet");
                    Pet petObject = JavaPlugin.getPlugin(OsialPets.class).getProfileManager().getPets().stream().filter(p -> p.getId().equals(pet)).findFirst().orElse(null);
                    if (petObject != null) {
                        PetInstance instance = new PetInstance(petObject.getId(), level, 0);
                        PetsProfile profile = JavaPlugin.getPlugin(OsialPets.class).getProfileManager().findByUUID(e.getPlayer().getUniqueId());
                        if (profile != null) {
                            profile.getOwnedPets().add(instance);
                            profile.saveData();
                        }
                    }

                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    int handSlot = e.getPlayer().getInventory().getHeldItemSlot();
                    removeOrReduceByOne(hand, handSlot, e.getPlayer());
                    e.setCancelled(true);
                }
            }
        }
    }

    public void removeOrReduceByOne(ItemStack item, int slot, Player player) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().setItem(slot, item);
        } else {
            player.getInventory().setItem(slot, null);
        }
    }

}
