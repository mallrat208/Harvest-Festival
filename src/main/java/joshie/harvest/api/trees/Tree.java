package joshie.harvest.api.trees;

import joshie.harvest.api.crops.Crop;
import joshie.harvest.api.crops.GrowthHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Tree extends Crop {
    public static final GrowthHandler TREE_GROWTH = new GrowthHandlerTree();
    private IBlockState log = Blocks.LOG.getDefaultState();
    private int maturity;

    /** Constructor for tree **/
    public Tree(ResourceLocation key) {
        super(key);
        setGrowthHandler(TREE_GROWTH);
        setNoWaterRequirements();
    }

    /** Returns the stage at which this tree is mature **/
    public int getStagesToMaturity() {
        return maturity;
    }

    /** Set the log state, call this before setStageLength
     *  @param logs    the state**/
    public Tree setLogs(IBlockState logs) {
        this.log = logs;
        return this;
    }

    /**
     * Returns what this crop drops when it's actually a log
     * **/
    public ItemStack getWoodStack() {
        return new ItemStack(log.getBlock(), 1, log.getBlock().getMetaFromState(log));
    }

    /**
     * Creates a state handler based on the passed in values
     */
    public Tree setStageLength(int seeds, int sapling, int juvenile, int maturity) {
        this.maturity = seeds + sapling + juvenile + maturity;
        setStages(this.maturity - maturity);
        setRegrow(this.maturity - 3);
        setStateHandler(new StateHandlerTree(log, seeds, sapling, juvenile));
        return this;
    }

    /**
     * Set how many days it takes fruit to regrow
     * **/
    public Tree setFruitRegrow(int regrow) {
        setRegrow(getStagesToMaturity() - regrow);
        return this;
    }
}