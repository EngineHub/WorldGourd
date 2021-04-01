package org.enginehub.worldgourd.bukkit;

import com.google.common.collect.Sets;
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
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldGourdPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        WorldEdit.getInstance().getEventBus().register(this);
    }

    private boolean isGourd(Material material) {
        return material == Material.PUMPKIN || material == Material.CARVED_PUMPKIN || material == Material.JACK_O_LANTERN;
    }

    private void sendMessage(Player player) {
        player.sendMessage(LegacyComponentSerializer.INSTANCE.serialize(TextComponent.of("").append(TextComponent.of("Hey!",
            TextColor.RED, Sets.newHashSet(TextDecoration.BOLD)))
            .append(TextComponent.of(" Sorry, but pumpkins are protected by WorldGourd.", TextColor.GRAY))));
    }

    @Subscribe
    public void onEditSession(EditSessionEvent event) {
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

    public static class GourdExtent extends AbstractDelegateExtent {

        /**
         * Create a new instance.
         *
         * @param extent the extent
         */
        protected GourdExtent(Extent extent) {
            super(extent);
        }

        @Override
        public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
            BlockState existing = getBlock(location);
            if (existing.getBlockType() == BlockTypes.PUMPKIN
                || existing.getBlockType() == BlockTypes.CARVED_PUMPKIN
                || existing.getBlockType() == BlockTypes.JACK_O_LANTERN) {
                return false;
            }
            return super.setBlock(location, block);
        }
    }
}
