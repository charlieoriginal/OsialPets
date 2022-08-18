package net.osial.osialpets.pets.menus;

import me.clip.placeholderapi.PlaceholderAPI;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.api.events.PetEquipEvent;
import net.osial.osialpets.api.events.PetUnequipEvent;
import net.osial.osialpets.pets.PetInstance;
import net.osial.osialpets.pets.PetsProfile;
import net.osial.osialpets.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class PetsGUIListener implements Listener {

    private OsialPets plugin;

    public PetsGUIListener(OsialPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() != null) {
            InventoryHolder holder = e.getClickedInventory().getHolder();
            if (holder instanceof PetInventoryHolder) {
                PetInventoryHolder pHolder = (PetInventoryHolder) holder;

                int closeButtonSlot = pHolder.getCloseButton();
                int nextPageButtonSlot = pHolder.getNextPageButton();
                int previousPageButtonSlot = pHolder.getPreviousPageButton();
                int currentPage = pHolder.getCurrentPage();
                int toggleVisibilitySlot = pHolder.getToggleVisibilityButton();
                int maxPage = pHolder.getMaxPage();
                int clickedSlot = e.getSlot();

                if (clickedSlot == closeButtonSlot) {
                    player.closeInventory();
                }

                if (clickedSlot == nextPageButtonSlot) {
                    if (currentPage < maxPage) {
                        PetsProfile profile = plugin.getProfileManager().findByUUID(player.getUniqueId());
                        if (profile != null) {
                            PetEquipMenu menu = new PetEquipMenu(player, profile, currentPage + 1);
                            menu.openInventory(player);
                        }
                    }
                }

                if (clickedSlot == previousPageButtonSlot) {
                    if (currentPage > 1) {
                        PetsProfile profile = plugin.getProfileManager().findByUUID(player.getUniqueId());
                        if (profile != null) {
                            PetEquipMenu menu = new PetEquipMenu(player, profile, currentPage - 1);
                            menu.openInventory(player);
                        }
                    }
                }

                if (clickedSlot == toggleVisibilitySlot) {
                    PetsProfile profile = plugin.getProfileManager().findByUUID(player.getUniqueId());
                    if (profile != null) {
                        profile.getPetRender().setToggleVisibility(!profile.getPetRender().isToggleVisibility());
                        profile.setReinstantiate(true);
                        if (profile.getPetRender().getPetEntity() != null) {
                            profile.getPetRender().getPetEntity().remove();
                        }
                        profile.getPetRender().setPetEntity(null);
                        PetEquipMenu menu = new PetEquipMenu(player, profile, currentPage);
                        menu.openInventory(player);
                    }
                }

                //Check through all keys in the petinstances array to see if the player clicked on one of them.
                for (Integer integer : pHolder.getPetInstanceHashMap().keySet()) {
                    ClickType clickType = e.getClick();

                    if (clickedSlot == integer.intValue()) {
                        PetsProfile profile = plugin.getProfileManager().findByUUID(player.getUniqueId());
                        if (profile != null) {
                            if (clickType.equals(ClickType.LEFT)) {
                                player.closeInventory();
                                PetInstance petInstance = pHolder.getPetInstanceHashMap().get(integer);
                                profile.equipPet(petInstance);
                                profile.setReinstantiate(true);

                                PetEquipEvent event = new PetEquipEvent(petInstance, profile.getActive(), profile);
                                plugin.getServer().getPluginManager().callEvent(event);
                                String petEquipped = plugin.getConfig().getString("messages.pet_equipped");
                                petEquipped = petEquipped.replace("%pet_id%", petInstance.getPetId());
                                if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                    petEquipped = PlaceholderAPI.setPlaceholders(player, petEquipped);
                                }
                                player.sendMessage(ColorUtil.color(petEquipped));
                            } else if (clickType.equals(ClickType.RIGHT)) {
                                player.closeInventory();

                                PetInstance instance = profile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(profile.getActiveInstance())).findFirst().orElse(null);
                                PetUnequipEvent event = new PetUnequipEvent(instance, profile.getActive(), profile);
                                plugin.getServer().getPluginManager().callEvent(event);

                                profile.setActive(null);
                                profile.setActiveInstance(null);
                                if (profile.getPetRender().getPetEntity() != null) {
                                    profile.getPetRender().getPetEntity().remove();
                                }
                                profile.getPetRender().setPetEntity(null);
                                String petUnequipped = plugin.getConfig().getString("messages.pet_unequipped");
                                petUnequipped = petUnequipped.replace("%pet_id%", instance.getPetId());
                                if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                    petUnequipped = PlaceholderAPI.setPlaceholders(player, petUnequipped);
                                }
                                player.sendMessage(ColorUtil.color(petUnequipped));
                            }
                        }
                    }
                }

                e.setCancelled(true);
            }
        }
    }

}
