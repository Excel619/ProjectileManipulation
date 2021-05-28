package com.gmail.excel8392.projectilemanipulation.entity.projectile;

import com.gmail.excel8392.projectilemanipulation.util.EntityUtil;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ManipulatedEnderpearl extends ManipulatedProjectile {

    public static double range = 100.0; // Range to scan for entities
    public static double speedMultiplier = 0.25; // Multiplied with the normal speed for enderpearls
    public static double correction = 0.3; // Signifies how accurate the enderpearl will move when on target

    private final EnderPearl enderpearlEntity;

    private final double speed; // Speed (magnitude) for this projectile, In this case will be the same for all enderpearls

    public ManipulatedEnderpearl(EnderPearl enderpearlEntity, LivingEntity shooter) {
        super(enderpearlEntity);
        this.enderpearlEntity = enderpearlEntity;
        this.speed = this.enderpearlEntity.getVelocity().length() * speedMultiplier;
        // Uses efficient EntityUtil method for seeing what entity the player is looking at, and assigns our target
        EntityUtil.getLookingAt(shooter, range).thenAccept(entity -> this.target = entity);
    }

    @Override
    public void recalculateTarget() {} // We don't need to recalculate target because that is determined on-spawn

    @Override
    public void tickMovement() {
        if (!isValid() || this.target == null) return;
        LivingEntity livingTarget = (LivingEntity) this.target;
        Vector addedVelocity = new Vector( // Calculate vector between projectile and target
                (livingTarget.getEyeLocation().getX() - this.enderpearlEntity.getLocation().getX()),
                (livingTarget.getEyeLocation().getY() - this.enderpearlEntity.getLocation().getY()),
                (livingTarget.getEyeLocation().getZ() - this.enderpearlEntity.getLocation().getZ())
        ).normalize().multiply(correction); // Normalize and multiply by how significant we want our added vector to be in the velocity calculation
        Vector newVelocity = this.enderpearlEntity.getVelocity().setY(0).add(addedVelocity); // Apply the added velocity vector
        this.enderpearlEntity.setVelocity(newVelocity.normalize().multiply(this.speed)); // Normalize and multiply by determined speed
    }

}
