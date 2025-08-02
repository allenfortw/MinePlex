package nautilus.game.tutorial.part.z_domination;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class Z99_Conclusion extends Part
{	
	public Z99_Conclusion(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		//Intro
		Add(new Dialogue(this, "You can play " + F.te("Domination") + " now, at " + F.te("dom.BetterMC.com") + "!"));
		Add(new Dialogue(this, "Good luck, and may you win all of the games!"));
		
		Add(new CompleteTutorial(this));
	}
	
	@Override
	public Part GetNext() 
	{
		return null;
	}
}
