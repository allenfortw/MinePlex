package nautilus.game.tutorial.part.c_locked_skills;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class C02_Slots extends Part
{	
	public C02_Slots(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "Good stuff!"));
		
		Add(new Pause(this, 2000));

		Add(new Dialogue(this, "Have a look at your " + F.te("Skills") + " again."));
		Add(new Dialogue(this, "Do this by opening your inventory."));
		
		Add(new Dialogue(this, "Each " + F.te("Class") + " has 7 or 8 " + F.te("Skill Slots") + "."));
		Add(new Dialogue(this, F.te("Brute") + " has 7 Skill Slots;"));
		Add(new Dialogue(this, "Two " + F.te("Active Slots") + "; Sword and Axe"));
		Add(new Dialogue(this, "Two " + F.te("Class Passive Slots") + "."));
		Add(new Dialogue(this, "Three " + F.te("Global Passive Slots") + "."));
		Add(new Dialogue(this, "You may only have one Skill in each Slot Type."));
		
		Add(new Dialogue(this, "I'll give you " + F.ta("10 Seconds") + " to have a look."));
		
		Add(new Pause(this, 10000));
		
		Add(new Dialogue(this, "Each " + F.te("Class") + " has a selection of " + F.te("Skills") + " for each " + F.te("Skill Slot") + "."));	
		Add(new Dialogue(this, "You have to pick and choose which " + F.te("Skills") + " you want to use!"));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "Let's try out some other " + F.te("Sword Skills") + " for " + F.te("Brute") + "!"));
		
		Add(new Complete(this));
	}

	@Override
	public Part GetNext() 
	{
		return new C03_BlockToss(Manager, Data, GetPlayer());
	}
}
