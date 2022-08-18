package net.osial.osialpets.api.events;

import lombok.Getter;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetInstance;
import net.osial.osialpets.pets.PetsProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetChanceRollEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private PetInstance rolled;
    @Getter private Pet rolledPet;
    @Getter private PetsProfile userProfile;

    public PetChanceRollEvent(PetInstance rolled, Pet rolledPet, PetsProfile userProfile) {
        this.rolled = rolled;
        this.rolledPet = rolledPet;
        this.userProfile = userProfile;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
