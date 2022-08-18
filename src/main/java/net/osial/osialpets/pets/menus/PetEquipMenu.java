package net.osial.osialpets.pets.menus;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetsProfile;
import net.osial.osialpets.utils.ColorUtil;
import net.osial.osialpets.utils.ItemBuilder;
import net.osial.osialpets.utils.ProgressUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PetEquipMenu {

    private int page;
    private PetsProfile profile;
    private Inventory inventory;
    private PetInventoryHolder holder;
    private List<Integer> petSlots;
    private List<Integer> fillerSlots;

    public PetEquipMenu(Player player, PetsProfile profile, int page) {
        OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);

        int size = plugin.getConfig().getInt("pet_selector.size");
        String title = ColorUtil.color(plugin.getConfig().getString("pet_selector.title"));

        this.petSlots = plugin.getConfig().getIntegerList("pet_selector.pet_slots");
        this.fillerSlots = plugin.getConfig().getIntegerList("pet_selector.filler_slots");

        String tName = plugin.getConfig().getString("pet_selector.pet_item.name");
        List<String> tLore = plugin.getConfig().getStringList("pet_selector.pet_item.lore");

        holder = new PetInventoryHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inventory);
        this.inventory = inventory;

        int petSlotsSize = petSlots.size();
        int maxPages = (int) Math.ceil(((double) profile.getOwnedPets().size() / (double) petSlotsSize));

        if (page > maxPages) {
            page = maxPages;
        }
        holder.setMaxPage(maxPages);
        holder.setCurrentPage(page);

        //The index in the array we're searching for (array starts at index 1)
        int startIndex = (page - 1) * petSlotsSize;
        int endIndex = startIndex + petSlotsSize;

        for (int i = startIndex; i < endIndex; i++) {
            if (i < profile.getOwnedPets().size()) {
                int slot = petSlots.get(i % petSlotsSize);
                holder.addPetInstance(profile.getOwnedPets().get(i), slot);

                //Checking if Pet Exists
                String petId = profile.getOwnedPets().get(i).getPetId();
                Pet pet = null;
                for (Pet p : plugin.getProfileManager().getPets()) {
                    if (p.getId().equals(petId)) {
                        holder.addPet(p, slot);
                        pet = p;
                        break;
                    }
                }
                //==========================================================

                //Pet Items
                if (pet != null) {
                    String renderedName = pet.getRenderedName();
                    renderedName = renderedName.replace("%player%", player.getName());
                    renderedName = renderedName.replace("%level%", String.valueOf(profile.getOwnedPets().get(i).getLevel()));
                    String name = tName.replace("%pet_rendered_name%", renderedName);
                    name = name.replace("%internal_level%", String.valueOf(profile.getOwnedPets().get(i).getLevel()));
                    name = name.replace("%internal_max_level%", String.valueOf(pet.getLevels().size()));
                    name = name.replace("%internal_counter%", String.valueOf(profile.getOwnedPets().get(i).getCounter()));
                    name = name.replace("%internal_progress_bar%", ProgressUtils.generateProgressBar(profile.getOwnedPets().get(i).getLevel(), pet.getLevels().size()));
                    name = name.replace("%internal_counter_progress_bar%", ProgressUtils.generateProgressBar(profile.getOwnedPets().get(i).getCounter(), pet.getLevels().get(profile.getOwnedPets().get(i).getLevel()).getCounterRequirement()));
                    name = ColorUtil.color(name);

                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        name = PlaceholderAPI.setPlaceholders(player, name);
                    }

                    List<String> lore = new ArrayList<>();
                    for (String line : tLore) {
                        line = line.replace("%pet_rendered_name%", renderedName);
                        line = line.replace("%internal_level%", String.valueOf(profile.getOwnedPets().get(i).getLevel()));
                        line = line.replace("%internal_max_level%", String.valueOf(pet.getLevels().size()));
                        line = line.replace("%internal_counter%", String.valueOf(profile.getOwnedPets().get(i).getCounter()));
                        line = line.replace("%internal_progress_bar%", ProgressUtils.generateProgressBar(profile.getOwnedPets().get(i).getLevel(), pet.getLevels().size()));
                        line = line.replace("%internal_counter_progress_bar%", ProgressUtils.generateProgressBar(profile.getOwnedPets().get(i).getCounter(), pet.getLevels().get(profile.getOwnedPets().get(i).getLevel()).getCounterRequirement()));
                        line = ColorUtil.color(line);
                        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                            line = PlaceholderAPI.setPlaceholders(player, line);
                        }
                        lore.add(line);
                    }

                    ItemStack item = new ItemBuilder("head-" + pet.getBase64())
                            .setName(name)
                            .setLore(lore)
                            .setGlowing(false)
                            .build();

                    inventory.setItem(slot, item);
                }
                //==========================================================
            }
        }

        //Filler Item
        Material material = plugin.getConfig().getString("pet_selector.items.filler_item.material").toUpperCase().equals("NONE") ? Material.AIR : Material.valueOf(plugin.getConfig().getString("pet_selector.items.filler_item.material"));
        List<String> tempLore = plugin.getConfig().getStringList("pet_selector.items.filler_item.lore");
        String tempName = plugin.getConfig().getString("pet_selector.items.filler_item.name");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            tempName = PlaceholderAPI.setPlaceholders(player, tempName);
        }
        List<String> kLore = new ArrayList<>();
        for (String s : tempLore) {
            kLore.add(ColorUtil.color(s));
        }
        tempName = ColorUtil.color(tempName);
        ItemStack filler = new ItemBuilder(material).setName(tempName).setLore(kLore).build();
        for (Integer fSlot : fillerSlots) {
            inventory.setItem(fSlot, filler);
        }
        //==========================================================

        //Next Page Item
        Material nextPageMaterial = plugin.getConfig().getString("pet_selector.items.next_page.material").toUpperCase().equals("NONE") ? Material.AIR : Material.valueOf(plugin.getConfig().getString("pet_selector.items.next_page.material"));
        List<String> nextPageLore = plugin.getConfig().getStringList("pet_selector.items.next_page.lore");
        String nextPageName = plugin.getConfig().getString("pet_selector.items.next_page.name");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            nextPageName = PlaceholderAPI.setPlaceholders(player, nextPageName);
        }
        nextPageName = nextPageName.replace("%current_page%", String.valueOf(page));
        nextPageName = nextPageName.replace("%max_pages%", String.valueOf(maxPages));
        int nextPageSlot = plugin.getConfig().getInt("pet_selector.items.next_page.slot");
        List<String> nextPageKLore = new ArrayList<>();
        for (String s : nextPageLore) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                s = PlaceholderAPI.setPlaceholders(player, s);
            }
            nextPageKLore.add(ColorUtil.color(s));
        }
        nextPageName = ColorUtil.color(nextPageName);
        ItemStack nextPage = new ItemBuilder(nextPageMaterial).setName(nextPageName).setLore(nextPageKLore).build();
        inventory.setItem(nextPageSlot, nextPage);
        holder.setNextPageButton(nextPageSlot);
        //==========================================================

        //Previous Page Item
        Material previousPageMaterial = plugin.getConfig().getString("pet_selector.items.previous_page.material").toUpperCase().equals("NONE") ? Material.AIR : Material.valueOf(plugin.getConfig().getString("pet_selector.items.previous_page.material"));
        List<String> previousPageLore = plugin.getConfig().getStringList("pet_selector.items.previous_page.lore");
        String previousPageName = plugin.getConfig().getString("pet_selector.items.previous_page.name");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            previousPageName = PlaceholderAPI.setPlaceholders(player, previousPageName);
        }
        previousPageName = previousPageName.replace("%current_page%", String.valueOf(page));
        previousPageName = previousPageName.replace("%max_pages%", String.valueOf(maxPages));
        int previousPageSlot = plugin.getConfig().getInt("pet_selector.items.previous_page.slot");
        List<String> previousPageKLore = new ArrayList<>();
        for (String s : previousPageLore) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                s = PlaceholderAPI.setPlaceholders(player, s);
            }
            previousPageKLore.add(ColorUtil.color(s));
        }
        previousPageName = ColorUtil.color(previousPageName);
        ItemStack previousPage = new ItemBuilder(previousPageMaterial).setName(previousPageName).setLore(previousPageKLore).build();
        inventory.setItem(previousPageSlot, previousPage);
        holder.setPreviousPageButton(previousPageSlot);
        //==========================================================

        //Close GUI Item
        Material closeMaterial = plugin.getConfig().getString("pet_selector.items.close_gui.material").toUpperCase().equals("NONE") ? Material.AIR : Material.valueOf(plugin.getConfig().getString("pet_selector.items.close_gui.material"));
        List<String> closeLore = plugin.getConfig().getStringList("pet_selector.items.close_gui.lore");
        String closeName = plugin.getConfig().getString("pet_selector.items.close_gui.name");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            closeName = PlaceholderAPI.setPlaceholders(player, closeName);
        }
        int closeSlot = plugin.getConfig().getInt("pet_selector.items.close_gui.slot");
        List<String> closeKLore = new ArrayList<>();
        for (String s : closeLore) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                s = PlaceholderAPI.setPlaceholders(player, s);
            }
            closeKLore.add(ColorUtil.color(s));
        }
        closeName = ColorUtil.color(closeName);
        ItemStack close = new ItemBuilder(closeMaterial).setName(closeName).setLore(closeKLore).build();
        inventory.setItem(closeSlot, close);
        holder.setCloseButton(closeSlot);
        //==========================================================

        //Toggle Visibility Button
        boolean toggleVisibility = profile.getPetRender().isToggleVisibility();
        int slot = plugin.getConfig().getInt("pet_selector.items.change_visibility.slot");
        if (!toggleVisibility) {
            Material tvMaterial = plugin.getConfig().getString("pet_selector.items.change_visibility.enable.material").toUpperCase().equals("NONE") ? Material.AIR : Material.valueOf(plugin.getConfig().getString("pet_selector.items.change_visibility.enable.material"));
            String tvName = plugin.getConfig().getString("pet_selector.items.change_visibility.enable.name");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                tvName = PlaceholderAPI.setPlaceholders(player, tvName);
            }
            List<String> tvLore = plugin.getConfig().getStringList("pet_selector.items.change_visibility.enable.lore");
            List<String> tvKLore = new ArrayList<>();
            for (String s : tvLore) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    s = PlaceholderAPI.setPlaceholders(player, s);
                }
                tvKLore.add(ColorUtil.color(s));
            }
            tvName = ColorUtil.color(tvName);
            ItemStack tv = new ItemBuilder(tvMaterial).setName(tvName).setLore(tvKLore).build();
            inventory.setItem(slot, tv);
        } else {
            Material tvMaterial = plugin.getConfig().getString("pet_selector.items.change_visibility.disable.material").toUpperCase().equals("NONE") ? Material.AIR : Material.valueOf(plugin.getConfig().getString("pet_selector.items.change_visibility.disable.material"));
            String tvName = plugin.getConfig().getString("pet_selector.items.change_visibility.disable.name");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                tvName = PlaceholderAPI.setPlaceholders(player, tvName);
            }
            List<String> tvLore = plugin.getConfig().getStringList("pet_selector.items.change_visibility.disable.lore");
            List<String> tvKLore = new ArrayList<>();
            for (String s : tvLore) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    s = PlaceholderAPI.setPlaceholders(player, s);
                }
                tvKLore.add(ColorUtil.color(s));
            }
            tvName = ColorUtil.color(tvName);
            ItemStack tv = new ItemBuilder(tvMaterial).setName(tvName).setLore(tvKLore).build();
            inventory.setItem(slot, tv);
        }
        holder.setToggleVisibilityButton(slot);
        //==========================================================

        //Pet Withdraw Mode Toggle
        //Similar to the previous one, but with a different config option
        boolean withdrawMode = holder.isPetWithdrawMode();
        int withdrawModeSlot = plugin.getConfig().getInt("pet_selector.items.pet_withdraw_mode.slot");
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }
}
