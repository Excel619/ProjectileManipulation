package com.gmail.excel8392.projectilemanipulation.entity;

import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * Represents an entity that is being manipulated by this plugin.
 */
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

}
