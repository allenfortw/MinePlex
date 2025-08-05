package nautilus.game.lobby.gamequeue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Gamemode
{
	private List<GameQueue> _gameQueues;
	
	private Material _displayMaterial;
	private byte _displayData;
	
	private String _name;
	private String[] _description;
	
	public Gamemode(Material displayMaterial, byte displayData, String name, String...description)
	{
		_displayMaterial = displayMaterial;
		_displayData = displayData;
		_name = name;
		_description = description;
		_gameQueues = new ArrayList<GameQueue>();
	}
	
	public void AddGameQueue(QueueType queueType)
	{
		_gameQueues.add(new GameQueue(this, queueType));
	}
	
	public List<GameQueue> GetQueues()
	{
		return _gameQueues;
	}

	public String[] GetDescription()
	{
		return _description;
	}

	public Material GetDisplayMaterial()
	{
		return _displayMaterial;
	}

	public byte GetDisplayData()
	{
		return _displayData;
	}

	public String GetName()
	{
		return _name;
	}
}
