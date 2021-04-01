package org.enginehub.worldgourd.bukkit;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.EditSession.Stage;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class WorldGourdPlugin extends JavaPlugin implements Listener {

    private static final String GOURDED_MESSAGE = LegacyComponentSerializer.legacy().serialize(TextComponent.builder()
        .append(TextComponent.of("Hey!", TextColor.RED, Sets.newHashSet(TextDecoration.BOLD)))
        .append(TextComponent.of(" Sorry, but gourds are protected by WorldGourd.", TextColor.GRAY)).build());

    private static final Set<Material> GOURDS = Collections.unmodifiableSet(EnumSet.of(
        Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN, Material.MELON,
        Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.MELON_STEM, Material.ATTACHED_MELON_STEM,
        Material.PUMPKIN_PIE, Material.MELON_SLICE));

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        WorldEdit.getInstance().getEventBus().register(this);
    }

    private boolean isGourd(Material material) {
        return GOURDS.contains(material);
    }

    private void sendMessage(Player player) {
        player.sendMessage(GOURDED_MESSAGE);
    }

    @Subscribe
    public void onEditSession(EditSessionEvent event) {
        if (event.getStage() == Stage.BEFORE_REORDER) return;
        event.setExtent(new GourdExtent(event.getExtent()));
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
    public void onItemConsumption(PlayerItemConsumeEvent event) {
        Material item = event.getItem().getType();
        if (isGourd(item)) {
            sendMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFrameRemoval(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame && isGourd(((ItemFrame) event.getEntity()).getItem().getType())) {
            if (event.getDamager() instanceof Player) {
                sendMessage((Player) event.getDamager());
            }
            event.setCancelled(true);
        }
    }

    public static class GourdExtent extends AbstractDelegateExtent {

        /**
         * Create a new instance.
         *
         * @param extent the extent
         */
        GourdExtent(Extent extent) {
            super(extent);
        }

        @Override
        public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
            BlockType blockType = getBlock(location).getBlockType();
            if (blockType == BlockTypes.PUMPKIN
                || blockType == BlockTypes.CARVED_PUMPKIN
                || blockType == BlockTypes.JACK_O_LANTERN
                || blockType == BlockTypes.MELON
                || blockType == BlockTypes.PUMPKIN_STEM
                || blockType == BlockTypes.ATTACHED_PUMPKIN_STEM
                || blockType == BlockTypes.MELON_STEM
                || blockType == BlockTypes.ATTACHED_MELON_STEM) {
                return false;
            }
            return super.setBlock(location, block);
        }
    }
}
