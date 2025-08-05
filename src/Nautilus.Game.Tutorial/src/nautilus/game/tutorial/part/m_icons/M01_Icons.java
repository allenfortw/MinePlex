package nautilus.game.tutorial.part.m_icons;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class M01_Icons extends Part
{	
	public M01_Icons(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "These are " + F.te("Condition Icons") + "!"));
		Add(new Dialogue(this, "When something has a " + F.te("Condition") + ",")); 
		Add(new Dialogue(this, "one of these is displayed above their head!"));
		Add(new Dialogue(this, "This lets you see what " + F.te("Conditions") + " they have."));
		Add(new Dialogue(this, "Have a look at all the different types."));
		Add(new EndTutorial(this)); 
	}
	
	@Override
	public Part GetNext() 
	{
		return null;
	}
}
