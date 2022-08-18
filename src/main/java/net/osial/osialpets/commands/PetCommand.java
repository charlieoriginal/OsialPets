package net.osial.osialpets.commands;

import net.osial.osialpets.OsialPets;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetInstance;
import net.osial.osialpets.pets.PetsProfile;
import net.osial.osialpets.pets.ProfileManager;
import net.osial.osialpets.pets.menus.PetEquipMenu;
import net.osial.osialpets.utils.ColorUtil;
import net.osial.osialpets.utils.NBTEditor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetCommand implements CommandExecutor, TabCompleter {

    private ProfileManager manager;
    private OsialPets plugin;
    private String antiSpam = "%%__FILEHASH__%%";

    public PetCommand(ProfileManager manager, OsialPets plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 0) {
                UUID uuid = player.getUniqueId();
                PetsProfile profile = manager.findByUUID(uuid);
                if (profile != null) {
                    if (profile.getOwnedPets().size() > 0) {
                        PetEquipMenu menu = new PetEquipMenu(player, profile, 1);
                        menu.openInventory(player);
                    }
                }
            }

            //Command: /pet reload
            if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("osialpets.reload")) {
                    plugin.unregisterListeners();
                    plugin.onDisable();
                    plugin.onEnable();
                    String reloadedMessage = plugin.getConfig().getString("messages.plugin_reloaded");
                    commandSender.sendMessage(ColorUtil.color(reloadedMessage));
                }
            }

            //Command: /pet equip
            if (strings.length == 1 && strings[0].equalsIgnoreCase("equip")) {
                UUID uuid = player.getUniqueId();
                PetsProfile profile = manager.findByUUID(uuid);
                if (profile != null) {
                    if (profile.getOwnedPets().size() > 0) {
                        PetEquipMenu menu = new PetEquipMenu(player, profile, 1);
                        menu.openInventory(player);
                    }
                }
            }

            //Command: /pet equip [page]
            if (strings.length == 2 && strings[0].equalsIgnoreCase("equip")) {
                Integer page = null;
                try {
                    page = Integer.parseInt(strings[1]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (page != null && page < 1) {
                    page = 1;
                }
                UUID uuid = player.getUniqueId();
                PetsProfile profile = manager.findByUUID(uuid);
                if (profile != null) {
                    PetEquipMenu menu = new PetEquipMenu(player, profile, page);
                    menu.openInventory(player);
                }
            }

            //Command: /pet giveitem [pet]
            else if (strings.length == 2 && strings[0].equalsIgnoreCase("giveitem")) {
                String petName = strings[1];
                Pet pet = manager.getPets().stream().filter(p -> p.getId().equalsIgnoreCase(petName)).findFirst().orElse(null);
                if (pet != null) {
                    if (player.hasPermission("osialpets.giveitem")) {
                        ItemStack item = pet.getGiveItem().clone();

                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = meta.getLore();
                        String name = meta.getDisplayName();
                        List<String> newLore = new ArrayList<>();
                        for (String l : lore) {
                            l = l.replace("%level%", "1");
                            l = l.replace("%player%", player.getName());
                            newLore.add(l);
                        }
                        meta.setLore(newLore);
                        name = name.replace("%level%", "1");
                        name = name.replace("%player%", player.getName());
                        meta.setDisplayName(name);
                        item.setItemMeta(meta);

                        item = NBTEditor.set(item, 1, "osialpets.level");

                        player.getInventory().addItem(item);
                        String message = plugin.getConfig().getString("messages.given_pet_item");
                        if (message != null) {
                            message = message.replace("%pet_id%", pet.getId());
                            player.sendMessage(ColorUtil.color(message));
                        }
                    } else {
                        String noPermissionMessage = plugin.getConfig().getString("messages.no_permission");
                        player.sendMessage(ColorUtil.color(noPermissionMessage));
                    }
                }
            }

            else if (strings.length == 3 && strings[0].equalsIgnoreCase("giveitem")) {
                String petName = strings[1];
                String playerName = strings[2];
                Pet pet = manager.getPets().stream().filter(p -> p.getId().equalsIgnoreCase(petName)).findFirst().orElse(null);
                if (pet != null) {
                    if (player.hasPermission("osialpets.giveitem")) {
                        ItemStack item = pet.getGiveItem().clone();

                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = meta.getLore();
                        String name = meta.getDisplayName();
                        List<String> newLore = new ArrayList<>();
                        for (String l : lore) {
                            l = l.replace("%level%", "1");
                            l = l.replace("%player%", playerName);
                            newLore.add(l);
                        }
                        meta.setLore(newLore);
                        name = name.replace("%level%", "1");
                        name = name.replace("%player%", playerName);
                        meta.setDisplayName(name);
                        item.setItemMeta(meta);

                        item = NBTEditor.set(item, 1, "osialpets.level");

                        Player target = plugin.getServer().getPlayer(playerName);
                        if (target != null) {
                            target.getInventory().addItem(item);
                            String message = plugin.getConfig().getString("messages.given_pet_item");
                            if (message != null) {
                                message = message.replace("%pet_id%", pet.getId());
                                player.sendMessage(ColorUtil.color(message));
                            }
                        } else {
                            String offlineMessage = plugin.getConfig().getString("messages.offline_player");
                            player.sendMessage(ColorUtil.color(offlineMessage));
                        }
                    } else {
                        String noPermissionMessage = plugin.getConfig().getString("messages.no_permission");
                        player.sendMessage(ColorUtil.color(noPermissionMessage));
                    }
                }
            }

            if (strings.length == 4 && strings[0].equalsIgnoreCase("giveitem")) {
                String petName = strings[1];
                String playerName = strings[2];
                Integer level = null;
                try {
                    level = Integer.parseInt(strings[3]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (level != null && level < 1) {
                    level = 1;
                }
                Pet pet = manager.getPets().stream().filter(p -> p.getId().equalsIgnoreCase(petName)).findFirst().orElse(null);
                if (pet != null) {
                    if (player.hasPermission("osialpets.giveitem")) {
                        ItemStack item = pet.getGiveItem().clone();

                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = meta.getLore();
                        String name = meta.getDisplayName();
                        List<String> newLore = new ArrayList<>();
                        for (String l : lore) {
                            l = l.replace("%level%", level.toString());
                            l = l.replace("%player%", playerName);
                            newLore.add(l);
                        }
                        meta.setLore(newLore);
                        name = name.replace("%level%", level.toString());
                        name = name.replace("%player%", playerName);
                        meta.setDisplayName(name);
                        item.setItemMeta(meta);

                        item = NBTEditor.set(item, level, "osialpets.level");

                        Player target = plugin.getServer().getPlayer(playerName);
                        if (target != null) {
                            target.getInventory().addItem(item);
                            String message = plugin.getConfig().getString("messages.given_pet_item");
                            if (message != null) {
                                message = message.replace("%pet_id%", pet.getId());
                                player.sendMessage(ColorUtil.color(message));
                            }
                        } else {
                            String offlineMessage = plugin.getConfig().getString("messages.offline_player");
                            player.sendMessage(ColorUtil.color(offlineMessage));
                        }
                    } else {
                        String noPermissionMessage = plugin.getConfig().getString("messages.no_permission");
                        player.sendMessage(ColorUtil.color(noPermissionMessage));
                    }
                }
            }

            //Give command, gives player a PetInstance.
            else if (strings.length == 2 && strings[0].equalsIgnoreCase("give")) {
                if (player.hasPermission("osialpets.give")) {
                    Pet pet = null;
                    for (Pet p : manager.getPets()) {
                        if (p.getId().equalsIgnoreCase(strings[1])) {
                            pet = p;
                            break;
                        }
                    }
                    if (pet != null) {
                        PetsProfile profile = manager.findByUUID(player.getUniqueId());
                        if (profile != null) {
                            profile.setActive(pet);
                            PetInstance instance = new PetInstance(pet.getId(), 1, 0L);
                            profile.getOwnedPets().add(instance);
                            profile.setActiveInstance(instance.getUuid());
                            if (profile.getPetRender().getPetEntity() != null) {
                                profile.getPetRender().getPetEntity().remove();
                                profile.getPetRender().setPetEntity(null);
                            }
                            profile.setReinstantiate(true);

                            String givenPet = plugin.getConfig().getString("messages.given_pet");
                            givenPet = givenPet.replace("%pet_id%", pet.getId());

                            player.sendMessage(ColorUtil.color(givenPet));
                        }
                    }
                } else {
                    String noPermission = plugin.getConfig().getString("messages.no_permission");
                    noPermission = ColorUtil.color(noPermission);
                    player.sendMessage(noPermission);
                }
            }

            //Command: /pet give {pet_id} {player_name}
            else if (strings.length == 3 && strings[0].equalsIgnoreCase("give")) {
                if (player.hasPermission("osialpets.give")) {
                    Pet pet = null;
                    for (Pet p : manager.getPets()) {
                        if (p.getId().equalsIgnoreCase(strings[1])) {
                            pet = p;
                            break;
                        }
                    }
                    if (pet != null) {
                        Player target = plugin.getServer().getPlayer(strings[2]);
                        if (target != null) {
                            PetsProfile profile = manager.findByUUID(target.getUniqueId());
                            if (profile != null) {
                                profile.setActive(pet);
                                PetInstance instance = new PetInstance(pet.getId(), 1, 0L);
                                profile.getOwnedPets().add(instance);
                                profile.setActiveInstance(instance.getUuid());
                                if (profile.getPetRender().getPetEntity() != null) {
                                    profile.getPetRender().getPetEntity().remove();
                                    profile.getPetRender().setPetEntity(null);
                                }
                                profile.setReinstantiate(true);

                                String givenPet = plugin.getConfig().getString("messages.given_pet_other");
                                givenPet = givenPet.replace("%pet_id%", pet.getId());
                                givenPet = givenPet.replace("%player%", target.getName());

                                player.sendMessage(ColorUtil.color(givenPet));
                            }
                        } else {
                            String noPlayer = plugin.getConfig().getString("messages.no_player");
                            noPlayer = ColorUtil.color(noPlayer);
                            player.sendMessage(noPlayer);
                        }
                    }
                } else {
                    String noPermission = plugin.getConfig().getString("messages.no_permission");
                    noPermission = ColorUtil.color(noPermission);
                    player.sendMessage(noPermission);
                }
            }

            //Other Commands (NOT IMPLEMENTED)
            else {
                return false;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> values = new ArrayList<>();
            if (commandSender.hasPermission("osialpets.give")) {
                values.add("give");
            }
            if (commandSender.hasPermission("osialpets.giveitem")) {
                values.add("giveitem");
            }

            values.add("equip");
            return values;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("set") || strings.length == 2 && strings[0].equalsIgnoreCase("give") || strings.length == 2 && strings[0].equalsIgnoreCase("giveitem")) {
            List<String> values = new ArrayList<>();
            values.add("none");
            List<Pet> pets = manager.getPets();
            for (Pet pet : pets) {
                values.add(pet.getId());
            }
            return values;
        }

        if (strings.length == 3 && strings[0].equalsIgnoreCase("giveitem")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }

        if (strings.length == 4 && strings[0].equalsIgnoreCase("giveitem")) {
            List<String> numberExamples = new ArrayList<>();
            numberExamples.add("1");
            numberExamples.add("2");
            numberExamples.add("3");
            numberExamples.add("4");
            numberExamples.add("5");
            return numberExamples;
        }

        return null;
    }
}
