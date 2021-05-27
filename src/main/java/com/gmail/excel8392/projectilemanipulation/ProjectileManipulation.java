package com.gmail.excel8392.projectilemanipulation;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProjectileManipulation extends JavaPlugin implements Listener {

    private PaperCommandManager commandManager;
    private Map<Entity, ManipulatedProjectile> projectiles;

    @Override
    public void onEnable() {
        projectiles = new HashMap<>();
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ProjectileManipulationCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        // TODO: use https://github.com/lucko/helper to recalculate projectile targets without putting too much pressure on a single tick
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            try {
                Iterator<Map.Entry<Entity, ManipulatedProjectile>> iterator = projectiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Entity, ManipulatedProjectile> entry = iterator.next();
                    if (!entry.getValue().isValid()) {
                        this.projectiles.remove(entry.getKey());
                        continue;
                    }
                    entry.getValue().recalculateTarget();
                }
            } catch (ConcurrentModificationException ignored) {} // We can ignore as we may encounter CME because of Futures, but will fix itself next time
        }, 0L, 4L);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            try {
                Iterator<Map.Entry<Entity, ManipulatedProjectile>> iterator = projectiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Entity, ManipulatedProjectile> entry = iterator.next();
                    if (!entry.getValue().isValid()) {
                        this.projectiles.remove(entry.getKey());
                        continue;
                    }
                    entry.getValue().tickMovement();
                }
            } catch (ConcurrentModificationException ignored) {} // We can ignore as we may encounter CME because of Futures, but will fix itself next time
        }, 0L, 1L);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Egg){
            this.projectiles.put(
                    event.getEntity(),
                    new ManipulatedEgg((Egg) event.getEntity()));
        } else if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof LivingEntity) {
            this.projectiles.put(
                    event.getEntity(),
                    new ManipulatedEnderpearl((EnderPearl) event.getEntity(), (LivingEntity) event.getEntity().getShooter()));
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow) {
            this.projectiles.put(
                    event.getProjectile(),
                    new ManipulatedArrow(
                            (Arrow) event.getProjectile(),
                            event.getEntity().getEntityId(),
                            event.getEntity().getLocation().getDirection().normalize().getY()));
        }
    }

}
