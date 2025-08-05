package nautilus.game.tutorial.action.types;

import org.bukkit.entity.Player;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class IndexJump extends Action
{
	private String _index;
	
	public IndexJump(Part part, String index) 
	{
		super(part, 0);
		
		_index = index;
	}

	@Override
	public void CustomAction(Player player)
	{
		for (int i=0 ; i<Part.GetActions().size() ; i++) 
		{
			if (!(Part.GetActions().get(i) instanceof Index))
				continue;
			
			if (!_index.equals(((Index)Part.GetActions().get(i)).GetIndex()))
				continue;
			
			Part.SetIndex(i);
		}
	}
}
