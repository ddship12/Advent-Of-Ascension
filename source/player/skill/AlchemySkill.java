package net.tslat.aoa3.player.skill;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.tslat.aoa3.common.registration.custom.AoASkills;
import net.tslat.aoa3.player.PlayerDataManager;

public class AlchemySkill extends AoASkill.Instance {
	public AlchemySkill(PlayerDataManager plData, JsonObject jsonData) {
		super(AoASkills.ALCHEMY.get(), plData, jsonData);
	}

	public AlchemySkill(CompoundNBT nbtData) {
		super(AoASkills.ALCHEMY.get(), nbtData);
	}
}
