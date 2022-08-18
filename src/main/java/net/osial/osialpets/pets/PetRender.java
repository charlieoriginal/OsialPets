package net.osial.osialpets.pets;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ArmorStand;

import java.util.UUID;

@Getter
public class PetRender {

    private UUID uuid;
    @Setter private ArmorStand petEntity;
    @Setter private double currentRelativeY;
    @Setter private double maxRelativeY;
    @Setter private double minRelativeY;
    @Setter private int direction;
    @Setter private boolean toggleVisibility;
    private PetRenderTask petRenderTask;

    public PetRender(UUID uuid, PetsProfile profile) {
        direction = -1;

        this.uuid = uuid;
        this.currentRelativeY = 0;
        this.maxRelativeY = 0.1;
        this.minRelativeY = -0.1;
        this.petRenderTask = new PetRenderTask(petEntity, uuid, profile);
    }

}
