package org.enginehub.worldgourd.bukkit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

public class WorldEditEditSessionListener {
    static void register() {
        WorldEdit.getInstance().getEventBus().register(new WorldEditEditSessionListener());
    }

    @Subscribe
    public void onEditSession(EditSessionEvent event) {
        if (event.getStage() == EditSession.Stage.BEFORE_REORDER) return;
        event.setExtent(new GourdExtent(event.getExtent()));
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
