package com.gmail.excel8392.projectilemanipulation;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ManipulatedEnderpearl extends ManipulatedProjectile {

    public static double range = 100.0;
    public static double speedMultiplier = 0.25;
    public static double correction = 0.3;

    private final EnderPearl enderpearlEntity;
    private final LivingEntity shooter;

    private final double speed;


    public ManipulatedEnderpearl(EnderPearl enderpearlEntity, LivingEntity shooter) {
        super(enderpearlEntity);
        this.enderpearlEntity = enderpearlEntity;
        this.shooter = shooter;
        this.speed = this.enderpearlEntity.getVelocity().length() * speedMultiplier;
        EntityUtil.getLookingAt(this.shooter, range).thenAccept(entity -> this.target = entity);
    }

    @Override
    public void recalculateTarget() {}

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
        this.enderpearlEntity.setVelocity(newVelocity.normalize().multiply(this.speed)); // Normalize, multiply by initial magnitude, and apply gravity
    }

    @Override
    public String getIdentifier() {
        return "enderpearl";
    }

}
