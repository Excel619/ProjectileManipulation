package com.gmail.excel8392.projectilemanipulation.entity.projectile;

import com.gmail.excel8392.projectilemanipulation.util.EntityUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ManipulatedArrow extends ManipulatedProjectile {

    public static double maxAngle = Math.toRadians(45.0); // Max angle that arrow can turn in x seconds
    public static int maxAngleTime = 20; // (Ticks) Amount of time for max angle
    public static int maxTimeAlive = 10 * 20; // (Ticks) Max time arrow can be alive
    public static int maxFollowTime = 3000; // (Millis) Max time arrow will follow target
    public static double speedMultiplier = 0.25; // Multiplied with the normal speed for our arrow
    public static double range = 5.0; // Range at which to scan for targets
    public static double correction = 0.3; // Signifies how accurate the arrow will move when on target

    private final Arrow arrowEntity;
    private final int shooterId; // We don't store Entity to avoid storing a player that has logged out
    private final double shooterYDirection; // How much speed in Y direction we have initially, to make arrow travel on cursor

    private final Set<Integer> invalidTargets = new HashSet<>(); // List of targets arrow has unsuccessfully tried to track
    @Nullable private Long startPursueTime = null; // When it started pursuing it's last target, in millis
    private final double speed; // Speed for this arrow specifically (depends on how far back bow is drawn)
    private double lastAngle = -1.0; // Used for calculation of maxAngle, last angle between arrow and target. -1 indicates no target.
    private int counter = 0; // Used for calculation of maxAngle, skips number of ticks until we check the angle between arrow and target again.
    private int timeAlive = 0; // Incremented in ticks, how long the arrow has been alive

    public ManipulatedArrow(Arrow arrowEntity, int shooterId, double shooterYDirection) {
        super(arrowEntity);
        this.arrowEntity = arrowEntity;
        this.shooterId = shooterId;
        this.shooterYDirection = shooterYDirection;
        this.speed = this.arrowEntity.getVelocity().length() * speedMultiplier;
        // Change Y speed so that arrow follows cursor correctly without an arc
        this.arrowEntity.setVelocity(this.arrowEntity.getVelocity().setY(this.shooterYDirection).normalize().multiply(this.speed));
        this.arrowEntity.setGravity(false);
    }

    @Override
    public void recalculateTarget() {
        if (!isValid()) return;
        // Use efficient version of World#getNearbyEntities that uses PaperLib's async chunk loading
        EntityUtil.getNearbyEntities(this.arrowEntity.getLocation(), range, true).thenAccept(entities -> {
            for (Entity entity : entities) {
                // Because it is sorted by closest to projectile, we want to find the first one that fits our requirements
                if (!(entity instanceof LivingEntity)
                        || entity.getEntityId() == this.shooterId
                        || this.invalidTargets.contains(entity.getEntityId())) continue;
                if (this.target != entity) { // If we found a new target...
                    this.startPursueTime = System.currentTimeMillis(); // Start new pursue time
                    this.target = entity;
                } else if (System.currentTimeMillis() - this.startPursueTime >= maxFollowTime) { // If we have follow our current target for too long...
                    this.invalidTargets.add(entity.getEntityId()); // Target is now invalidated
                    this.target = null; // Need to find a new target
                    this.startPursueTime = null; // Pursue time reset
                }
                break;
            }
        });
    }

    @Override
    public void tickMovement() {
        if (!isValid()) return;
        this.timeAlive++;
        if (this.timeAlive >= maxTimeAlive) { // Check if arrow has been alive for too long
            this.arrowEntity.remove();
            return;
        }
        if (this.target != null) { // If we have a target...
            LivingEntity livingTarget = (LivingEntity) this.target;
            if (this.lastAngle == -1.0) { // Means that we haven't calculated angle to our target yet (new target)
                this.lastAngle = Math.atan2( // Use atan2 to calculate angle between the two. Used for maxAngle.
                        livingTarget.getEyeLocation().getZ() - this.arrowEntity.getLocation().getZ(),
                        livingTarget.getEyeLocation().getX() - this.arrowEntity.getLocation().getX());
            }
            this.counter++; // Increment tick counter for next time we check the angle (maxAngle)

            // Here we apply the velocity correction towards target:
            Vector addedVelocity = new Vector( // Calculate vector between projectile and target
                    (livingTarget.getEyeLocation().getX() - this.arrowEntity.getLocation().getX()),
                    (livingTarget.getEyeLocation().getY() - this.arrowEntity.getLocation().getY()),
                    (livingTarget.getEyeLocation().getZ() - this.arrowEntity.getLocation().getZ())
            ).normalize().multiply(correction); // Normalize and multiply by how significant we want our added vector to be in the velocity calculation
            this.arrowEntity.setVelocity(this.arrowEntity.getVelocity().add(addedVelocity).normalize().multiply(this.speed)); // Apply velocity vector, normalize, and multiply by initial magnitude

            // Check for max angle:
            if (this.counter >= maxAngleTime) { // We have waited until next angle check
                double newAngle = Math.atan2( // Use atan2 to calculate angle between the two. Used for maxAngle.
                        livingTarget.getEyeLocation().getZ() - this.arrowEntity.getLocation().getZ(),
                        livingTarget.getEyeLocation().getX() - this.arrowEntity.getLocation().getX());
                if (Math.abs(newAngle - this.lastAngle) >= maxAngle) { // If the difference between angles is greater than max angle remove arrow
                    this.arrowEntity.remove();
                }
                this.lastAngle = newAngle; // Our current angle is now lastAngle
                this.counter = 0; // Reset tick counter
            }
        } else { // If we don't have a target...
            this.counter = 0; // Reset our tick counter
            this.lastAngle = -1.0; // -1 indicates no last angle
            // Correct our velocity (we don't have to follow a target now)
            this.arrowEntity.setVelocity(this.arrowEntity.getVelocity().setY(this.shooterYDirection).normalize().multiply(this.speed)); // Normalize and multiply by initial magnitude
        }
    }

    @Override
    public boolean isValid() {
        if (this.arrowEntity.isInBlock()) return false; // Check to see if arrow is stuck in a block
        return super.isValid();
    }

}
