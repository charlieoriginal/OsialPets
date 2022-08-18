package net.osial.osialpets.api.events;

import lombok.Getter;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetInstance;
import net.osial.osialpets.pets.PetsProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PetEquipEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private PetInstance petInstance;
    private Pet pet;
    private PetsProfile profile;

    public PetEquipEvent(PetInstance petInstance, Pet pet, PetsProfile profile) {
        this.petInstance = petInstance;
        this.pet = pet;
        this.profile = profile;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
