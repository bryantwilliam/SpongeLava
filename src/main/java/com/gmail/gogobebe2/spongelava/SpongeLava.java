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
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class SpongeLava extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Starting up SpongeLava. If you need me to update this plugin, email at gogobebe2@gmail.com");
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
        if (!getConfig().getBoolean("Needs to touch lava to clear") || isTouchingLava(sponge)) {
            checkNear(sponge, new Material[]{Material.LAVA, Material.STATIONARY_LAVA}, true);
        }
    }

    private boolean checkNear(Block block, Material[] materials, boolean breakOnFind) {
        boolean broke = false;
        for (int x = block.getX() - 7; x < block.getX() + 7; x++) {
            for (int y = block.getY() - 7; y < block.getY() + 7; y++) {
                for (int z = block.getZ() - 7; z < block.getZ() + 7; z++) {
                    Block near = block.getWorld().getBlockAt(x, y, z);
                    for (Material material : materials) {
                        if (near.getType().equals(material)) {
                            if (breakOnFind) {
                                near.breakNaturally();
                                broke = true;
                            }
                            else {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return broke;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLavaFlow(BlockFromToEvent event) {
        Block lava = event.getBlock();
        if (lava.getType().equals(Material.LAVA) || lava.getType().equals(Material.STATIONARY_LAVA)) {
            if (getConfig().isSet("SPONGES")) {
                Set<String> spongeIDs = getConfig().getConfigurationSection("SPONGES").getKeys(false);
                for (String id : spongeIDs) {
                    clearSurroundingLava(loadSponge(Integer.parseInt(id)));
                    if (checkNear(lava, new Material[]{Material.SPONGE}, false)) {
                        event.setCancelled(true);
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
        if (getConfig().isSet("SPONGES")) {
            Set<String> spongeIDs = getConfig().getConfigurationSection("SPONGES").getKeys(false);
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
        getConfig().set("SPONGES." + ID + ".WORLD", WORLD.getName());
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
            if (getConfig().isSet("SPONGES")) {
                Set<String> spongeIDs = getConfig().getConfigurationSection("SPONGES").getKeys(false);
                for (String id : spongeIDs) {
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

