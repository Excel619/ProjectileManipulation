package com.gmail.excel8392.projectilemanipulation;

import co.aikar.commands.PaperCommandManager;
import com.gmail.excel8392.projectilemanipulation.command.ProjectileManipulationCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ProjectileManipulation extends JavaPlugin implements Listener {

    private static ProjectileManipulation instance;

    private PaperCommandManager commandManager; // Aikar Command Framework Command Manager for Paper (supports async)
    private ManipulatedProjectileHandler projectileHandler;

    @Override
    public void onEnable() {
        instance = this;

        // Register commands with command manager, nothing else needed for them to function
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new ProjectileManipulationCommand());

        // Initialize Projectile Handler, will automatically register projectile tasks and listeners
        this.projectileHandler = new ManipulatedProjectileHandler();
    }

    public PaperCommandManager getCommandManager() {
        return this.commandManager;
    }

    public ManipulatedProjectileHandler getProjectileHandler() {
        return this.projectileHandler;
    }

    public static ProjectileManipulation getInstance() {
        return instance;
    }

}
