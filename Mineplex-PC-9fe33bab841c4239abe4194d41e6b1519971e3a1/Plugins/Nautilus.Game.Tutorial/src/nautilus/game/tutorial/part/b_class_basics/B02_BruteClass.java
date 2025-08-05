package nautilus.game.tutorial.part.b_class_basics;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.events.ClassSetupEvent;
import mineplex.minecraft.game.classcombat.events.ClassSetupEvent.SetupType;
import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B02_BruteClass extends Part
{	
	public B02_BruteClass(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{	
		Add(new Dialogue(this, "Fantastic!"));
		
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Now do it again, and choose " + F.te("Brute Default Build") + "."));
		Add(new AllowClassSetup(this, true));
		Add(new Pause(this, 20000));
		Add(new IndexJump(this, "Start"));
	}
	
	@EventHandler
	public void ClassSetup(ClassSetupEvent event)
	{
		if (!event.GetPlayer().equals(GetPlayer()))
			return;
		
		if (event.GetType() == SetupType.OpenMain)
			return;
		
		if (event.GetType() == SetupType.ApplyDefaultBuilt)
		{
			if (event.GetClassType() == ClassType.Brute)
			{ 
				event.GetPlayer().closeInventory();
				SetCompleted(true);
			}
			else
			{
				event.GetPlayer().closeInventory();
				SetIndex(1);
				Dialogue(event.GetPlayer(), "That's not the " + F.te("Brute Default Build") + "!");
				event.SetCancelled(true);
			}
		} 
				 
		else
		{ 
			event.GetPlayer().closeInventory();
			SetIndex(1);
			Dialogue(event.GetPlayer(), "Nope!");
			event.SetCancelled(true);
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return new B03_Skills(Manager, Data, GetPlayer());
	}
}
