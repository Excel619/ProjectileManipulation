package com.gmail.excel8392.projectilemanipulation;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityUtil {

    /**
     * This function is a replacement for the World#getNearbyEntities
     * (which has quite a performance impact due to it performing additional bukkit API calls we don't need).
     * Instead, we use PaperLib's async chunk loading feature to minimize performance impact,
     * which is why this method returns a CompletableFuture<List<Entity>>.
     * Parameter "sort" determines if to sort by how close the entities are to the location.
     */
    public static CompletableFuture<List<Entity>> getNearbyEntities(Location location, double radius, boolean sort) {
        List<Entity> entities = new ArrayList<>();
        if (location.getWorld() == null) throw new IllegalArgumentException("Location world cannot be null!");
        World world = location.getWorld();
        CompletableFuture<List<Entity>> future = new CompletableFuture<>();

        // Because we can only grab one chunk at a time async, we wait until all chunks have been grabbed and then complete our future
        AtomicInteger totalChunks = new AtomicInteger(0);
        AtomicInteger chunksLoaded = new AtomicInteger(0);

        // Convert to chunk coordinates instead of block coordinates
        int smallX = (int) Math.floor((location.getX() - radius) / 16.0);
        int bigX = (int) Math.floor((location.getX() + radius) / 16.0);
        int smallZ = (int) Math.floor((location.getZ() - radius) / 16.0);
        int bigZ = (int) Math.floor((location.getZ() + radius) / 16.0);

        for (int x = smallX; x <= bigX; x++) {
            for (int z = smallZ; z <= bigZ; z++) {
                if (!world.isChunkLoaded(x, z)) continue;
                totalChunks.getAndIncrement();
                // We use PaperLib Async Chunk Loading to decrease performance impact
                PaperLib.getChunkAtAsync(world, x, z).thenAccept(chunk -> {
                    entities.addAll(Arrays.asList(chunk.getEntities()));
                    chunksLoaded.getAndIncrement();
                    if (chunksLoaded.get() != totalChunks.get()) return;
                    // We have finished loading all the chunks, we can complete the future
                    // We sort by how close each entity is to our location
                    if (sort) {
                        entities.sort(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(location)));
                    }
                    // Remove entities in the "chunk box" that aren't in our radius
                    Iterator<Entity> entityIterator = entities.iterator();
                    double radiusSquared = Math.pow(radius, 2);
                    while (entityIterator.hasNext()) {
                        if (entityIterator.next().getLocation().distanceSquared(location) > radiusSquared) {
                            entityIterator.remove();
                        }
                    }
                    future.complete(entities);
                });
            }
        }
        return future;
    }

    /**
     * Uses vector math to see if an entity is looking at a second entity.
     */
    public static boolean isLookingAt(LivingEntity entity1, LivingEntity entity2) {
        Vector difference = entity2.getEyeLocation().toVector().subtract(entity1.getEyeLocation().toVector()); // Get difference between entity one eye location and entity two eye location as vector
        double angleAccuracy = Math.max(0.14 - difference.length() * 0.0065, 0.03); // Arbitrary calculation to make distance scale with accuracy requirement
        return entity1.getEyeLocation().getDirection().angle(difference) < angleAccuracy;
        // Get angle between direction of entity one and the difference vector and compare with arbitrary double in radians indicating accuracy
        // TODO: This doesn't exactly work well with different sizes of mobs, but it is good enough for now
    }

    /**
     * Uses more efficient getNearbyEntities to get the entity the player is looking at.
     * Also the future's value can be null I don't remember how to mark that in java.
     * TODO: Change to scan only in chunks player is looking towards (using yaw), and scan gradually through each chunk.
     */
    public static CompletableFuture<LivingEntity> getLookingAt(LivingEntity entity, double radius) {
        CompletableFuture<LivingEntity> future = new CompletableFuture<>();
        getNearbyEntities(entity.getEyeLocation(), radius, true).thenAccept(entities -> {
            for (Entity nearbyEntity : entities) {
                if (!(nearbyEntity instanceof LivingEntity)) continue;
                if (isLookingAt(entity, (LivingEntity) nearbyEntity)) {
                    future.complete((LivingEntity) nearbyEntity);
                    return;
                }
            }
            future.complete(null);
        });
        return future;
    }

}
