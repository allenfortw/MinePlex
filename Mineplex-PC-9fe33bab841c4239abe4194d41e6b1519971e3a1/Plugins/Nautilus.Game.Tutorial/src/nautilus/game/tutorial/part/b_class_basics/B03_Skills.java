package nautilus.game.tutorial.part.b_class_basics;

import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B03_Skills extends Part
{	
	public B03_Skills(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "You're doing great, I'm so proud."));
		Add(new Dialogue(this, "We should have a party to celebrate!"));
		
		
		Add(new Pause(this, 3000));
		Add(new Dialogue(this, "Just kidding, you're actually doing very badly."));
		Add(new Dialogue(this, "Anyway..."));
		Add(new Dialogue(this, "Each " + F.te("Class") + " has its own set of Skills."));
		Add(new Dialogue(this, "Skills can be " + F.te("Active") + " or " + F.te("Passive") + "."));
		Add(new Dialogue(this, "You use " + F.te("Active Skills") + " by clicking with weapons."));
		Add(new Dialogue(this, "However, " + F.te("Passive Skills") + " work automatically."));
		
		Add(new Pause(this, 2000));
		
		Add(new Dialogue(this, "Let's look at your equipped " + F.te("Skills") + "."));
		Add(new Dialogue(this, "In your inventory, there will be a row of items."));	
		Add(new Dialogue(this, "This shows you what " + F.te("Skills") + " you have, and what they do."));	
		
		Add(new Dialogue(this, "I'll give you " + F.ta("20 Seconds") + " to have a look."));
		
		Add(new Pause(this, 20000));
		
		Add(new Dialogue(this, "You can view your " + F.te("Skills") + " like this, at any time."));
		
		Add(new Complete(this));
	}
	
	@Override
	public Part GetNext() 
	{
		return new B04_DwarfToss(Manager, Data, GetPlayer());
	}
}
