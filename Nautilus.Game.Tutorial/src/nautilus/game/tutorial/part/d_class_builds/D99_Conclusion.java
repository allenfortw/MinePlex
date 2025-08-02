package nautilus.game.tutorial.part.d_class_builds;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class D99_Conclusion extends Part
{	
	public D99_Conclusion(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		//Intro
		Add(new Dialogue(this, "Congratulations!"));
		Add(new Dialogue(this, "You've finished all of the " + F.ta("Core Tutorials") + "!"));
		
		Add(new CompleteTutorial(this));
	}
	
	@Override
	public Part GetNext() 
	{
		return null;
	}
}
