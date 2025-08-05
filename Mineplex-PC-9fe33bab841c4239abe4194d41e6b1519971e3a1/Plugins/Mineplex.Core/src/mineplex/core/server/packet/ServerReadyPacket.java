package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mineplex.core.server.event.ServerReadyEvent;

import org.bukkit.event.Event;

public class ServerReadyPacket extends Packet
{
	private String _serverPath;
	
	public ServerReadyPacket() { }
	
	public ServerReadyPacket(String serverPath)
	{
		_serverPath = serverPath;
	}
	
	@Override
	public void ParseStream(DataInputStream inputStream) throws IOException
	{
		_serverPath = readString(inputStream, 21);
	}

	@Override
	public void Write(DataOutputStream dataOutput) throws IOException
	{
		dataOutput.writeShort(61);
		writeString(_serverPath, dataOutput);
	}

	@Override
	public Event GetEvent()
	{
		return new ServerReadyEvent(_serverPath);
	}
	
}
