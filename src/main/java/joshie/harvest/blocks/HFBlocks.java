package joshie.harvest.blocks;

import joshie.harvest.blocks.BlockFlower.FlowerType;
import joshie.harvest.blocks.tiles.TileMarker;
import joshie.harvest.core.helpers.generic.RegistryHelper;
import joshie.harvest.core.util.base.BlockHFBaseEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HFBlocks {
    //Fluid
    public static final Fluid GODDESS = registerFluid(new Fluid("hf_goddess_water", new ResourceLocation("blocks/hf_goddess_water_still"), new ResourceLocation("blocks/hf_goddess_water_flow")).setRarity(EnumRarity.RARE));
    public static final BlockFluidClassic GODDESS_WATER = new BlockGoddessWater(GODDESS).setUnlocalizedName("goddess.water");

    public static final BlockFlower FLOWERS = (BlockFlower) new BlockFlower().setUnlocalizedName("flowers");
    //Mine
    public static final BlockHFBaseEnum STONE = new BlockStone().setUnlocalizedName("stone");
    public static final BlockHFBaseEnum DIRT = new BlockDirt().setUnlocalizedName("dirt");
    //Misc
    public static final BlockHFBaseEnum WOOD_MACHINES = new BlockWood().setUnlocalizedName("woodware");
    public static final BlockPreview PREVIEW = (BlockPreview) new BlockPreview().setUnlocalizedName("preview");

    //Gathering
    public static final BlockHFBaseEnum GATHERING = new BlockGathering().setUnlocalizedName("gathering");

    public static void preInit() {
        GODDESS.setBlock(GODDESS_WATER);
        RegistryHelper.registerTiles(TileMarker.class);
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
            public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
                FlowerType type = FLOWERS.getEnumFromState(state);
                if (!type.isColored()) return -1;
                return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
            }
        }, FLOWERS);

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                return FLOWERS.getEnumFromMeta(stack.getItemDamage()).isColored() ? ColorizerFoliage.getFoliageColorBasic(): -1;
            }
        }, FLOWERS);
    }

    private static Fluid registerFluid(Fluid fluid) {
        FluidRegistry.registerFluid(fluid);
        return fluid;
    }
}