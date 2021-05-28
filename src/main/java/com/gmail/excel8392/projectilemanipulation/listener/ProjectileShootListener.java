package com.gmail.excel8392.projectilemanipulation.listener;

import com.gmail.excel8392.projectilemanipulation.ProjectileManipulation;
import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedArrow;
import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedEgg;
import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedEnderpearl;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileShootListener implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Egg) {
            ProjectileManipulation.getInstance().getProjectileHandler().addProjectile(new ManipulatedEgg((Egg) event.getEntity()));
        } else if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof LivingEntity) {
            // Shooter must be an entity (not dispenser) so that we can determine where it was looking.
            ProjectileManipulation.getInstance().getProjectileHandler().addProjectile(new ManipulatedEnderpearl(
                    (EnderPearl) event.getEntity(),
                    (LivingEntity) event.getEntity().getShooter()));
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        // We use bow shoot event instead so that it fires only when living entities shoot arrow
        if (event.getProjectile() instanceof Arrow) {
            ProjectileManipulation.getInstance().getProjectileHandler().addProjectile(new ManipulatedArrow(
                    (Arrow) event.getProjectile(),
                    event.getEntity().getEntityId(),
                    event.getEntity().getLocation().getDirection().normalize().getY())); // Normalize the vector so that the Y is in terms of a unit vector
        }
    }

}
