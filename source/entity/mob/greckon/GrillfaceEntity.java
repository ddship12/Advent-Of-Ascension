package net.tslat.aoa3.entity.mob.greckon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.tslat.aoa3.advent.AdventOfAscension;
import net.tslat.aoa3.common.packet.AoAPackets;
import net.tslat.aoa3.common.packet.packets.ScreenOverlayPacket;
import net.tslat.aoa3.common.registration.AoASounds;
import net.tslat.aoa3.entity.base.AoAMeleeMob;

import javax.annotation.Nullable;

public class GrillfaceEntity extends AoAMeleeMob {
    private int scareCooldown = 0;

    public GrillfaceEntity(EntityType<? extends MonsterEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 2f;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return AoASounds.ENTITY_GRILLFACE_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AoASounds.ENTITY_GRILLFACE_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AoASounds.ENTITY_GRILLFACE_HURT.get();
    }

    @Override
    protected void onAttack(Entity target) {
        if (target instanceof ServerPlayerEntity && getLastHurtByMob() == null && scareCooldown <= 0) {
            playSound(AoASounds.ENTITY_GRILLFACE_SCARE.get(), 1.0f, 1.0f);
            AoAPackets.messagePlayer((ServerPlayerEntity)target, new ScreenOverlayPacket(new ResourceLocation(AdventOfAscension.MOD_ID, "textures/gui/overlay/effect/grillface.png"), 20));

            scareCooldown = 100;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (scareCooldown > 0)
            scareCooldown--;
    }
}
