package com.gmail.excel8392.projectilemanipulation;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public abstract class ManipulatedProjectile implements ManipulatedEntity {

    private final Entity entity;

    public ManipulatedProjectile(@Nullable Entity entity) {
        this.entity = entity;
    }

    @Nullable protected Entity target = null;

    @Override
    public Entity getEntity(){
        return this.entity;
    }

    @Override
    public boolean isValid() {
        if (this.entity == null) return false;
        return !this.entity.isDead();
    }

    @Nullable
    public Location getTarget() {
        if (this.target == null) return null;
        return this.target.getLocation();
    }

    /**
     *  Recalculates the projectile's target
     */
    public abstract void recalculateTarget();

}
