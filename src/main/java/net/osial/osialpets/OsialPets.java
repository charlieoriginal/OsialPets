package net.osial.osialpets;

import lombok.Getter;
import net.osial.osialpets.commands.PetCommand;
import net.osial.osialpets.hooks.OsialPetsPAPI;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetLevel;
import net.osial.osialpets.pets.PetParticle;
import net.osial.osialpets.pets.ProfileManager;
import net.osial.osialpets.pets.menus.PetsGUIListener;
import net.osial.osialpets.pets.sub.ProcEffect;
import net.osial.osialpets.pets.sub.ProcEffectType;
import net.osial.osialpets.pets.sub.TriggerCondition;
import net.osial.osialpets.pets.sub.TriggerConditionType;
import net.osial.osialpets.utils.ColorUtil;
import net.osial.osialpets.utils.ItemBuilder;
import net.osial.osialpets.utils.NBTEditor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class OsialPets extends JavaPlugin {

    @Getter private ProfileManager profileManager;
    private OsialPetsPAPI papi;
    private PetsGUIListener listener;
    private PetCommand command;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Plugin startup logic
        profileManager = new ProfileManager();
        loadPets();
        profileManager.onStartup();
        // Register Events
        getServer().getPluginManager().registerEvents(profileManager, this);

        command = new PetCommand(profileManager, this);
        getCommand("pet").setExecutor(command);
        getCommand("pet").setTabCompleter(command);

        listener = new PetsGUIListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            papi = new OsialPetsPAPI();
            papi.register();
        }

        String consoleGreen = "\u001B[32m";
        String consoleYellow = "\u001B[33m";
        getLogger().info(consoleGreen + "==========================================================");
        getLogger().info("");
        getLogger().info("  " + consoleGreen + "OsialPets v" + getDescription().getVersion() + " build 7");
        getLogger().info("  " + consoleYellow + "You are utilizing a very early build of OsialPets. Bugs are expected.");
        getLogger().info("  " + consoleYellow+ "Please report any issues to wizard-chan#8732");
        getLogger().info("  " + consoleGreen + "Thanks for using OsialPets!");
        getLogger().info("");
        getLogger().info(consoleGreen+"==========================================================" + "\u001B[0m");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        profileManager.shutdown();
    }

    private void loadPets() {
        // Load pets
        ConfigurationSection petsSection = getConfig().getConfigurationSection("pets");
        Set<String> pets = petsSection.getKeys(false);
        for (String pet : pets) {
            ConfigurationSection petSection = petsSection.getConfigurationSection(pet);
            String id = pet;
            String base64 = getConfig().getString("pets." + id + ".skin");
            String renderedName = getConfig().getString("pets." + id + ".rendered_name");
            String prettyName = getConfig().getString("pets." + id + ".pretty_name");

            double offsetY = getConfig().getDouble("pets." + id + ".particle.offset_y");
            int count = getConfig().getInt("pets." + id + ".particle.count");
            double randomOffsetXMax = getConfig().getDouble("pets." + id + ".particle.random_offset_x_max");
            double randomOffsetZMax = getConfig().getDouble("pets." + id + ".particle.random_offset_z_max");
            double randomOffsetYMax = getConfig().getDouble("pets." + id + ".particle.random_offset_y_max");
            String ptype = getConfig().getString("pets." + id + ".particle.particle_type");
            PetParticle petParticle = new PetParticle(offsetY, count, randomOffsetXMax, randomOffsetZMax, randomOffsetYMax, ptype);

            String material = getConfig().getString("pets." + id + ".give_item.material");
            boolean glowing = getConfig().getBoolean("pets." + id + ".give_item.glowing");
            String name = getConfig().getString("pets." + id + ".give_item.name");
            List<String> lore = getConfig().getStringList("pets." + id + ".give_item.lore");
            ItemStack giveItem = new ItemBuilder(material).setGlowing(glowing).setName(ColorUtil.color(name)).setLore(lore).build();
            giveItem = NBTEditor.set(giveItem, pet, "osialpets.pet");

            Pet p = new Pet(id, base64, renderedName, petParticle, prettyName, giveItem);

            ConfigurationSection procEffectsSection = petSection.getConfigurationSection("proc_effects");
            if (procEffectsSection != null) {
                for (String k : procEffectsSection.getKeys(false)) {
                    ConfigurationSection procEffectSection = procEffectsSection.getConfigurationSection(k);
                    String type = procEffectSection.getString("type");
                    String value = procEffectSection.getString("value");
                    double chance = procEffectSection.getDouble("chance");
                    ProcEffect procEffect = new ProcEffect(ProcEffectType.valueOf(type), value, chance);
                    p.addProcEffect(procEffect);
                }
            }

            ConfigurationSection triggerConditionsSection = petSection.getConfigurationSection("trigger_conditions");
            if (triggerConditionsSection != null) {
                for (String k : triggerConditionsSection.getKeys(false)) {
                    ConfigurationSection triggerConditionSection = triggerConditionsSection.getConfigurationSection(k);
                    String type = triggerConditionSection.getString("type");
                    String value = triggerConditionSection.getString("value");
                    double chance = triggerConditionSection.getDouble("chance");
                    TriggerCondition triggerCondition = new TriggerCondition(TriggerConditionType.valueOf(type), value, chance);
                    p.addTriggerCondition(triggerCondition);
                }
            }

            long i = 1L;
            ConfigurationSection levelSections = petSection.getConfigurationSection("levels");
            if (levelSections != null) {
                for (String k : levelSections.getKeys(false)) {
                    ConfigurationSection levelSection = levelSections.getConfigurationSection(k);
                    long counterRequirement = levelSection.getLong("counter_requirement");
                    double chanceAddition = levelSection.getDouble("chance_addition");
                    PetLevel level = new PetLevel(i, counterRequirement, chanceAddition);
                    p.addLevel(i, level);
                    i++;
                }
            }

            profileManager.registerPet(p);
        }
    }

    public void unregisterListeners() {
        PlayerFishEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
    }
}
