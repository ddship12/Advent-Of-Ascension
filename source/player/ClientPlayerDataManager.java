package net.tslat.aoa3.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.tslat.aoa3.client.gui.adventgui.AdventGuiTabLore;
import net.tslat.aoa3.common.packet.AoAPackets;
import net.tslat.aoa3.common.packet.packets.PlayerAbilityKeybindPacket;
import net.tslat.aoa3.common.registration.custom.AoAResources;
import net.tslat.aoa3.common.registration.custom.AoASkills;
import net.tslat.aoa3.player.ability.AoAAbility;
import net.tslat.aoa3.player.resource.AoAResource;
import net.tslat.aoa3.player.skill.AoASkill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ClientPlayerDataManager {
	private static ClientPlayerEntity player;

	private static final ConcurrentSkipListMap<AoASkill, AoASkill.Instance> skills = new ConcurrentSkipListMap<AoASkill, AoASkill.Instance>(Comparator.comparing(AoASkill::getRegistryName));
	private static final ConcurrentSkipListMap<AoAResource, AoAResource.Instance> resources = new ConcurrentSkipListMap<AoAResource, AoAResource.Instance>(Comparator.comparing(AoAResource::getRegistryName));

	private static final ConcurrentHashMap<Integer, ArrayList<AoAPlayerEventListener>> keyListeners = new ConcurrentHashMap<Integer, ArrayList<AoAPlayerEventListener>>(1);

	private static boolean isLegitimate = true;
	private static int totalLevel = 0;

	public static void setPlayer(ClientPlayerEntity pl) {
		player = pl;
	}

	public void dog() {

	}

	public static void reset() {
		player = null;
		isLegitimate = true;
		totalLevel = 0;

		skills.clear();
		resources.clear();
		keyListeners.clear();
	}

	public static boolean isLegitimate() {
		return isLegitimate;
	}

	public static int getTotalLevel() {
		return totalLevel;
	}

	public static Collection<AoASkill.Instance> getSkills() {
		return skills.values();
	}

	@Nonnull
	public static AoASkill.Instance getSkill(AoASkill skill) {
		return skills.getOrDefault(skill, AoASkills.DEFAULT);
	}

	public static Collection<AoAResource.Instance> getResources() {
		return resources.values();
	}

	@Nonnull
	public static AoAResource.Instance getResource(AoAResource resource) {
		return resources.getOrDefault(resource, AoAResources.DEFAULT);
	}

	public static void handleKeyInput(int keycode) {
		if (Minecraft.getInstance().player == null || keyListeners.isEmpty() || !keyListeners.containsKey(keycode))
			return;

		ArrayList<AoAPlayerEventListener> listeners = keyListeners.get(keycode);
		ArrayList<String> abilities = new ArrayList<String>(listeners.size());

		for (AoAPlayerEventListener listener : listeners) {
			if (listener.getListenerState() == AoAPlayerEventListener.ListenerState.ACTIVE && listener.shouldSendKeyPress())
				abilities.add(((AoAAbility.Instance)listener).getUniqueIdentifier());
		}

		if (!abilities.isEmpty())
			AoAPackets.messageServer(new PlayerAbilityKeybindPacket(abilities));
	}

	private static void updateTotalLevel() {
		totalLevel = 0;

		for (AoASkill.Instance skill : skills.values()) {
			totalLevel += 100 * skill.getCycles();
			totalLevel += skill.getLevel(true);
		}
	}

	public static void loadFromNbt(CompoundNBT baseTag) {
		isLegitimate = baseTag.getBoolean("legitimate");
		int hash = baseTag.getInt("hash");

		if (hash == 0)
			isLegitimate = false;

		skills.clear();
		resources.clear();

		if (baseTag.contains("skills")) {
			CompoundNBT skillsNbt = baseTag.getCompound("skills");

			for (String id : skillsNbt.getAllKeys()) {
				AoASkill skill = AoASkills.getSkill(new ResourceLocation(id));
				AoASkill.Instance instance = skill.buildClientInstance(skillsNbt.getCompound(id));

				skills.put(skill, instance);
				checkForKeyListeners(instance);

				for (AoAAbility.Instance ability : instance.getAbilityMap().values()) {
					checkForKeyListeners(ability);
				}
			}

			updateTotalLevel();
		}

		if (baseTag.contains("resources")) {
			CompoundNBT resourcesNbt = baseTag.getCompound("resources");

			for (String id : resourcesNbt.getAllKeys()) {
				AoAResource resource = AoAResources.getResource(new ResourceLocation(id));
				AoAResource.Instance instance = resource.buildClientInstance(resourcesNbt.getCompound(id));

				resources.put(resource, instance);
				checkForKeyListeners(instance);
			}
		}

		if (baseTag.contains("PatchouliBooks")) {
			ArrayList<ResourceLocation> books = new ArrayList<ResourceLocation>();

			ListNBT booksNbt = baseTag.getList("PatchouliBooks", Constants.NBT.TAG_STRING);

			for (INBT book : booksNbt) {
				books.add(new ResourceLocation(book.getAsString()));
			}

			AdventGuiTabLore.syncBooks(books);
		}
	}

	public static void updateData(CompoundNBT syncTag) {
		if (syncTag.contains("skills")) {
			CompoundNBT skillsData = syncTag.getCompound("skills");

			for (String key : skillsData.getAllKeys()) {
				getSkill(AoASkills.getSkill(new ResourceLocation(key))).receiveSyncData(skillsData.getCompound(key));
			}

			updateTotalLevel();
		}

		if (syncTag.contains("resources")) {
			CompoundNBT resourcesData = syncTag.getCompound("resources");

			for (String key : resourcesData.getAllKeys()) {
				getResource(AoAResources.getResource(new ResourceLocation(key))).receiveSyncData(resourcesData.getCompound(key));
			}
		}

		if (syncTag.contains("patchouliBooks")) {
			ArrayList<ResourceLocation> books = new ArrayList<ResourceLocation>();

			ListNBT booksNbt = syncTag.getList("patchouliBooks", Constants.NBT.TAG_STRING);

			for (INBT book : booksNbt) {
				books.add(new ResourceLocation(book.getAsString()));
			}

			AdventGuiTabLore.syncBooks(books);
		}

		if (syncTag.contains("legitimate"))
			isLegitimate = syncTag.getBoolean("legitimate");
	}

	private static void checkForKeyListeners(AoAPlayerEventListener listener) {
		for (AoAPlayerEventListener.ListenerType type : listener.getListenerTypes()) {
			if (type == AoAPlayerEventListener.ListenerType.KEY_INPUT) {
				int keyCode = listener.getKeybind().getKey().getValue();

				if (!keyListeners.containsKey(keyCode))
					keyListeners.putIfAbsent(keyCode, new ArrayList<AoAPlayerEventListener>(1));

				keyListeners.get(keyCode).add(listener);

				return;
			}
		}
	}
}
