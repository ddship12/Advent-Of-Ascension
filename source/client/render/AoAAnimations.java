package net.tslat.aoa3.client.render;

import net.minecraft.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

public final class AoAAnimations {
	public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("misc.idle", true);
	public static final AnimationBuilder RECOVER = new AnimationBuilder().addAnimation("misc.rest", false);
	public static final AnimationBuilder EAT = new AnimationBuilder().addAnimation("misc.eat", false);

	public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation("move.walk", true);
	public static final AnimationBuilder RUN = new AnimationBuilder().addAnimation("move.run", true);
	public static final AnimationBuilder FLY = new AnimationBuilder().addAnimation("move.fly", true);
	public static final AnimationBuilder SWIM = new AnimationBuilder().addAnimation("move.swim", true);

	public static final AnimationBuilder ATTACK_SWING = new AnimationBuilder().addAnimation("attack.swing", false);
	public static final AnimationBuilder ATTACK_THROW = new AnimationBuilder().addAnimation("attack.throw", false);
	public static final AnimationBuilder ATTACK_BITE = new AnimationBuilder().addAnimation("attack.bite", false);
	public static final AnimationBuilder ATTACK_SLAM = new AnimationBuilder().addAnimation("attack.slam", false);
	public static final AnimationBuilder ATTACK_STRIKE = new AnimationBuilder().addAnimation("attack.strike", false);
	public static final AnimationBuilder ATTACK_SPIN = new AnimationBuilder().addAnimation("attack.spin", false);
	public static final AnimationBuilder ATTACK_FLYING_BITE = new AnimationBuilder().addAnimation("attack.midair_bite", false);
	public static final AnimationBuilder ATTACK_SHOOT = new AnimationBuilder().addAnimation("attack.shoot", false);

	public static <T extends IAnimatable> AnimationController<T> genericWalkController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(WALK);

				return PlayState.CONTINUE;
			}

			return PlayState.STOP;
		});
	}

	public static <T extends IAnimatable> AnimationController<T> genericSwimController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(SWIM);

				return PlayState.CONTINUE;
			}

			return PlayState.STOP;
		});
	}

	public static <T extends IAnimatable> AnimationController<T> genericSwimIdleController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(SWIM);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	public static <T extends IAnimatable> AnimationController<T> genericWalkIdleController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(WALK);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	public static <T extends IAnimatable> AnimationController<T> genericFlyController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			event.getController().setAnimation(FLY);

			return PlayState.CONTINUE;
		});
	}

	public static <T extends IAnimatable> AnimationController<T> genericFlyIdleController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(FLY);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	public static <T extends IAnimatable> AnimationController<T> genericWalkRunIdleController(T entity) {
		return new AnimationController<T>(entity, "movement", 0, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(WALK);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	public static <T extends LivingEntity & IAnimatable> AnimationController<T> genericAttackController(T entity, AnimationBuilder attackAnimation) {
		return new AnimationController<T>(entity, "attacking", 0, event -> {
			if (entity.swinging) {
				event.getController().setAnimation(attackAnimation);

				return PlayState.CONTINUE;
			}

			event.getController().markNeedsReload();
			return PlayState.STOP;
		});
	}

	public static <T extends LivingEntity & IAnimatable> AnimationController<T> customAttackController(T entity, AnimationController.IAnimationPredicate<T> controllerPredicate) {
		return new AnimationController<T>(entity, "attacking", 0, controllerPredicate);
	}
}
