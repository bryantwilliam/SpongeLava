package com.gmail.gogobebe2.spongelava;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SpongeLava extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Starting up SpongeLava. If you need me to update this plugin, email at gogobebe2@gmail.com");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling SpongeLava. If you need me to update this plugin, email at gogobebe2@gmail.com");
    }

    private void clearSurroundingLava(Block sponge) {
        List<Block> lavas = getNearest(sponge, Material.LAVA);
        if (!lavas.isEmpty()) {
            for (Block lava : lavas) {
                lava.setType(Material.AIR);
            }
        }
        lavas = getNearest(sponge, Material.STATIONARY_LAVA);
        if (!lavas.isEmpty()) {
            for (Block lava : lavas) {
                lava.setType(Material.AIR);
            }
        }
    }

    private List<Block> getNearest(Block block, Material material) {
        List<Block> blocks = new ArrayList<>();
        for (int x = block.getX() - 3; x < block.getX() + 3; x++) {
            for (int y = block.getY() - 3; y < block.getY() + 3; y++) {
                for (int z = block.getZ() - 3; z < block.getZ() + 3; z++) {
                    Block near = block.getWorld().getBlockAt(x, y, z);
                    if (near.getType() == material) {
                        blocks.add(near);
                    }
                }
            }
        }
        return blocks;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLavaFlow(BlockFromToEvent event) {
        Block lava = event.getBlock();
        if (lava.getType().equals(Material.LAVA) || lava.getType().equals(Material.STATIONARY_LAVA)) {
            List<Block> sponges = getNearest(lava, Material.SPONGE);
            if (!sponges.isEmpty()) {
                event.setCancelled(true);
                for (Block sponge : sponges) {
                    clearSurroundingLava(sponge);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (blockPlaced.getType().equals(Material.SPONGE)) {
            Block sponge = event.getBlockPlaced();
            clearSurroundingLava(sponge);
        } else if (blockPlaced.getType() == Material.LAVA || blockPlaced.getType() == Material.STATIONARY_LAVA) {
            List<Block> sponges = getNearest(blockPlaced, Material.SPONGE);
            if (!sponges.isEmpty()) {
                event.setCancelled(true);
                for (Block sponge : sponges) {
                    clearSurroundingLava(sponge);
                }
            }

        }
    }
}

