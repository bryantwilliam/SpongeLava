package com.gmail.gogobebe2.spongelava;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpongeLava extends JavaPlugin implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpongePlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.SPONGE) && event.getItemInHand().getDurability() != 1) {
            Block sponge = event.getBlockPlaced();
            for (int x = sponge.getX() - 7; x < sponge.getX() + 7; x++) {
                for (int y = sponge.getY() - 7; y < sponge.getY() + 7; y++) {
                    for (int z = sponge.getZ() - 7; y < sponge.getZ() + 7; y++) {
                        Block block = sponge.getLocation().getWorld().getBlockAt(x, y, z);
                        if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA))
                        {
                            block.breakNaturally();
                        }
                    }
                }
            }
        }
    }
}
