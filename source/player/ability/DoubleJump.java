package net.tslat.aoa3.player.ability;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.tslat.aoa3.common.packet.AoAPackets;
import net.tslat.aoa3.common.packet.packets.UpdateClientMovementPacket;
import net.tslat.aoa3.common.registration.custom.AoAAbilities;
import net.tslat.aoa3.common.registration.custom.AoAResources;
import net.tslat.aoa3.mixin.common.invoker.AccessibleLivingEntity;
import net.tslat.aoa3.player.ClientPlayerDataManager;
import net.tslat.aoa3.player.skill.AoASkill;
import net.tslat.aoa3.util.NumberUtil;
import net.tslat.aoa3.util.PlayerUtil;

public class DoubleJump extends AoAAbility.Instance {
	private static final ListenerType[] LISTENERS = new ListenerType[] {ListenerType.KEY_INPUT, ListenerType.PLAYER_FALL};

	private final float energyConsumption;

	private boolean canJump = true;

	public DoubleJump(AoASkill.Instance skill, JsonObject data) {
		super(AoAAbilities.DOUBLE_JUMP.get(), skill, data);

		this.energyConsumption = Math.max(0, JSONUtils.getAsFloat(data, "energy_consumption"));
	}

	public DoubleJump(AoASkill.Instance skill, CompoundNBT data) {
		super(AoAAbilities.DOUBLE_JUMP.get(), skill, data);

		this.energyConsumption = data.getFloat("energy_consumption");
	}

	@Override
	public TranslationTextComponent getDescription() {
		return new TranslationTextComponent(super.getDescription().getKey(), NumberUtil.roundToNthDecimalPlace(this.energyConsumption, 2));
	}

	@Override
	public ListenerType[] getListenerTypes() {
		return LISTENERS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public KeyBinding getKeybind() {
		return Minecraft.getInstance().options.keyJump;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSendKeyPress() {
		return !Minecraft.getInstance().player.isOnGround() && ClientPlayerDataManager.getResource(AoAResources.ENERGY.get()).hasAmount(this.energyConsumption);
	}

	@Override
	public void handleKeyInput() {
		if (canJump) {
			ServerPlayerEntity player = getPlayer();

			if (player.isOnGround() || player.isCreative())
				return;

			if (skill.getPlayerDataManager().getResource(AoAResources.ENERGY.get()).consume(energyConsumption, false)) {
				canJump = false;

				((AccessibleLivingEntity)player).jump();
				AoAPackets.messagePlayer(player, new UpdateClientMovementPacket(UpdateClientMovementPacket.Operation.SET).y((float)player.getDeltaMovement().y()));
				skill.adjustXp(PlayerUtil.getTimeBasedXpForLevel(skill.getLevel(true), 16), false, false);
			}
		}
	}

	@Override
	public void handlePlayerFall(LivingFallEvent ev) {
		if (!canJump)
			ev.setDistance(ev.getDistance() - ((AccessibleLivingEntity)ev.getEntityLiving()).getJumpVelocity() * 10f);

		canJump = true;
	}

	@Override
	public CompoundNBT getSyncData(boolean forClientSetup) {
		CompoundNBT syncData = super.getSyncData(forClientSetup);

		if (forClientSetup)
			syncData.putFloat("energy_consumption", energyConsumption);

		return syncData;
	}
}
