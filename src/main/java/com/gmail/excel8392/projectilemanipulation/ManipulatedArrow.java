package com.gmail.excel8392.projectilemanipulation;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ManipulatedArrow extends ManipulatedProjectile {

    public static double maxAngle = Math.toRadians(45.0);
    public static int maxAngleTime = 20; // Ticks
    public static int maxTimeAlive = 10 * 20; // Ticks
    public static int maxFollowTime = 3000; // Millis
    public static double speedMultiplier = 0.25;
    public static double range = 5.0;
    public static double correction = 0.3;

    private final Arrow arrowEntity;
    private final int shooterId;
    private final double shooterYDirection;

    private final Set<Integer> invalidTargets = new HashSet<>();
    @Nullable private Long startPursueTime = null;
    private final double speed;
    private double lastAngle = -1.0;
    private int counter = 0;
    private int timeAlive = 0;

    public ManipulatedArrow(Arrow arrowEntity, int shooterId, double shooterYDirection) {
        super(arrowEntity);
        this.arrowEntity = arrowEntity;
        this.shooterId = shooterId;
        this.shooterYDirection = shooterYDirection;
        this.speed = this.arrowEntity.getVelocity().length() * speedMultiplier;
        this.arrowEntity.setVelocity(this.arrowEntity.getVelocity().setY(this.shooterYDirection).normalize().multiply(this.speed));
    }

    @Override
    public void recalculateTarget() {
        if (!isValid()) return;
        EntityUtil.getNearbyEntities(this.arrowEntity.getLocation(), range, true).thenAccept(entities -> {
            for (Entity entity : entities) {
                if (!(entity instanceof LivingEntity)
                        || entity.getEntityId() == this.shooterId
                        || this.invalidTargets.contains(entity.getEntityId())) continue;
                if (this.target != entity) {
                    this.startPursueTime = System.currentTimeMillis();
                    this.target = entity;
                } else if (System.currentTimeMillis() - this.startPursueTime >= maxFollowTime) {
                    this.invalidTargets.add(entity.getEntityId());
                    this.target = null;
                    this.startPursueTime = null;
                }
                break;
            }
        });
    }

    @Override
    public void tickMovement() {
        if (!isValid()) return;
        this.timeAlive++;
        if (this.timeAlive >= maxTimeAlive) {
            this.arrowEntity.remove();
            return;
        }
        if (this.target != null) {
            LivingEntity livingTarget = (LivingEntity) this.target;
            if (this.lastAngle == -1.0) {
                this.lastAngle = Math.atan2(
                        livingTarget.getEyeLocation().getZ() - this.arrowEntity.getLocation().getZ(),
                        livingTarget.getEyeLocation().getX() - this.arrowEntity.getLocation().getX());
            }
            this.counter++;
            Vector addedVelocity = new Vector( // Calculate vector between projectile and target
                    (livingTarget.getEyeLocation().getX() - this.arrowEntity.getLocation().getX()),
                    (livingTarget.getEyeLocation().getY() - this.arrowEntity.getLocation().getY()),
                    (livingTarget.getEyeLocation().getZ() - this.arrowEntity.getLocation().getZ())
            ).normalize().multiply(correction); // Normalize and multiply by how significant we want our added vector to be in the velocity calculation
            this.arrowEntity.setVelocity(this.arrowEntity.getVelocity().add(addedVelocity).normalize().multiply(this.speed)); // Apply velocity vector, normalize, and multiply by initial magnitude
            if (this.counter >= maxAngleTime) {
                double newAngle = Math.atan2(
                        livingTarget.getEyeLocation().getZ() - this.arrowEntity.getLocation().getZ(),
                        livingTarget.getEyeLocation().getX() - this.arrowEntity.getLocation().getX());
                if (Math.abs(newAngle - this.lastAngle) >= maxAngle) {
                    this.arrowEntity.remove();
                }
                this.lastAngle = newAngle;
                this.counter = 0;
            }
        } else {
            this.counter = 0;
            this.lastAngle = -1.0;
            this.arrowEntity.setVelocity(this.arrowEntity.getVelocity().setY(this.shooterYDirection).normalize().multiply(this.speed)); // Normalize and multiply by initial magnitude
        }
    }

    @Override
    public String getIdentifier() {
        return "arrow";
    }

    @Override
    public boolean isValid() {
        if (this.arrowEntity.isInBlock()) return false;
        return super.isValid();
    }

}
