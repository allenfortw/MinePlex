package mineplex.core.packethandler;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_6_R2.Packet;

public interface IPacketRunnable
{
	boolean run(Packet packet, Player owner, PacketArrayList packetList);
}
