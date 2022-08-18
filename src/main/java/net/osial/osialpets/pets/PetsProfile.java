package net.osial.osialpets.pets;

import lombok.Getter;
import lombok.Setter;
import net.osial.osialpets.OsialPets;
import net.osial.osialpets.utils.OsialConfig;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PetsProfile {

    private UUID uuid;
    private boolean isOnline;
    private PetRender petRender;
    private BukkitTask task;
    private List<PetInstance> ownedPets;
    private OsialConfig data;
    @Setter private boolean reinstantiate = false;
    @Setter private UUID activeInstance;
    @Setter private Pet active;

    public PetsProfile(UUID uuid) {
        this.uuid = uuid;
        this.isOnline = true;
        this.petRender = new PetRender(uuid, this);
        this.active = null;
        this.ownedPets = new ArrayList<>();
        this.data = new OsialConfig("data/" + uuid.toString());
        this.task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(OsialPets.class), petRender.getPetRenderTask(), 0, 1);
        loadData();
    }

    public void disconnect() {
        task.cancel();
        if (petRender.getPetEntity() != null) {
            petRender.getPetEntity().remove();
            petRender.setPetEntity(null);
        }
        this.isOnline = false;
        saveData();
    }

    public void reconnect() {
        task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(OsialPets.class), petRender.getPetRenderTask(), 0, 1);
        this.isOnline = true;
        this.reinstantiate = true;
    }

    public void equipPet(PetInstance instance) {
        active = null;
        activeInstance = instance.getUuid();
        Pet withId = null;
        for (Pet pet : JavaPlugin.getPlugin(OsialPets.class).getProfileManager().getPets()) {
            if (pet.getId().equals(instance.getPetId())) {
                withId = pet;
                break;
            }
        }
        active = withId;
        if (petRender.getPetEntity() != null) {
            petRender.getPetEntity().remove();
        }
        petRender.setPetEntity(null);
    }

    public void saveData() {
        data.getConfig().set("toggleVisibility", petRender.isToggleVisibility());
        if (activeInstance != null) {
            data.getConfig().set("active", activeInstance.toString());
        }
        for (PetInstance instance : ownedPets) {
            data.getConfig().set("pets." + instance.getUuid().toString() + ".id", instance.getPetId());
            data.getConfig().set("pets." + instance.getUuid().toString() + ".level", instance.getLevel());
            data.getConfig().set("pets." + instance.getUuid().toString() + ".counter", instance.getCounter());
        }
        data.save();
    }

    public void loadData() {
        if (data.getConfig().contains("toggleVisibility")) {
            petRender.setToggleVisibility(data.getConfig().getBoolean("toggleVisibility"));
        } else {
            petRender.setToggleVisibility(false);
        }
        if (data.getConfig().contains("pets")) {
            for (String key : data.getConfig().getConfigurationSection("pets").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                String petId = data.getConfig().getString("pets." + key + ".id");
                int level = data.getConfig().getInt("pets." + key + ".level");
                int counter = data.getConfig().getInt("pets." + key + ".counter");
                PetInstance instance = new PetInstance(uuid, petId, level, counter);
                ownedPets.add(instance);
            }
        }
        if (data.getConfig().contains("active")) {
            for (PetInstance instance : ownedPets) {
                if (instance.getUuid().equals(UUID.fromString(data.getConfig().getString("active")))) {
                    OsialPets pets = JavaPlugin.getPlugin(OsialPets.class);
                    activeInstance = instance.getUuid();
                    Pet pet = pets.getProfileManager().getPets().stream().filter(p -> p.getId().equals(instance.getPetId())).findFirst().orElse(null);
                    active = pet;
                }
            }
        }
    }

}
