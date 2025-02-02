package net.tslat.aoa3.player.skill;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.tslat.aoa3.common.registration.custom.AoASkills;
import net.tslat.aoa3.player.PlayerDataManager;

public class ImbuingSkill extends AoASkill.Instance {
	public ImbuingSkill(PlayerDataManager plData, JsonObject jsonData) {
		super(AoASkills.IMBUING.get(), plData, jsonData);
	}

	public ImbuingSkill(CompoundNBT nbtData) {
		super(AoASkills.IMBUING.get(), nbtData);
	}
}
