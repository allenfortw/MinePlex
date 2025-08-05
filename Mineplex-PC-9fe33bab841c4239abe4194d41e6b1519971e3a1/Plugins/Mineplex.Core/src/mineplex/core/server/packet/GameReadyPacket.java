package mineplex.core.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mineplex.core.server.event.GameReadyEvent;

import org.bukkit.event.Event;

public class GameReadyPacket extends Packet
{
	private List<String> _players;
	
	public GameReadyPacket() { }
	
	public GameReadyPacket(List<String> players)
	{
		_players = players;
	}
	
	public void ParseStream(DataInputStream dataInput) throws IOException
	{
		int playerCount = dataInput.readShort();
	
		if (_players == null)
			_players = new ArrayList<String>();
			
		for (int i = 0; i < playerCount; i++) 
		{
			_players.add(readString(dataInput, 16));
		}
	}
	
	public void Write(DataOutputStream dataOutput) throws IOException
	{
		dataOutput.writeShort(73);
		dataOutput.writeShort(_players.size());

		for (int i = 0; i < _players.size(); i++) 
		{
			writeString(_players.get(i), dataOutput);
		}
	}
	
	public Event GetEvent()
	{
		return new GameReadyEvent(_players);
	}
	
	public List<String> GetPlayers()
	{
		return _players;
	}
}
