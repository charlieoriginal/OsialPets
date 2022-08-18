package net.osial.osialpets.pets;

import net.osial.osialpets.OsialPets;
import net.osial.osialpets.utils.ColorUtil;
import net.osial.osialpets.utils.ItemBuilder;
import net.osial.osialpets.utils.VectorUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PetRenderTask implements Runnable {

    private Entity petEntity;
    private UUID uuid;
    private PetsProfile petsProfile;
    private Particle particle;

    public PetRenderTask(Entity petEntity, UUID player, PetsProfile profile) {
        this.petEntity = petEntity;
        this.uuid = player;
        this.petsProfile = profile;
        this.particle = Particle.SOUL;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            if (!petsProfile.getPetRender().isToggleVisibility()) {
                Vector playerPosition = player.getLocation().toVector();
                Vector unitRightBehind = new Vector(-0.3, 0, 1);
                Vector relativeRightBehind = VectorUtils.rotateVector(unitRightBehind, player.getLocation().getYaw(), player.getLocation().getPitch());
                Vector petPosition = playerPosition.add(relativeRightBehind);
                petPosition = petPosition.add(new Vector(0, 1.4, 0));

                if (petsProfile.isReinstantiate()) {
                    Vector finalPetPosition = petPosition;
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(OsialPets.class), () -> {
                        if (petsProfile.getPetRender().getPetEntity() != null) {
                            petsProfile.getPetRender().getPetEntity().remove();
                            petsProfile.getPetRender().setPetEntity(null);
                        }

                        //Spawn a new armorstand.
                        if (petsProfile.getActive() != null) {
                            ItemStack head = new ItemBuilder("head-" + petsProfile.getActive().getBase64())
                                    .setAmount(1)
                                    .setGlowing(false)
                                    .build();

                            petEntity = player.getWorld().spawn(finalPetPosition.toLocation(player.getWorld()), ArmorStand.class);

                            PetInstance equipped = petsProfile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(petsProfile.getActiveInstance())).findFirst().orElse(null);

                            String name = petsProfile.getActive().getRenderedName();
                            name = name.replace("%player%", player.getName());
                            if (equipped == null) {
                                name = name.replace("%level%", "1");
                            } else {
                                name = name.replace("%level%", String.valueOf(equipped.getLevel()));
                            }
                            name = ColorUtil.color(name);

                            petEntity.setCustomName(name);
                            ((ArmorStand) petEntity).setSmall(true);
                            ((ArmorStand) petEntity).setInvisible(true);
                            petEntity.setCustomNameVisible(true);
                            petEntity.setGravity(false);
                            petEntity.setInvulnerable(true);
                            petsProfile.getPetRender().setPetEntity((ArmorStand) petEntity);
                            ((ArmorStand) petEntity).setCollidable(false);
                            ((ArmorStand) petEntity).getEquipment().setHelmet(head);
                        }
                    }, 2L);
                }

                petsProfile.setReinstantiate(false);

                if (petEntity == null) {
                    if (petsProfile.getActive() != null) {
                        //Spawn a new armorstand.
                        ItemStack head = new ItemBuilder("head-" + petsProfile.getActive().getBase64())
                                .setAmount(1)
                                .setGlowing(false)
                                .build();

                        petEntity = player.getWorld().spawn(player.getLocation(), ArmorStand.class);

                        PetInstance equipped = petsProfile.getOwnedPets().stream().filter(pet -> pet.getUuid().equals(petsProfile.getActiveInstance())).findFirst().orElse(null);

                        String name = petsProfile.getActive().getRenderedName();
                        name = name.replace("%player%", player.getName());
                        if (equipped == null) {
                            name = name.replace("%level%", "1");
                        } else {
                            name = name.replace("%level%", String.valueOf(equipped.getLevel()));
                        }
                        name = ColorUtil.color(name);

                        petEntity.setCustomName(name);
                        ((ArmorStand) petEntity).setSmall(true);
                        ((ArmorStand) petEntity).setInvisible(true);
                        petEntity.setCustomNameVisible(true);
                        petEntity.setGravity(false);
                        petEntity.setInvulnerable(true);
                        petsProfile.getPetRender().setPetEntity((ArmorStand) petEntity);
                        ((ArmorStand) petEntity).setCollidable(false);
                        ((ArmorStand) petEntity).getEquipment().setHelmet(head);
                    } else {
                        if (petsProfile.getPetRender().getPetEntity() != null) {
                            petsProfile.getPetRender().getPetEntity().remove();
                            petsProfile.getPetRender().setPetEntity(null);
                        }
                    }
                    return;
                }

                boolean petNull = petsProfile.getActive() == null;

                if (!petNull) {
                    double distanceBetweenPetAndPlayer = petEntity.getLocation().toVector().distance(playerPosition);
                    if (distanceBetweenPetAndPlayer > 30) {
                        petEntity.remove();
                        petEntity = null;
                        petsProfile.getPetRender().setPetEntity(null);
                        return;
                    }

                    double currentRelY = petsProfile.getPetRender().getCurrentRelativeY();
                    int direction = petsProfile.getPetRender().getDirection();
                    double minRelativeY = JavaPlugin.getPlugin(OsialPets.class).getConfig().getDouble("pets." + petsProfile.getActive().getId() + ".up_down_movement.min_y");
                    double maxRelativeY = JavaPlugin.getPlugin(OsialPets.class).getConfig().getDouble("pets." + petsProfile.getActive().getId() + ".up_down_movement.max_y");
                    double increment = JavaPlugin.getPlugin(OsialPets.class).getConfig().getDouble("pets." + petsProfile.getActive().getId() + ".up_down_movement.speed");

                    //Direction details:
                    // Up = 1
                    // Down = -1

                    if (direction == 1) {
                        if (currentRelY <= maxRelativeY) {
                            currentRelY += increment;

                            //Set
                            petsProfile.getPetRender().setCurrentRelativeY(currentRelY);
                        } else {
                            direction = -1;

                            //Set
                            petsProfile.getPetRender().setCurrentRelativeY(currentRelY);
                            petsProfile.getPetRender().setDirection(direction);
                        }
                    } else if (direction == -1) {
                        if (currentRelY >= minRelativeY) {
                            currentRelY -= increment;

                            //Set
                            petsProfile.getPetRender().setCurrentRelativeY(currentRelY);
                        } else {
                            direction = 1;

                            //Set
                            petsProfile.getPetRender().setCurrentRelativeY(currentRelY);
                            petsProfile.getPetRender().setDirection(direction);
                        }
                    }

                    Vector movePosition = petPosition.add(new Vector(0, currentRelY, 0));
                    Location movePosActual = movePosition.toLocation(player.getWorld());
                    movePosActual.setPitch(player.getLocation().getPitch());
                    movePosActual.setYaw(player.getLocation().getYaw());
                    petEntity.teleport(movePosActual);

                    PetParticle p = petsProfile.getActive().getPetParticle();

                    //Random offset between .01 & .3
                    double randomX = Math.random() * p.getRandomOffsetXMax();
                    double randomY = Math.random() * p.getRandomOffsetYMax();
                    double randomZ = Math.random() * p.getRandomOffsetZMax();

                    Vector particlePosition = petPosition.add(new Vector(0, p.getOffsetY(), 0));
                    Location particlePosActual = particlePosition.toLocation(player.getWorld());
                    player.getWorld().spawnParticle(p.getType(), particlePosActual, p.getCount(), randomX, randomY, randomZ, 0);
                }
            } else {
                //If entity exists, despawn it.
                if (petEntity != null) {
                    petEntity.remove();
                    petsProfile.getPetRender().setPetEntity(null);
                }
            }
        }
    }
}
