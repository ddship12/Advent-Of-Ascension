package net.tslat.aoa3.common.packet.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tslat.aoa3.client.ClientOperations;
import net.tslat.aoa3.common.registration.custom.AoAResources;
import net.tslat.aoa3.common.registration.custom.AoASkills;
import net.tslat.aoa3.player.resource.AoAResource;
import net.tslat.aoa3.player.skill.AoASkill;

import java.util.function.Supplier;

public class ToastPopupPacket implements AoAPacket {
	private final ToastPopupType type;
	private final Object subject;
	private final Object value;

	public ToastPopupPacket(final AoASkill skill, final int levelReq) {
		this.type = ToastPopupType.SKILL_REQUIREMENT;
		this.value = levelReq;
		this.subject = skill.getRegistryName();
	}

	public ToastPopupPacket(final AoAResource resource, final float amount) {
		this.type = ToastPopupType.RESOURCE_REQUIREMENT;
		this.value = amount;
		this.subject = resource.getRegistryName();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeUtf(type.toString());

		switch (type) {
			case SKILL_REQUIREMENT:
				buffer.writeUtf(subject.toString());
				buffer.writeInt((Integer)value);
				break;
			case RESOURCE_REQUIREMENT:
				buffer.writeUtf(subject.toString());
				buffer.writeFloat((Float)value);
				break;
		}
	}

	public static ToastPopupPacket decode(PacketBuffer buffer) {
		ToastPopupType type = ToastPopupType.valueOf(buffer.readUtf(32767));

		switch (type) {
			case SKILL_REQUIREMENT:
				return new ToastPopupPacket(AoASkills.getSkill(new ResourceLocation(buffer.readUtf(32767))), buffer.readInt());
			case RESOURCE_REQUIREMENT:
				return new ToastPopupPacket(AoAResources.getResource(new ResourceLocation(buffer.readUtf(32767))), buffer.readFloat());
		}

		return null;
	}

	public void receiveMessage(Supplier<NetworkEvent.Context> context) {
		ClientOperations.addToast(type, subject, value);

		context.get().setPacketHandled(true);
	}

	public enum ToastPopupType {
		SKILL_REQUIREMENT,
		RESOURCE_REQUIREMENT
	}
}
