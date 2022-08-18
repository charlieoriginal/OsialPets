package net.osial.osialpets.api.events;

import lombok.Getter;
import net.osial.osialpets.pets.Pet;
import net.osial.osialpets.pets.PetInstance;
import net.osial.osialpets.pets.PetsProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetActivateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private Pet current;
    @Getter private PetsProfile userProfile;

    public PetActivateEvent(Pet current, PetsProfile userProfile) {
        this.current = current;
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
