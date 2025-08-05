package mineplex.core.server.packet;

import java.net.Socket;

import org.bukkit.event.Event;

public interface IPacketHandler
{
	void HandlePacketEvent(Event packetEvent, Socket socket);
}
