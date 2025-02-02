package net.tslat.aoa3.client.model.entity.mob.lborean;

import net.minecraft.util.ResourceLocation;
import net.tslat.aoa3.advent.AdventOfAscension;
import net.tslat.aoa3.entity.mob.lborean.SeaViperEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SeaViperModel extends AnimatedGeoModel<SeaViperEntity> {
	private static final ResourceLocation MODEL = new ResourceLocation(AdventOfAscension.MOD_ID, "geo/entities/mobs/lborean/sea_viper.geo.json");
	private static final ResourceLocation TEXTURE = new ResourceLocation(AdventOfAscension.MOD_ID, "textures/entity/mobs/lborean/sea_viper.png");
	private static final ResourceLocation ANIMATIONS = new ResourceLocation(AdventOfAscension.MOD_ID, "animations/entities/mobs/lborean/sea_viper.animation.json");

	@Override
	public ResourceLocation getModelLocation(SeaViperEntity seaViper) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(SeaViperEntity seaViper) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(SeaViperEntity seaViper) {
		return ANIMATIONS;
	}
}
