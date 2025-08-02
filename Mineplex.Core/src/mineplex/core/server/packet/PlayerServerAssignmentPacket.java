package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mineplex.core.server.event.PlayerServerAssignmentEvent;

import org.bukkit.event.Event;

public class PlayerServerAssignmentPacket extends Packet
{
	private String _playerName;
	private String _serverName;
	
	public PlayerServerAssignmentPacket() { }
	
	public PlayerServerAssignmentPacket(String playerName, String serverName)
	{
		_playerName = playerName;
		_serverName = serverName;
	}
	
	@Override
	public void ParseStream(DataInputStream inputStream) throws IOException
	{
		_playerName = readString(inputStream, 16);
		_serverName = readString(inputStream, 16);
	}
	
	public void Write(DataOutputStream dataOutput) throws IOException
	{
		dataOutput.writeShort(72);
		writeString(_playerName, dataOutput);
		writeString(_serverName, dataOutput);
	}
	
	@Override
	public Event GetEvent()
	{
		return new PlayerServerAssignmentEvent(_playerName, _serverName);
	}
	
}
