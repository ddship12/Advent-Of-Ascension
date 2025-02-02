package net.tslat.aoa3.item.tool.shovel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.tslat.aoa3.common.registration.AoATags;
import net.tslat.aoa3.item.LootModifyingItem;
import net.tslat.aoa3.util.BlockUtil;
import net.tslat.aoa3.util.ItemUtil;
import net.tslat.aoa3.util.LocaleUtil;

import javax.annotation.Nullable;
import java.util.List;

public class SoulstoneShovel extends BaseShovel implements LootModifyingItem {
	public SoulstoneShovel() {
		super(ItemUtil.customItemTier(2000, 11.0f, 6.0f, 6, 10, null));
	}

	@Override
	public void doLootModification(List<ItemStack> existingLoot, LootContext lootContext) {
		BlockState harvestedBlock = getHarvestedBlock(lootContext);
		Block block = harvestedBlock.getBlock();

		if (block == Blocks.AIR || existingLoot.isEmpty() || !harvestedBlock.isToolEffective(ToolType.SHOVEL) || !lootContext.hasParam(LootParameters.THIS_ENTITY))
			return;

		if (!BlockUtil.isBlockTaggedAs(block, Tags.Blocks.DIRT, Tags.Blocks.SAND, Tags.Blocks.GRAVEL, AoATags.Blocks.GRASS) && !(block instanceof GrassBlock))
			return;

		Entity harvestingPlayer = lootContext.getParamOrNull(LootParameters.THIS_ENTITY);

		if (!(harvestingPlayer instanceof ServerPlayerEntity))
			return;

		ServerWorld world = lootContext.getLevel();
		BlockPos pos = new BlockPos(lootContext.getParamOrNull(LootParameters.ORIGIN));
		ItemStack blockDrop = ItemStack.EMPTY;
		Item blockItem = Item.byBlock(block);

		for (ItemStack stack : existingLoot) {
			if (stack.getItem() == blockItem) {
				blockDrop = stack;

				break;
			}
		}

		if (blockDrop == ItemStack.EMPTY)
			blockDrop = existingLoot.get(0);

		/*if (blockDrop != ItemStack.EMPTY && PlayerUtil.consumeResource((ServerPlayerEntity)harvestingPlayer, AoAResource.SOUL, 1, false)) {
			blockDrop.setCount(blockDrop.getCount() * 2);

			for (int i = 0; i < 5; i++) {
				world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 1, 0, 0, 0, 0);
			}
		}*/ // TODO
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(LocaleUtil.getFormattedItemDescriptionText(this, LocaleUtil.ItemDescriptionType.BENEFICIAL, 1));
	}
}
