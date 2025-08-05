package nautilus.game.tutorial.action.types;

import org.bukkit.entity.Player;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class AllowAction extends Action
{
	public boolean _value;
	
	public AllowAction(Part part, boolean value) 
	{
		super(part, 0);
		
		_value = value;
	}

	@Override
	public void CustomAction(Player player)
	{
		Part.SetAllowAction(_value);
	}
}
