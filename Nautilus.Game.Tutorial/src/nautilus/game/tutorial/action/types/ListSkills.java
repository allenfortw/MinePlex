package nautilus.game.tutorial.action.types;

import org.bukkit.entity.Player;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class ListSkills extends Action
{
	public ListSkills(Part part) 
	{
		super(part, 0);
	}

	@Override
	public void CustomAction(Player player)
	{
		Part.Manager.Clients().Get(player).Class().ListSkills(player);
	}
}
