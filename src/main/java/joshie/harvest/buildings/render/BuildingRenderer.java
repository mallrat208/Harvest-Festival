package joshie.harvest.buildings.render;

import joshie.harvest.buildings.Building;
import joshie.harvest.buildings.placeable.blocks.PlaceableBlock;
import joshie.harvest.core.util.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuildingRenderer {
    private final Direction direction;
    private final Building building;
    private VertexBuffer buffer;
    private BlockPos pos;

    public BuildingRenderer(World world, BuildingKey key) {
        this.direction = Direction.withMirrorAndRotation(key.getMirror(), key.getRotation());
        this.building = key.getBuilding();
        this.pos = key.getPos();
        this.buffer = new VertexBuffer(262144);
        this.buffer.begin(7, DefaultVertexFormats.BLOCK);
        for (PlaceableBlock placeable : building.getPreviewList()) {
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(placeable.getTransformedState(direction), placeable.transformBlockPos(direction), world, buffer);
        }
    }

    public Building getBuilding() {
        return building;
    }

    public VertexBuffer getBuffer() {
        return buffer;
    }

    public BlockPos getPos() {
        return pos;
    }
}