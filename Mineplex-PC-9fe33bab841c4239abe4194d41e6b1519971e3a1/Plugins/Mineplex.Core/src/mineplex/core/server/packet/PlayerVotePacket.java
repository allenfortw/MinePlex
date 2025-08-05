package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mineplex.core.server.event.PlayerVoteEvent;

import org.bukkit.event.Event;

public class PlayerVotePacket extends Packet
{
	private String _playerName;
	private int _points;
	
	public PlayerVotePacket() { }
	
	public PlayerVotePacket(String playerName, int points)
	{
		_playerName = playerName;
		_points = points;
	}
	
	public void ParseStream(DataInputStream dataInput) throws IOException
	{
		_playerName = readString(dataInput, 16);
		_points = dataInput.readInt();
	}
	
	public void Write(DataOutputStream dataOutput) throws IOException
	{
		dataOutput.writeShort(81);
		writeString(_playerName, dataOutput);
		dataOutput.writeInt(_points);
	}
	
	public String GetPlayerName()
	{
		return _playerName;
	}
	
	public int GetPointReward()
	{
		return _points;
	}

	public Event GetEvent()
	{
		return new PlayerVoteEvent(_playerName, _points);
	}
}
