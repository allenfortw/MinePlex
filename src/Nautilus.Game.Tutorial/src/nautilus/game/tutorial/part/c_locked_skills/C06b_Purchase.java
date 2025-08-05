package nautilus.game.tutorial.part.c_locked_skills;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class C06b_Purchase extends Part
{	
	public boolean browsed = false;

	public C06b_Purchase(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions()  
	{
		Add(new Dialogue(this, "Oh..."));
		Add(new Pause(this, 1000));
		Add(new Dialogue(this, "Whats this..?"));
		Add(new Pause(this, 1000));
		Add(new Dialogue(this, "It seems have already unlocked " + F.te("Inferno") + "."));
		Add(new Pause(this, 1000));
		Add(new Dialogue(this, "I guess I don't need to teach you how to unlock Skills."));
		Add(new Dialogue(this, "Lucky me!"));
		
		Add(new Complete(this));
	}

	@Override
	public Part GetNext() 
	{
		return new C99_Conclusion(Manager, Data, GetPlayer());
	}
}
