package nautilus.game.lobby.gamequeue;

public enum QueueType
{
	SOLO("Solo", "Play all alone."),
	PARTY("Party", "Play with friends.");
	
	private String _name;
	private String _description;
	
	QueueType(String name, String description)
	{
		_name = name;
		_description = description;
	}
	
	public String GetName()
	{
		return _name;
	}
	
	public String GetDescription()
	{
		return _description;
	}
}
