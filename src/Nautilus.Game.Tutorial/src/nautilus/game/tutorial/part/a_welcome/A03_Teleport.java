package nautilus.game.tutorial.part.a_welcome;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class A03_Teleport extends Part
{	
	public A03_Teleport(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "Fantastic!"));
		Add(new Dialogue(this, "Now that you've eaten some 'food' we can start."));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "I'm going to teleport you to " + F.te("Tutorial Islands") + "."));
		Add(new Dialogue(this, "The weather is beautiful there."));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "System", "Commencing teleport in;"));
		Add(new Dialogue(this, "System", "3..."));
		Add(new Dialogue(this, "System", "2..."));
		Add(new Dialogue(this, "System", "1..."));
		Add(new SoundEffect(this, Sound.ZOMBIE_UNFECT, 2f, 2f));
		
		Add(new Teleport(this, Manager.spawnB));
		Add(new Pause(this, 1000));
		Add(new Dialogue(this, "Uhh..."));
		Add(new Pause(this, 2500));
		
		Add(new Teleport(this, Manager.spawnC));
		Add(new Pause(this, 4000));
		Add(new Dialogue(this, "Much better."));
		Add(new Dialogue(this, "Lets get straight into it!"));
		Add(new Pause(this, 2000));
		
		Add(new Complete(this));
	}
	
	@Override
	public Part GetNext() 
	{
		return new A04_Tutorials(Manager, Data, GetPlayer());
	}
}
