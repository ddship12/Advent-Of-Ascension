package net.tslat.aoa3.block.functional.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.tslat.aoa3.common.container.InfusionTableContainer;
import net.tslat.aoa3.item.misc.InfusionStone;
import net.tslat.aoa3.player.PlayerDataManager;
import net.tslat.aoa3.util.BlockUtil;
import net.tslat.aoa3.util.PlayerUtil;

public class InfusionTable extends Block {
	public InfusionTable() {
		super(new BlockUtil.CompactProperties(Material.STONE, MaterialColor.COLOR_PURPLE).stats(10f, 15f).get());
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player instanceof ServerPlayerEntity) {
			ItemStack stack = player.getItemInHand(hand);
			Item item = stack.getItem();

			if (item instanceof InfusionStone) {
				PlayerDataManager plData = PlayerUtil.getAdventPlayer((ServerPlayerEntity)player);
				InfusionStone stone = (InfusionStone)item;
				int count = stack.getCount();

				/*if (player.isCreative() || plData.stats().getLevel(Skills.INFUSION) >= stone.getLvl()) {
					plData.stats().addXp(Skills.INFUSION, stone.getXp() * count, false, false);
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), AoASounds.BLOCK_INFUSION_TABLE_CONVERT.get(), SoundCategory.BLOCKS, 1.0f, 1.0f);

					int chanceMod = plData.equipment().getCurrentFullArmourSet() == AdventArmour.Type.INFUSION ? 33 : 100;
					int powerStoneCount = 0;

					for (int i = 0; i < count; i++) {
						if (RandomUtil.oneInNChance(chanceMod))
							powerStoneCount++;
					}

					if (!player.isCreative()) {
						if (powerStoneCount > 0) {
							player.setItemInHand(hand, new ItemStack(stone.getPowerStone(), powerStoneCount));
						}
						else {
							player.setItemInHand(hand, ItemStack.EMPTY);
						}
					}
					else {
						ItemUtil.givePlayerItemOrDrop(player, new ItemStack(stone.getPowerStone(), powerStoneCount));
					}
				}*/ // TODO
			}
			else {
				InfusionTableContainer.openContainer((ServerPlayerEntity)player, pos);
			}
		}

		return ActionResultType.SUCCESS;
	}
}
