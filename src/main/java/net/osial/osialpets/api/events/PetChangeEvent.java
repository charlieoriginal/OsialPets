package net.osial.osialpets.api.events;

import lombok.Getter;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetsProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetChangeEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    @Getter private Pet current;
    @Getter private Pet previous;
    @Getter private PetsProfile userProfile;

    public PetChangeEvent(Pet current, Pet previous, PetsProfile userProfile) {
        this.current = current;
        this.previous = previous;
        this.userProfile = userProfile;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
