package nautilus.game.tutorial.part.c_locked_skills;

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

public class C01_Brute extends Part
{	
	public C01_Brute(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{	
		Add(new Dialogue(this, "Meow!"));
		Add(new Dialogue(this, "Let's learn about the " + F.te("Class Shop") + " and " + F.te("Skill Unlocks") + "!"));
		
		Add(new Pause(this, 2000));
		
		Add(new Dialogue(this, "In the last tutorial, you chose " + F.te("Class Default Build") + "."));
		Add(new Dialogue(this, "This gives you a pre-selected group of " + F.te("Skills") + "."));	
		Add(new Dialogue(this, "However, there are a lot more " + F.te("Skills") + " for each " + F.te("Class") + "!"));
		Add(new Dialogue(this, "You will get to choose which " + F.te("Skills") + " you want to use."));
		
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Let's start by choosing " + F.te("Brute Default Build") + " again."));
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
		return new C02_Slots(Manager, Data, GetPlayer());
	}
}
