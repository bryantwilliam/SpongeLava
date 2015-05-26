package com.gmail.gogobebe2.spongelava;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class SpongeLava extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Starting up SpongeLava. If you need me to update this plugin, email at gogobebe2@gmail.com");
        Bukkit.getPluginManager().registerEvents(this, this);
        int timerIncrementer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (getConfig().isSet("SPONGES")) {
                    Set<String> spongeIDs = getConfig().getConfigurationSection("SPONGES").getKeys(false);
                    if (!spongeIDs.isEmpty()) {
                        for (String id : spongeIDs) {
                            clearSurroundingLava(loadSponge(Integer.parseInt(id)));
                        }
                    }
                }
            }
        }, 0L, 5L);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling SpongeLava. If you need me to update this plugin, email at gogobebe2@gmail.com");
        reloadConfig();
    }

    private boolean isTouchingLava(Block block) {
        for (BlockFace face : new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.EAST, BlockFace.WEST}) {
            Block relative = block.getRelative(face);
            if (relative.getType().equals(Material.LAVA) || relative.getType().equals(Material.STATIONARY_LAVA)) {
                return true;
            }
        }
        return false;
    }

    private void clearSurroundingLava(Block sponge) {
        if (isTouchingLava(sponge)) {
            for (int x = sponge.getX() - 7; x < sponge.getX() + 7; x++) {
                for (int y = sponge.getY() - 7; y < sponge.getY() + 7; y++) {
                    for (int z = sponge.getZ() - 7; z < sponge.getZ() + 7; z++) {
                        Block block = sponge.getWorld().getBlockAt(x, y, z);
                        if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA)) {
                            block.breakNaturally();
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpongePlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.SPONGE) && event.getItemInHand().getDurability() != 1) {
            Block sponge = event.getBlockPlaced();
            saveSponge(sponge);
            clearSurroundingLava(sponge);
        }
    }

    private int getNextID() {
        int greatest = 0;
        Set<String> spongeIDs = getConfig().getConfigurationSection("SPONGES").getKeys(false);
        if (!spongeIDs.isEmpty()) {
            for (String id : spongeIDs) {
                int ID = Integer.parseInt(id);
                if (ID > greatest) {
                    greatest = ID;
                }
            }
        }
        return ++greatest;
    }

    private void saveSponge(Block sponge) {
        World WORLD = sponge.getWorld();
        int X = sponge.getX();
        int Y = sponge.getY();
        int Z = sponge.getZ();
        int ID = getNextID();
        getConfig().set("SPONGES." + ID + ".WORLD", WORLD);
        getConfig().set("SPONGES." + ID + ".X", X);
        getConfig().set("SPONGES." + ID + ".Y", Y);
        getConfig().set("SPONGES." + ID + ".Z", Z);
        saveConfig();
    }

    private Block loadSponge(int ID) {
        World WORLD = Bukkit.getWorld(getConfig().getString("SPONGES." + ID + ".WORLD"));
        int X = getConfig().getInt("SPONGES." + ID + ".X");
        int Y = getConfig().getInt("SPONGES." + ID + ".Y");
        int Z = getConfig().getInt("SPONGES." + ID + ".Z");
        return new Location(WORLD, X, Y, Z).getBlock();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpongeBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.SPONGE)) {
            Set<String> spongeIDs = getConfig().getConfigurationSection("SPONGES").getKeys(false);
            if (!spongeIDs.isEmpty()) {
                for (String id : getConfig().getConfigurationSection("SPONGES").getKeys(false)) {
                    if (loadSponge(Integer.parseInt(id)).equals(block)) {
                        getConfig().set("SPONGES." + id, null);
                        saveConfig();
                        return;
                    }
                }
            }
        }
    }
}

