package com.gmail.gogobebe2.spongelava;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
            Block lava = getNearest(sponge, Material.LAVA);
            while (lava != null) {
                lava.setType(Material.AIR);
                lava = getNearest(sponge, Material.LAVA);
            }
        }
    }

    private Block getNearest(Block block, Material material) {
        for (int x = block.getX() - 3; x < block.getX() + 3; x++) {
            for (int y = block.getY() - 3; y < block.getY() + 3; y++) {
                for (int z = block.getZ() - 3; z < block.getZ() + 3; z++) {
                    Block near = block.getWorld().getBlockAt(x, y, z);
                    if (near.getType().equals(material)) {
                        return near;
                    }
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLavaFlow(BlockFromToEvent event) {
        Block lava = event.getBlock();
        if (lava.getType().equals(Material.LAVA) || lava.getType().equals(Material.STATIONARY_LAVA)) {
            Block sponge = getNearest(lava, Material.SPONGE);
            if (sponge != null) {
                clearSurroundingLava(sponge);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (blockPlaced.getType().equals(Material.SPONGE) && event.getItemInHand().getDurability() != 1) {
            Block sponge = event.getBlockPlaced();
            clearSurroundingLava(sponge);
        } else if (blockPlaced.getType() == Material.LAVA || blockPlaced.getType() == Material.STATIONARY_LAVA) {
            Block sponge = getNearest(blockPlaced, Material.SPONGE);
            if (sponge != null) {
                clearSurroundingLava(sponge);
                event.setCancelled(true);
            }
        }
    }
}

