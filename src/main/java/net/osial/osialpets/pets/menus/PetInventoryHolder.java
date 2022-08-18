package net.osial.osialpets.pets.menus;

import lombok.Getter;
import lombok.Setter;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetInstance;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;

@Getter
public class PetInventoryHolder implements InventoryHolder {

    @Setter private Inventory inventory;
    private HashMap<Integer, PetInstance> petInstanceHashMap;
    private HashMap<Integer, Pet> petHashMap;
    @Setter private Integer closeButton;
    @Setter private Integer nextPageButton;
    @Setter private Integer previousPageButton;
    @Setter private Integer toggleVisibilityButton;
    @Setter private Integer currentPage;
    @Setter private Integer maxPage;
    @Setter private boolean petWithdrawMode = false;

    public PetInventoryHolder() {
        petInstanceHashMap = new HashMap<>();
        petHashMap = new HashMap<>();
    }

    public void addPetInstance(PetInstance petInstance, int slot) {
        petInstanceHashMap.put(slot, petInstance);
    }

    public void addPet(Pet pet, int slot) {
        petHashMap.put(slot, pet);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
