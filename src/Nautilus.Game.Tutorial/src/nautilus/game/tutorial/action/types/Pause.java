package nautilus.game.tutorial.action.types;

import org.bukkit.entity.Player;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class Pause extends Action
{
	public Pause(Part part, long delay) 
	{
		super(part, delay);
	}

	@Override
	public void CustomAction(Player player)
	{
		//None
	}
}
