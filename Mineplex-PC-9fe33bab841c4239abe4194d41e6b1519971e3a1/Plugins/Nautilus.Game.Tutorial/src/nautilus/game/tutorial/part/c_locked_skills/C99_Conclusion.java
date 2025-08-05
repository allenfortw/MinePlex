package nautilus.game.tutorial.part.c_locked_skills;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class C99_Conclusion extends Part
{	
	public C99_Conclusion(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		//Intro
		Add(new Dialogue(this, "All done here!"));
		Add(new Dialogue(this, "You've finished the " + F.te("Locked Skills") + " tutorial!"));
		Add(new Dialogue(this, "The next tutorial is " + F.te("Custom Builds") + "."));
		
		Add(new CompleteTutorial(this));
	}
	
	@Override
	public Part GetNext() 
	{
		return null;
	}
}
