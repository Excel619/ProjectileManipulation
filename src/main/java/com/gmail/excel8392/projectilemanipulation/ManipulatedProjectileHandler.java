package com.gmail.excel8392.projectilemanipulation;

import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedProjectile;
import com.gmail.excel8392.projectilemanipulation.listener.ProjectileShootListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Handles logic for all manipulated projectiles
public class ManipulatedProjectileHandler {

    private final Map<Entity, ManipulatedProjectile> projectiles = new HashMap<>(); // Map of active projectiles in the world

    public ManipulatedProjectileHandler() {

        // Register the task that constantly scans for new projectile targets
        // TODO: use https://github.com/lucko/helper to recalculate projectile targets without putting too much pressure on a single tick
        Bukkit.getScheduler().runTaskTimer(ProjectileManipulation.getInstance(), () -> {
            try {
                Iterator<Map.Entry<Entity, ManipulatedProjectile>> iterator = projectiles.entrySet().iterator();
                while (iterator.hasNext()) { // We use while to avoid CME exceptions caused by modification inside the loop
                    Map.Entry<Entity, ManipulatedProjectile> entry = iterator.next();
                    if (!entry.getValue().isValid()) { // Remove dead projectiles
                        this.projectiles.remove(entry.getKey());
                        continue;
                    }
                    entry.getValue().recalculateTarget();
                }
            } catch (ConcurrentModificationException ignored) {} // We can ignore as we may encounter CME because of Futures, but will fix itself next time
        }, 0L, 4L); // Repeat less often, as we don't need to check for new targets so quickly


        // Register the task that constantly updates the projectile's movement
        Bukkit.getScheduler().runTaskTimer(ProjectileManipulation.getInstance(), () -> {
            try {
                Iterator<Map.Entry<Entity, ManipulatedProjectile>> iterator = projectiles.entrySet().iterator();
                while (iterator.hasNext()) {// We use while to avoid CME exceptions caused by modification inside the loop
                    Map.Entry<Entity, ManipulatedProjectile> entry = iterator.next();
                    if (!entry.getValue().isValid()) { // Remove dead projectiles
                        this.projectiles.remove(entry.getKey());
                        continue;
                    }
                    entry.getValue().tickMovement();
                }
            } catch (ConcurrentModificationException ignored) {} // We can ignore as we may encounter CME because of Futures, but will fix itself next time
        }, 0L, 1L); // Repeat often, to make movement smooth


        Bukkit.getPluginManager().registerEvents(new ProjectileShootListener(), ProjectileManipulation.getInstance());
    }

    public Map<Entity, ManipulatedProjectile> getProjectiles() {
        return this.projectiles;
    }

    public void addProjectile(ManipulatedProjectile projectile) {
        this.projectiles.put(projectile.getEntity(), projectile);
    }

}
