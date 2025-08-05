package nautilus.game.tutorial.action.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class Teleport extends Action
{
	private Location _target;
	
	public Teleport(Part part, Location target) 
	{
		super(part, 0);
		
		_target = target;
	}

	@Override
	public void CustomAction(Player player)
	{
		Part.Manager.Teleport().TP(player, _target);
	}
}
