package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.event.Event;

import mineplex.core.server.event.PlayerGameAssignmentEvent;

public class PlayerGameAssignmentPacket extends Packet
{
	private String _playerName;
	
	public PlayerGameAssignmentPacket() { }
	
	public PlayerGameAssignmentPacket(String playerName)
	{
		_playerName = playerName;
	}
	
	public void ParseStream(DataInputStream dataInput) throws IOException
	{
		_playerName = readString(dataInput, 16);
	}
	
	public void Write(DataOutputStream dataOutput) throws IOException
	{
		dataOutput.writeShort(71);
		writeString(_playerName, dataOutput);
	}
	
	public Event GetEvent()
	{
		return new PlayerGameAssignmentEvent(_playerName);
	}
	
	public String GetPlayerName()
	{
		return _playerName;
	}
}
