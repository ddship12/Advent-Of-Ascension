package net.tslat.aoa3.player;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.tslat.aoa3.advent.AdventOfAscension;
import net.tslat.aoa3.common.packet.AoAPackets;
import net.tslat.aoa3.common.packet.packets.ScreenOverlayPacket;
import net.tslat.aoa3.event.custom.events.PlayerChangeXpEvent;
import net.tslat.aoa3.event.custom.events.PlayerLevelChangeEvent;

import java.util.List;

@SuppressWarnings("unused")
public interface AoAPlayerEventListener {
	/**
	 * Used to determine which events your listener should be listening for.
	 * Override and return whichever {@link AoAPlayerEventListener.ListenerType} types you want to handle
	 *
	 * @return an array of {@link AoAPlayerEventListener.ListenerType} types this listener should receive events for
	 */
	default ListenerType[] getListenerTypes() {
		return new ListenerType[] {};
	}

	/**
	 * Used to determine whether the listener should be receiving event calls.
	 * Override to handle enabling/disabling as needed.
	 *
	 * @return whether the listener is currently active.
	 */
	default ListenerState getListenerState() {
		return ListenerState.ACTIVE;
	}

	/**
	 * Used to determine whether the listener meets the requirements relevant to stay active.
	 * This check is only made if the listener is currently active, and returning false will call for the listener to be disabled.
	 *
	 * @return whether the listener currently meets any requirements necessary to stay active.
	 */
	default boolean meetsRequirements() {
		return true;
	}

	/**
	 * Called when your listener is enabled from a previously disabled state. Usually this occurs when the player manually re-enables it, or if AoA determines the listener now meets previously un-met conditions
	 */
	default void reenable(boolean isInit) {}

	/**
	 * Called when the listener is disabled regardless of the source. Usually called when the listener returns false from {@link AoAPlayerEventListener#meetsRequirements()}.
	 * If your listener isn't able to be disabled, then leave this method as default.
	 */
	default void disable(ListenerState reason, boolean isInit) {}

	/**
	 * This method gets triggered every tick as the player is being ticked.
	 * Override to handle data changes or trigger effects that may need to occur at a given tick or each tick for the player.
	 * NOTE: This method will be called regardless of tick phase. Filter out the tick phase you don't intend to use when overriding.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_TICK} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link TickEvent.PlayerTickEvent} event
	 */
	default void handlePlayerTick(final TickEvent.PlayerTickEvent ev) {}

	/**
	 * This method gets triggered when the player jumps.
	 * Override to trigger effects that may need to occur when a player jumps.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_JUMP} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingEvent.LivingJumpEvent} event
	 */
	default void handlePlayerJump(final LivingEvent.LivingJumpEvent ev) {}

	/**
	 * This method gets triggered when the player lands from a fall.
	 * Override to trigger effects that may need to occur when a player lands from a fall.
	 * NOTE: This event can also be used to mitigate/negate fall damage, instead of using one of the attack methods.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_FALL} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingFallEvent} event
	 */
	default void handlePlayerFall(final LivingFallEvent ev) {}

	/**
	 * This method gets triggered when the player dies.
	 * Override to handle data changes or effects that may need to occur when a player dies.
	 * NOTE: While this event is cancellable (and will technically stop the player dying), it is recommended to not use this event to prevent death, and instead handling a damage event.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_DEATH} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingDeathEvent} event
	 */
	default void handlePlayerDeath(final LivingDeathEvent ev) {}

	/**
	 * This method gets triggered when the player respawns.
	 * Override to handle data changes or effects that may need to occur when a player respawns.
	 * NOTE: If a player chooses not to respawn until after a full server restart has occured, game data may not be in the state expected. Only trust serialized data and data manager data.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_RESPAWN} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.PlayerRespawnEvent} event
	 */
	default void handlePlayerRespawn(final PlayerEvent.PlayerRespawnEvent ev) {}

	/**
	 * This method gets triggered when the player logs in.
	 * Override to handle data changes or effects that may need to occur when a player logs in.
	 * At the stage this method is called, the player technically exists ingame, but is still shortly before the client's screen has loaded the world, so any short-lived effects may not be visible.
	 * The player's data manager should already be fully-formed at this point, so feel free to utilise it as needed.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_LOGIN} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.PlayerLoggedInEvent} event
	 */
	default void handlePlayerLogin(final PlayerEvent.PlayerLoggedInEvent ev) {}

	/**
	 * This method gets triggered when the player logs out.
	 * Override to handle data changes that may need to occur when a player logs out.
	 * The player will not see any effects triggered at this stage, so this event should be used for data changes only.
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_LOGOUT} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.PlayerLoggedOutEvent} event
	 */
	default void handlePlayerLogout(final PlayerEvent.PlayerLoggedOutEvent ev) {}

	/**
	 * This method gets triggered when the player is being cloned to a new entity state.
	 * Override to handle data changes that may need to occur when a player's data is cloned to a new state, usually through death or end-portal travel.
	 * NOTE: This should not be used to trigger any effects or actions, and should only be used for transferring required data between old and new data states
	 *
	 * Will only trigger if {@link ListenerType#PLAYER_CLONE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.Clone} event
	 */
	default void handlePlayerDataClone(final PlayerEvent.Clone ev) {}

	/**
	 * This method gets triggered when the player equips, unequips, or swaps out armour.
	 * Override to handle data changes or trigger effects that may need to occur when the player changes armour.
	 *
	 * Will only trigger if {@link ListenerType#EQUIPMENT_CHANGE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingEquipmentChangeEvent} event
	 */
	default void handleArmourChange(final LivingEquipmentChangeEvent ev) {}

	/**
	 * This method gets triggered when the player changes dimension.
	 * Override to trigger effects for when a player changes dimension.
	 * NOTE: At this stage, the player has already moved to the new dimension.
	 *
	 * Will only trigger if {@link ListenerType#DIMENSION_CHANGE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.PlayerChangedDimensionEvent} event
	 */
	default void handleDimensionChange(final PlayerEvent.PlayerChangedDimensionEvent ev) {}

	/**
	 * This method gets triggered when the player changes gamemode.
	 * Override to trigger effects for when a player has their gamemode changed.
	 *
	 * Will only trigger if {@link ListenerType#GAMEMODE_CHANGE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.PlayerChangeGameModeEvent} event
	 */
	default void handleGamemodeChange(final PlayerEvent.PlayerChangeGameModeEvent ev) {}

	/**
	 * This method gets triggered when the player attempts to harvest a block in the world.
	 * Override to oversee or modify the viability of a player harvesting a block.
	 * NOTE: This event is intended to be used to determine additional requirements for obtaining a block's drops on break, but it is also used to determine break speed (E.G. pickaxe on stone).
	 *
	 * Will only trigger if {@link ListenerType#BLOCK_HARVEST_ATTEMPT} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.HarvestCheck} event
	 */
	default void handleBlockHarvestAttempt(final PlayerEvent.HarvestCheck ev) {}

	/**
	 * This method gets triggered when the player starts to harvest a block in the world.
	 * Override to oversee or modify the speed at which a player will break a given block.
	 *
	 * Will only trigger if {@link ListenerType#BLOCK_BREAK_SPEED} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.BreakSpeed} event
	 */
	default void handleHarvestSpeedCheck(final PlayerEvent.BreakSpeed ev) {}

	/**
	 * This method gets triggered when the player breaks a block in the world.
	 * Override to trigger effects for when a player breaks a block.
	 * NOTE: This may not be triggered by modded effects or abilities.
	 *
	 * Will only trigger if {@link ListenerType#BLOCK_BREAK} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link BlockEvent.BreakEvent} event
	 */
	default void handleBlockBreak(final BlockEvent.BreakEvent ev) {}

	/**
	 * This method gets triggered when the player places a block in the world.
	 * Override to trigger effects for when a player places a block.
	 * NOTE: This may not be triggered by modded effects or abilities.
	 *
	 * Will only trigger if {@link ListenerType#BLOCK_PLACE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link BlockEvent.EntityPlaceEvent} event
	 */
	default void handleBlockPlacement(final BlockEvent.EntityPlaceEvent ev) {}

	/**
	 * This method gets triggered when the player throws out an item from their inventory or hand.
	 * Override to trigger effects for when a player throws out an item.
	 *
	 * Will only trigger if {@link ListenerType#ITEM_THROW} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link ItemTossEvent} event
	 */
	default void handleItemToss(final ItemTossEvent ev) {}

	/**
	 * This method gets triggered when the player's level changes in any AoA skill.
	 * Override to trigger effects for when a player's level changes an AoA skill.
	 * NOTE: This method does not discriminate between skills. Do your own checks if required.
	 *
	 * Will only trigger if {@link ListenerType#LEVEL_CHANGE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerLevelChangeEvent} event
	 */
	default void handleLevelChange(final PlayerLevelChangeEvent ev) {}

	/**
	 * This method gets triggered when the player's xp in any given skill is is changed.
	 * Override to trigger effects for when a player is granted or loses AoA skill xp.
	 * NOTE: This method does not discriminate between skills. Do your own checks if required.
	 *
	 * Will only trigger if {@link ListenerType#GAIN_SKILL_XP} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerChangeXpEvent} event
	 */
	default void handleSkillXpGain(final PlayerChangeXpEvent ev) {}

	/**
	 * This method gets triggered when the player is granted xp via the {@link net.minecraft.entity.player.PlayerEntity#giveExperiencePoints} method.
	 * Override to trigger effects for when a player is granted vanilla xp.
	 *
	 * Will only trigger if {@link ListenerType#GAIN_VANILLA_XP} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerXpEvent.XpChange} event
	 */
	default void handleVanillaXpGain(final PlayerXpEvent.XpChange ev) {}

	/**
	 * This method gets triggered when the player takes an item out of a crafting device.
	 * Override to trigger effects for when a player has retrieved an item from a crafting table or crafting device. The resulting stack is not modifiable here.
	 *
	 * Will only trigger if {@link ListenerType#ITEM_CRAFT} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.ItemCraftedEvent} event
	 */
	default void handleItemCraft(final PlayerEvent.ItemCraftedEvent ev) {}

	/**
	 * This method gets triggered when the player takes an item out of a smelting device or furnace.
	 * Override to trigger effects for when a player has retrieved an item from a furnace or smelting device. The resulting stack is not modifiable here.
	 *
	 * Will only trigger if {@link ListenerType#ITEM_SMELT} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PlayerEvent.ItemSmeltedEvent} event
	 */
	default void handleItemSmelt(final PlayerEvent.ItemSmeltedEvent ev) {}

	/**
	 * This method gets triggered when the player has a potion effect applied to them. It is also triggered if an existing effect is upgraded or extended.
	 * Override to trigger effects for when a player is afflicted with a potion effect of any kind.
	 *
	 * Will only trigger if {@link ListenerType#POTION_APPLIED} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link PotionEvent.PotionAddedEvent} event
	 */
	default void handleAppliedPotion(final PotionEvent.PotionAddedEvent ev) {}

	/**
	 * This method gets triggered when player attribute modifiers should be applied. Usually this is on login and during a clone of the player's data.
	 * Consider this a trigger point for safely applying any attribute modifiers your listener may implement.
	 *
	 * Will only trigger if {@link ListenerType#ATTRIBUTE_MODIFIERS} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param plData {@link PlayerDataManager} player Advent data container
	 */
	default void applyAttributeModifiers(final PlayerDataManager plData) {}

	/**
	 * This method gets triggered in most circumstances when a player's attribute modifiers should be removed. Usually this is when a listener is disabled or removed, or if the player no longer meets the requirements for the listener.
	 * Consider this a trigger point for safely removing any attribute modifiers your listener may implement.
	 *
	 * Will only trigger if {@link ListenerType#ATTRIBUTE_MODIFIERS} is included in the retuned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param plData {@link PlayerDataManager} player Advent data container
	 */
	default void removeAttributeModifiers(final PlayerDataManager plData) {}

	/**
	 * This method gets triggered when a player is about to critically strike a target. This is called before the damage is done, but after any other modifications have been made.
	 * Override to trigger effects or modify the damage modifier the critical hit will apply in the coming attack.
	 *
	 * Will only trigger if {@link ListenerType#CRITICAL_HIT} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link CriticalHitEvent} event
	 */
	default void handleCriticalHit(final CriticalHitEvent ev) {}

	/**
	 * This method gets triggered when the player is about to be attacked by another entity.
	 * Override this method to trigger effects that may occur when being attacked by another entity, regardless of whether the attack is successful or not.
	 *
	 * Will only trigger if {@link ListenerType#INCOMING_ATTACK_BEFORE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingAttackEvent} event
	 */
	default void handlePreIncomingAttack(final LivingAttackEvent ev) {}

	/**
	 * This method gets triggered when the player is about to attack another entity.
	 * Override this method to trigger effects that may occur when attacking another entity, regardless of whether the attack is successful or not.
	 *
	 * Will only trigger if {@link ListenerType#OUTGOING_ATTACK_BEFORE} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingAttackEvent} event
	 */
	default void handlePreOutgoingAttack(final LivingAttackEvent ev) {}

	/**
	 * This method gets triggered when the player is being attacked by another entity.
	 * Override this method to modify the damage to be used for the event. The damage value of the attack provided here is prior to any armour, enchantment, or potion modifications take place.
	 *
	 * Will only trigger if {@link ListenerType#INCOMING_ATTACK_DURING} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingHurtEvent} event
	 */
	default void handleIncomingAttack(final LivingHurtEvent ev) {}

	/**
	 * This method gets triggered when the player is attacking another entity.
	 * Override this method to modify the damage to be used for the event. The damage value of the attack provided here is prior to any armour, enchantment, or potion modifications take place.
	 *
	 * Will only trigger if {@link ListenerType#OUTGOING_ATTACK_DURING} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingHurtEvent} event
	 */
	default void handleOutgoingAttack(final LivingHurtEvent ev) {}

	/**
	 * This method gets triggered when the player has been successfully attacked by another entity.
	 * Override this method to handle damage-triggered effects. The damage value of the attack is final here, use one of the earlier attack events to modify damage.
	 *
	 * Will only trigger if {@link ListenerType#INCOMING_ATTACK_AFTER} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingDamageEvent} event
	 */
	default void handlePostIncomingAttack(final LivingDamageEvent ev) {}

	/**
	 * This method gets triggered when the player has attacked an entity successfully.
	 * Override this method to handle attack-triggered effects. The damage value of the attack is final here, use one of the earlier attack events to modify damage.
	 *
	 * Will only trigger if {@link ListenerType#OUTGOING_ATTACK_AFTER} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingDamageEvent} event
	 */
	default void handlePostOutgoingAttack(final LivingDamageEvent ev) {}

	/**
	 * This method gets triggered when an entity is killed and the player is determined to have at least partially contributed.
	 * This method will <b>not</b> be called if the player dies. The {@link ListenerType#PLAYER_DEATH} listener can be used for that situation.
	 * Override this method to handle actions caused by killing an entity. The entity's death may technically be cancelled here, but it's not recommended.
	 *
	 * Will only trigger if {@link ListenerType#ENTITY_KILL} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param ev {@link LivingDeathEvent} event
	 */
	default void handleEntityKill(final LivingDeathEvent ev) {}

	/**
	 * This method gets triggered when a loot table is rolled that involves the player in some way.
	 * Override this method to handle loot modification, either addition, removal, or negation.
	 * Modify the provided loot list, do not create a new one. The remaining contents of the list will be dropped in the world as normal.
	 *
	 * Will only trigger if {@link ListenerType#LOOT_MODIFICATION} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes}
	 *
	 * @param loot the existing list of loot stacks to be dropped.
	 * @param context the context for the loot table roll. Contains all the relevant info for the event itself.
	 */
	default void handleLootModification(final List<ItemStack> loot, LootContext context) {}

	@OnlyIn(Dist.CLIENT)
	default KeyBinding getKeybind() {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	default boolean shouldSendKeyPress() {
		return true;
	}

	/**
	 * This method gets triggered when the keybinding assigned in {@link AoAPlayerEventListener#getKeybind()} is pressed.
	 * It will only be called once per press, so ongoing press effects should be handled manually via the keybind itself.
	 *
	 * NOTE: Currently this is only functional for {@link net.tslat.aoa3.player.ability.AoAAbility} abilities.
	 *
	 * Will only trigger if {@link ListenerType#KEY_INPUT} is included in the returned event listener types in {@link AoAPlayerEventListener#getListenerTypes()}
	 */
	default void handleKeyInput() {}

	default void activatedActionKey(ServerPlayerEntity player) {
		AoAPackets.messagePlayer(player, new ScreenOverlayPacket(AdventOfAscension.id("textures/gui/overlay/misc/action_key_activation_vignette.png"), 10));
	}

	enum ListenerState {
		ACTIVE("active"),
		MANUALLY_DISABLED("disabled"),
		DEACTIVATED("deactivated");

		private final String id;

		ListenerState(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}

		public static ListenerState fromId(String id) {
			switch (id) {
				case "disabled":
					return MANUALLY_DISABLED;
				case "deactivated":
					return DEACTIVATED;
				case "active":
				default:
					return ACTIVE;
			}
		}
	}

	enum ListenerType {
		PLAYER_TICK,
		PLAYER_JUMP,
		PLAYER_FALL,
		PLAYER_DEATH,
		PLAYER_RESPAWN,
		PLAYER_LOGIN,
		PLAYER_LOGOUT,
		PLAYER_CLONE,
		EQUIPMENT_CHANGE,
		DIMENSION_CHANGE,
		GAMEMODE_CHANGE,
		BLOCK_HARVEST_ATTEMPT,
		BLOCK_BREAK_SPEED,
		BLOCK_BREAK,
		BLOCK_PLACE,
		ITEM_THROW,
		LEVEL_CHANGE,
		GAIN_SKILL_XP,
		GAIN_VANILLA_XP,
		ITEM_CRAFT,
		ITEM_SMELT,
		POTION_APPLIED,
		LOOT_MODIFICATION,
		ATTRIBUTE_MODIFIERS,
		CRITICAL_HIT,
		INCOMING_ATTACK_BEFORE,
		INCOMING_ATTACK_DURING,
		INCOMING_ATTACK_AFTER,
		OUTGOING_ATTACK_BEFORE,
		OUTGOING_ATTACK_DURING,
		OUTGOING_ATTACK_AFTER,
		ENTITY_KILL,
		KEY_INPUT
	}
}
