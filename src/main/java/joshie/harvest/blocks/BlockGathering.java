package joshie.harvest.blocks;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.core.ITiered.ToolTier;
import joshie.harvest.blocks.BlockGathering.GatheringType;
import joshie.harvest.core.HFTab;
import joshie.harvest.core.helpers.WorldHelper;
import joshie.harvest.core.lib.CreativeSort;
import joshie.harvest.core.util.base.BlockHFEnum;
import joshie.harvest.items.HFItems;
import joshie.harvest.mining.MiningHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static joshie.harvest.api.core.ITiered.ToolTier.*;
import static joshie.harvest.blocks.BlockGathering.GatheringType.*;

public class BlockGathering extends BlockHFEnum<BlockGathering, GatheringType> {
    public static final PropertyBool WINTER = PropertyBool.create("winter");

    public enum GatheringType implements IStringSerializable {
        BRANCH_SMALL(false), BRANCH_MEDIUM(false), BRANCH_LARGE(false), STUMP_SMALL(false), STUMP_MEDIUM(false), STUMP_LARGE(false),
        ROCK_SMALL(true), ROCK_MEDIUM(true), ROCK_LARGE(true), ORE_SMALL(true), ORE_MEDIUM(true), ORE_LARGE(true);

        private final boolean isRock;

        GatheringType(boolean isRock) {
            this.isRock = isRock;
        }

        @Override
        public String getName() {
            return toString().toLowerCase();
        }
    }

    public BlockGathering() {
        super(Material.WOOD, GatheringType.class, HFTab.GATHERING);
        setHardness(1.5F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        if(property == null) return new BlockStateContainer(this, temporary, WINTER);
        return new BlockStateContainer(this, property, WINTER);
    }

    @Override
    public String getToolType(GatheringType type) {
        return type.isRock ? "pickaxe" : "axe";
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        GatheringType type = getEnumFromState(state);
        switch (type) {
            case BRANCH_SMALL:
                return new AxisAlignedBB(0D, 0D, 0D, 1D, 0.6D, 1D);
            default:
                return FULL_BLOCK_AABB;
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Season season = HFApi.calendar.getDate(WorldHelper.getWorld(world)).getSeason();
        if (season == Season.WINTER) {
            return state.withProperty(WINTER, true);
        } else return state.withProperty(WINTER, false);
    }

    public ItemStack getDrop(World world, BlockPos pos, EntityPlayer player, GatheringType type, float luck) {
        switch (type) {
            case BRANCH_SMALL: return new ItemStack(Blocks.LOG, 1);
            case BRANCH_MEDIUM: return new ItemStack(Blocks.LOG, 2);
            case BRANCH_LARGE: return new ItemStack(Blocks.LOG, 4);
            case STUMP_SMALL: return new ItemStack(Blocks.LOG, 3);
            case STUMP_MEDIUM: return new ItemStack(Blocks.LOG, 6);
            case STUMP_LARGE: return new ItemStack(Blocks.LOG, 12);
            case ROCK_SMALL: return new ItemStack(Blocks.STONE, 1);
            case ROCK_MEDIUM: return new ItemStack(Blocks.STONE, 4);
            case ROCK_LARGE: return new ItemStack(Blocks.STONE, 4);
            case ORE_SMALL: return MiningHelper.getLoot(world, pos, player, 0F + luck);
            case ORE_MEDIUM: return MiningHelper.getLoot(world, pos, player, 1F + luck);
            case ORE_LARGE: return MiningHelper.getLoot(world, pos, player, 3F + luck);
            default: return null;
        }
    }

    public void smashBlock(EntityPlayer player, World world, BlockPos pos, IBlockState state, ItemStack stack, ToolTier tier) {
        if (!world.isRemote) {
            GatheringType type = getEnumFromState(state);
            boolean smashed = false;
            float luck = 0F;
            if (stack.getItem() == HFItems.AXE && !type.isRock) {
                if (tier.isGreaterThanOrEqualTo(BASIC) && type == BRANCH_SMALL) smashed = true;
                if (tier.isGreaterThanOrEqualTo(COPPER) && type == BRANCH_MEDIUM) smashed = true;
                if (tier.isGreaterThanOrEqualTo(SILVER) && type == STUMP_SMALL) smashed = true;
                if (tier.isGreaterThanOrEqualTo(GOLD) && type == BRANCH_LARGE) smashed = true;
                if (tier.isGreaterThanOrEqualTo(MYSTRIL) && type == STUMP_MEDIUM) smashed = true;
                if (tier.isGreaterThanOrEqualTo(CURSED) && type == STUMP_LARGE) smashed = true;
            } else if (stack.getItem() == HFItems.HAMMER) {
                smashed = true;
                luck = tier.ordinal() * 0.25F;
            }

            if (smashed) {
                world.setBlockToAir(pos);
                ItemStack drop = getDrop(world, pos, player, type, luck);
                if (drop != null) {
                    spawnAsEntity(world, pos, drop);
                }
            }
        }
    }

    @Override
    protected boolean isValidTab(CreativeTabs tab, GatheringType e) {
        if (e == ORE_SMALL || e == ORE_MEDIUM || e == ORE_LARGE) return tab == HFTab.MINING;
        else return tab == getCreativeTabToDisplayOn();
    }

    @Override
    public int getSortValue(ItemStack stack) {
        GatheringType type = getEnumFromMeta(stack.getItemDamage());
        if (type == BRANCH_SMALL || type == BRANCH_MEDIUM || type == BRANCH_LARGE) {
            return CreativeSort.TOOLS - 4;
        } else if (type == STUMP_SMALL || type == STUMP_MEDIUM || type == STUMP_LARGE) {
            return CreativeSort.TOOLS - 3;
        } return CreativeSort.TOOLS - 2;
    }
}