package com.gmail.excel8392.projectilemanipulation;

import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public interface ManipulatedEntity {

    /**
     * Gets the bukkit entity that this projectile represents
     */
    @Nullable
    Entity getEntity();

    /**
     * Recalculates movement for manipulated entity
     */
    void tickMovement();

    /**
     * Returns false if projectile has been destroyed/invalidated
     */
    boolean isValid();

    /**
     * Gets the unique manipulated-projectile identifier for all projectiles of this type
     */
    String getIdentifier();

}
