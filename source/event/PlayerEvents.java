package net.tslat.aoa3.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tslat.aoa3.advent.AdventOfAscension;
import net.tslat.aoa3.advent.Logging;
import net.tslat.aoa3.common.registration.AoADimensions;
import net.tslat.aoa3.common.registration.AoAItems;
import net.tslat.aoa3.common.registration.AoAWeapons;
import net.tslat.aoa3.config.AoAConfig;
import net.tslat.aoa3.data.server.AoASkillReqReloadListener;
import net.tslat.aoa3.event.dimension.LelyetiaEvents;
import net.tslat.aoa3.event.dimension.LunalusEvents;
import net.tslat.aoa3.event.dimension.VoxPondsEvents;
import net.tslat.aoa3.item.armour.AdventArmour;
import net.tslat.aoa3.item.misc.ReservedItem;
import net.tslat.aoa3.item.misc.summoning.BossSpawningItem;
import net.tslat.aoa3.item.tool.misc.ExpFlask;
import net.tslat.aoa3.player.PlayerDataManager;
import net.tslat.aoa3.util.*;
import org.apache.logging.log4j.Level;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = AdventOfAscension.MOD_ID)
public class PlayerEvents {
	@SubscribeEvent
	public static void onPlayerTick(final TickEvent.PlayerTickEvent ev) {
		if (ev.phase == TickEvent.Phase.END) {
			if (WorldUtil.isWorld(ev.player.level, AoADimensions.LELYETIA.key)) {
				LelyetiaEvents.doPlayerTick(ev.player);
			}
			else if (WorldUtil.isWorld(ev.player.level, AoADimensions.VOX_PONDS.key)) {
				if (!ev.player.level.isClientSide())
					VoxPondsEvents.doPlayerTick(ev.player);
			}
			else if (WorldUtil.isWorld(ev.player.level, AoADimensions.LUNALUS.key)) {
				LunalusEvents.doPlayerTick(ev.player);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerJump(final LivingEvent.LivingJumpEvent ev) {
		if (WorldUtil.isWorld(ev.getEntity().level, AoADimensions.LUNALUS.key) && ev.getEntity() instanceof PlayerEntity)
			LunalusEvents.doPlayerJump((PlayerEntity)ev.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerHit(final LivingAttackEvent ev) {
		if (ev.getEntityLiving() instanceof ServerPlayerEntity && ev.getEntityLiving().getHealth() - ev.getAmount() <= 0 && ev.getEntityLiving().level.getLevelData().isHardcore())
			ReservedItem.handlePlayerDeath((ServerPlayerEntity)ev.getEntityLiving());
	}

	@SubscribeEvent
	public static void onEnderPearl(final EntityTeleportEvent.EnderPearl ev) {
		World world = ev.getPlayer().level;

		if (!world.isClientSide() && world.dimension().location().getNamespace().equals(AdventOfAscension.MOD_ID) && ev.getTargetY() >= world.dimensionType().logicalHeight())
			ev.setCanceled(true);
	}

	@SubscribeEvent
	public static void onPlayerHurt(final LivingHurtEvent ev) {
		if (!ev.getEntityLiving().level.isClientSide) {
			if (ev.getEntityLiving() instanceof ServerPlayerEntity) {
				ServerPlayerEntity pl = (ServerPlayerEntity)ev.getEntityLiving();

				Entity creeper = ev.getSource().getDirectEntity();

				if (pl.getHealth() > 0 && ev.getSource().isExplosion() && creeper instanceof CreeperEntity) {
					if ((!pl.level.getEntitiesOfClass(TNTEntity.class, creeper.getBoundingBox().inflate(3)).isEmpty() || !pl.level.getEntitiesOfClass(TNTEntity.class, pl.getBoundingBox().inflate(3)).isEmpty()) && ItemUtil.findInventoryItem(pl, new ItemStack(AoAItems.BLANK_REALMSTONE.get()), true, 1))
						ItemUtil.givePlayerItemOrDrop(pl, new ItemStack(AoAItems.CREEPONIA_REALMSTONE.get()));
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerFall(final LivingFallEvent ev) {
		if (ev.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)ev.getEntityLiving();

			if (ev.getDistance() > 25 && ev.getDamageMultiplier() > 0 && ItemUtil.findInventoryItem(player, new ItemStack(AoAItems.BLANK_REALMSTONE.get()), true, 1))
				ItemUtil.givePlayerItemOrDrop(player, new ItemStack(AoAItems.LELYETIA_REALMSTONE.get()));

			if (WorldUtil.isWorld(player.level, AoADimensions.LUNALUS.key))
				LunalusEvents.doPlayerLanding(player, ev);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEntityDeath(final LivingDeathEvent ev) {
		if (!ev.getEntity().level.isClientSide) {
			if (ev.getEntity() instanceof ServerPlayerEntity) {
				ReservedItem.handlePlayerDeath((ServerPlayerEntity)ev.getEntity());
			}
			else if (ev.getSource().getEntity() instanceof ServerPlayerEntity) {
				if (WorldUtil.isWorld(ev.getEntity().level, AoADimensions.DEEPLANDS.key)) {
					if (ev.getEntityLiving() instanceof FlyingEntity)
						ev.getEntityLiving().spawnAtLocation(new ItemStack(AoAItems.MUSIC_DISC_CAVERNS.get()), 0.5f);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockBreak(final BlockEvent.BreakEvent ev) {
		PlayerEntity pl = ev.getPlayer();
		BlockPos pos = ev.getPos();

		if (pl.isCreative())
			return;

		if (pl instanceof ServerPlayerEntity) {
			BlockState block = pl.level.getBlockState(pos);

			if (!AoASkillReqReloadListener.canBreakBlock(PlayerUtil.getAdventPlayer((ServerPlayerEntity)pl), ev.getState().getBlock())) {
				ev.setCanceled(true);

				return;
			}

			if (block.is(BlockTags.CROPS) && RandomUtil.oneInNChance(2500))
				pl.spawnAtLocation(new ItemStack(AoAWeapons.GARDENER.get()), 0);

			if (block.is(Tags.Blocks.ORES) && pos.getY() <= 5 && ItemUtil.findInventoryItem(pl, new ItemStack(AoAItems.BLANK_REALMSTONE.get()), true, 1))
				ItemUtil.givePlayerItemOrDrop(pl, new ItemStack(AoAItems.DEEPLANDS_REALMSTONE.get()));
		}
	}

	@SubscribeEvent
	public static void onBlockPlace(final BlockEvent.EntityPlaceEvent ev) {
		if (ev.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity pl = (ServerPlayerEntity)ev.getEntity();

			if (!AoASkillReqReloadListener.canPlaceBlock(PlayerUtil.getAdventPlayer(pl), ev.getState().getBlock())) {
				ev.setCanceled(true);

				return;
			}

			if (PlayerUtil.isWearingFullSet(pl, AdventArmour.Type.HYDRANGIC)) {
				if (ev.getPlacedBlock().getBlock() instanceof IGrowable && BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), ev.getEntity().level, ev.getPos(), pl)) {
					ev.getWorld().levelEvent(2005, ev.getPos(), 0);
					pl.inventory.hurtArmor(DamageSource.GENERIC, 16);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBlockInteract(final PlayerInteractEvent.RightClickBlock ev) {
		if (ev.getPlayer() instanceof ServerPlayerEntity) {
			if (!AoASkillReqReloadListener.canInteractWith(PlayerUtil.getAdventPlayer((ServerPlayerEntity)ev.getPlayer()), ev.getWorld().getBlockState(ev.getPos()).getBlock())) {
				ev.setCanceled(true);

				return;
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent ev) {
		if (ev.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity pl = (ServerPlayerEntity)ev.getEntityLiving();
			UUID uuid = pl.getGameProfile().getId();
			String msg = null;

			if (uuid.compareTo(UUID.fromString("2459b511-ca45-43d8-808d-f0eb30a63be4")) == 0) {
				msg = TextFormatting.DARK_RED + "It begins...Is this the end?";

				((ServerWorld)pl.level).sendParticles(ParticleTypes.LARGE_SMOKE, pl.getX(), pl.getY() + 0.2d, pl.getZ(), 16, RandomUtil.randomValueUpTo(0.1f) - 0.05d, RandomUtil.randomValueUpTo(0.1f) - 0.05d, RandomUtil.randomValueUpTo(0.1f) - 0.05d, 1);
			}
			else if (AoAHaloUtil.isCrazyDonator(uuid)) {
				msg = TextFormatting.LIGHT_PURPLE + "They approach. Tremble before them.";
			}

			if (msg != null)
				pl.getServer().getPlayerList().broadcastMessage(new StringTextComponent(msg), ChatType.GAME_INFO, Util.NIL_UUID);

			AoAHaloUtil.syncWithNewClient(pl);
			PlayerDataManager.syncNewPlayer(pl);

			PlayerAdvancements plAdvancements = pl.getAdvancements();
			Advancement rootAdv = AdvancementUtil.getAdvancement(new ResourceLocation(AdventOfAscension.MOD_ID, "overworld/root"));

			if (rootAdv == null) {
				Logging.logMessage(Level.WARN, "Unable to find inbuilt advancements, another mod is breaking things.");

				if (AoAConfig.COMMON.doVerboseDebugging.get()) {
					Logging.logStatusMessage("Printing out current advancements list...");
					pl.getServer().getAdvancements().getAllAdvancements().forEach(advancement -> Logging.logMessage(Level.INFO, advancement.getId().toString()));
				}
			}
			else if (!plAdvancements.getOrStartProgress(rootAdv).isDone()) {
				plAdvancements.award(AdvancementUtil.getAdvancement(new ResourceLocation(AdventOfAscension.MOD_ID, "overworld/by_the_books")), "legitimate");
				plAdvancements.award(rootAdv, "playerjoin");
			}
		}
	}

	@SubscribeEvent
	public static void onItemToss(final ItemTossEvent ev) {
		World world = ev.getPlayer().getCommandSenderWorld();

		if (ev.getPlayer() instanceof ServerPlayerEntity) {
			ItemEntity entityItem = ev.getEntityItem();
			Item item = entityItem.getItem().getItem();

			if (item == AoAItems.BLANK_REALMSTONE.get()) {
				if (ev.getPlayer().isInLava()) {
					ItemUtil.givePlayerItemOrDrop(ev.getPlayer(), new ItemStack(AoAItems.NETHER_REALMSTONE.get()));
					ev.getEntityItem().remove();
				}
			}
			else if (item instanceof ReservedItem) {
				ReservedItem.handlePlayerToss(ev);
			}
			else if (item instanceof BossSpawningItem) {
				if (world.getDifficulty() == Difficulty.PEACEFUL) {
					ev.getPlayer().sendMessage(new TranslationTextComponent("message.feedback.spawnBoss.difficultyFail"), Util.NIL_UUID);

					return;
				}

				ev.setCanceled(true);
				world.addFreshEntity(BossSpawningItem.newBossEntityItemFromExisting(entityItem, ev.getPlayer()));

				BossSpawningItem bossItem = (BossSpawningItem)item;

				if (bossItem.getThrowingSound() != null)
					world.playSound(null, entityItem.getX(), entityItem.getX(), entityItem.getZ(), bossItem.getThrowingSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerPickupXp(final PlayerXpEvent.PickupXp ev) {
		PlayerEntity pl = ev.getPlayer();

		if (!pl.level.isClientSide && ev.getOrb().value > 0) {
			ItemStack stack = ItemUtil.getStackFromInventory(pl, AoAItems.EXP_FLASK.get());

			if (stack != null) {
				ExpFlask.addExp(stack, ev.getOrb().value);
				ev.setCanceled(true);
				ev.getOrb().value = 0;
				ev.getOrb().remove();
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerFishing(final ItemFishedEvent ev) {
		if (WorldUtil.isWorld(ev.getEntityLiving().level, AoADimensions.LBOREAN.key) && RandomUtil.oneInNChance(10)) {
			FishingBobberEntity hook = ev.getHookEntity();
			LivingEntity fisher = ev.getEntityLiving();

			ItemEntity drop = new ItemEntity(fisher.level, hook.getX(), hook.getY(), hook.getZ(), new ItemStack(AoAItems.CALL_OF_THE_DRAKE.get()));
			double velocityX = fisher.getX() - hook.getX();
			double velocityY = fisher.getY() - hook.getY();
			double velocityZ = fisher.getZ() - hook.getZ();
			double velocity = MathHelper.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);

			drop.setDeltaMovement(velocityX * 0.1D, velocityY * 0.1D + (double)MathHelper.sqrt(velocity) * 0.08D, velocityZ * 0.1D);
			fisher.level.addFreshEntity(drop);
		}
	}
}
