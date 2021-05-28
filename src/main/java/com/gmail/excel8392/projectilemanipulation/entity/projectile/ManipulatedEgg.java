package com.gmail.excel8392.projectilemanipulation.entity.projectile;

import com.gmail.excel8392.projectilemanipulation.util.EntityUtil;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class ManipulatedEgg extends ManipulatedProjectile {

    public static double range = 20.0; // Range for scanning for nearby targets
    public static double correction = 0.3; // Signifies how accurate the egg will move when on target
    public static double gravity = 0.013; // How much gravity (blocks) will be applied each tick

    private final Egg eggEntity;

    public ManipulatedEgg(@Nullable Egg eggEntity) {
        super(eggEntity);
        this.eggEntity = eggEntity;
    }

    @Override
    public void recalculateTarget() {
        if (!isValid()) return;
        // Use efficient version of World#getNearbyEntities that uses PaperLib's async chunk loading
        EntityUtil.getNearbyEntities(this.eggEntity.getLocation(), range, true).thenAccept(entities -> {
            for (Entity entity : entities) {
                // Because it is sorted by closest to projectile, we want to find the first one that fits our requirements
                if (entity instanceof Monster) {
                    target = entity;
                    return;
                }
            }
            target = null;
        });
    }

    @Override
    public void tickMovement() {
        if (!isValid() || this.target == null) return;
        double magnitude = this.eggEntity.getVelocity().length(); // Calculate initial magnitude before applying new vector
        Mob mobTarget = (Mob) target;
        Vector addedVelocity = new Vector( // Calculate vector between projectile and target
                (mobTarget.getEyeLocation().getX() - this.eggEntity.getLocation().getX()),
                (mobTarget.getEyeLocation().getY() - this.eggEntity.getLocation().getY()),
                (mobTarget.getEyeLocation().getZ() - this.eggEntity.getLocation().getZ())
        ).normalize().multiply(correction); // Normalize and multiply by how significant we want our added vector to be in the velocity calculation
        Vector newVelocity = this.eggEntity.getVelocity().add(addedVelocity); // Apply the added velocity vector
        this.eggEntity.setVelocity(newVelocity.setY(newVelocity.getY() - gravity).normalize().multiply(magnitude)); // Normalize, multiply by initial magnitude, and apply gravity
    }

}
