package nautilus.game.tutorial.action.types;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

import org.bukkit.entity.Player;

public class Index extends Action
{
	private String _index;
	
	public Index(Part part, String index) 
	{
		super(part, 0);
		
		_index = index;
	}

	@Override
	public void CustomAction(Player player)
	{
		
	}
	
	public String GetIndex()
	{
		return _index;
	}
}
