package nautilus.game.tutorial.part.b_class_basics;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.events.ClassSetupEvent;
import mineplex.minecraft.game.classcombat.events.ClassSetupEvent.SetupType;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B01_ClassSelection extends Part
{	
	public B01_ClassSelection(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		//Intro
		Add(new Dialogue(this, "Hello again, " + GetPlayerName() + "!"));
		Add(new Dialogue(this, "Ready to learn about " + F.te("Classes") + " and " + F.te("Default Builds") + "?"));
		Add(new Dialogue(this, "Yes?"));
		Add(new Dialogue(this, "Good!"));
		
		Add(new Dialogue(this, "There are five different Classes available to you."));
		Add(new Dialogue(this, "Knight."));
		Add(new Dialogue(this, "Ranger."));
		Add(new Dialogue(this, "Brute."));
		Add(new Dialogue(this, "Assassin."));
		Add(new Dialogue(this, "Mage."));
		Add(new Dialogue(this, "Each of them have their own unique abilities."));
		
		Add(new Pause(this, 3000));
				
		Add(new Dialogue(this, "See that " + F.te("Enchantment Table") + "?"));
		Add(new ForceLook(this, Manager.classSetup, 0));
		
		Add(new Dialogue(this, "Well, it's not an " + F.te("Enchantment Table") + "."));
		Add(new Dialogue(this, "Thats a " + F.te("Class Setup Table") + "."));
	
		Add(new Dialogue(this, "You can use it to choose your " + F.te("Class") + "."));
		
		//Armor Set
		Add(new Index(this, "Armor Set"));
		
		Add(new Dialogue(this, F.te(C.cGreen + "Right-Click") + " the " + F.te("Class Setup Table") + "."));
		Add(new Dialogue(this, "Then " + F.te(C.cGreen + "Left-Click") + " any of the armor sets."));
		Add(new AllowClassSetup(this, true));
		
		Add(new Pause(this, 20000));
		
		Add(new Dialogue(this, Dialogue.restartMessages));
		
		Add(new ForceLook(this, Manager.classSetup, 0));
		
		Add(new IndexJump(this, "Armor Set"));
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
			event.GetPlayer().closeInventory();	
			SetCompleted(true);
		} 
				
		else
		{
			event.GetPlayer().closeInventory();
			IndexJump("Armor Set");
			Dialogue(event.GetPlayer(), "Wrong! Try again!");
			event.SetCancelled(true);
		} 
	}
	
	@Override
	public Part GetNext() 
	{
		return new B02_BruteClass(Manager, Data, GetPlayer());
	}
}
