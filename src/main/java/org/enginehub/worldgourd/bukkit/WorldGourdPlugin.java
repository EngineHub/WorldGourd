package org.enginehub.worldgourd.bukkit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class WorldGourdPlugin extends JavaPlugin implements Listener {

    private static final String GOURDED_MESSAGE = "§c§lHey! §r§7Sorry, but gourds are protected by WorldGourd.";

    private static final Set<Material> GOURDS = Collections.unmodifiableSet(EnumSet.of(
        Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN, Material.MELON,
        Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.MELON_STEM, Material.ATTACHED_MELON_STEM,
        Material.PUMPKIN_PIE, Material.MELON_SLICE));

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            WorldEditEditSessionListener.register();
        }
    }

    private boolean isGourd(Material material) {
        return GOURDS.contains(material);
    }

    private void sendMessage(Player player) {
        player.sendMessage(GOURDED_MESSAGE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isGourd(event.getBlock().getType())) {
            sendMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isGourd(block.getType())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (isGourd(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(next -> isGourd(next.getType()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFace(BlockFadeEvent event) {
        if (isGourd(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (isGourd(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && isGourd(event.getClickedBlock().getType())) {
            event.setCancelled(true);
            sendMessage(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerArmorStand(PlayerArmorStandManipulateEvent event) {
        ItemStack armorStandItem = event.getArmorStandItem();
        if (armorStandItem != null && isGourd(armorStandItem.getType())) {
            sendMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerShearSnowman(PlayerShearEntityEvent event) {
        if (event.getEntity() instanceof Snowman) {
            sendMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsumption(PlayerItemConsumeEvent event) {
        Material item = event.getItem().getType();
        if (isGourd(item)) {
            sendMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFrameDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        boolean protectItemFrame = entity instanceof ItemFrame
            && isGourd(((ItemFrame) entity).getItem().getType());
        boolean protectArmorStand = entity instanceof ArmorStand
            && Arrays.stream(((ArmorStand) entity).getEquipment().getArmorContents())
            .anyMatch(is -> is != null && isGourd(is.getType()));

        if (protectItemFrame || protectArmorStand) {
            if (event.getDamager() instanceof Player) {
                sendMessage((Player) event.getDamager());
            }
            event.setCancelled(true);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onFrameRemoval(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame && isGourd(((ItemFrame) event.getEntity()).getItem().getType())) {
            if (event instanceof HangingBreakByEntityEvent) {
                HangingBreakByEntityEvent byEntityEvent = (HangingBreakByEntityEvent) event;
                if (byEntityEvent.getRemover() instanceof Player) {
                    sendMessage((Player) byEntityEvent.getRemover());
                }
            }
            event.setCancelled(true);
        }
    }
}
