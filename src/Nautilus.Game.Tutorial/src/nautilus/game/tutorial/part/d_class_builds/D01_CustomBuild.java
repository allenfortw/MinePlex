package nautilus.game.tutorial.part.d_class_builds;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class D01_CustomBuild extends Part
{	
	private boolean _prox = false;
	
	public D01_CustomBuild(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "Are you ready for some fun!?"));
		Add(new Dialogue(this, "It's time for some sexy " + F.te("Class Custom Builds") + "!"));
		
		Add(new Pause(this, 2000));
		
		Add(new Dialogue(this, "Alright! Where to begin...")); 
		Add(new Dialogue(this, "When you " + F.ta("Left-Click") + " armor in the " + F.te("Class Setup Table") + ","));
		Add(new Dialogue(this, "you receive the " + F.te("Default Build") + " for that " + F.te("Class") + "."));
		Add(new Dialogue(this, "This is a pre-made setup for each " + F.te("Class") + "."));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "However, you can create your own " + F.te("Custom Builds") + "!"));
		
		Add(new Dialogue(this, "Let's create a new " + F.te("Custom Build") + "."));	
		
		Add(new Index(this, "Search"));

		Add(new Dialogue(this, "Head on down to the " + F.te("Class Setup Table") + "."));
		Add(new ForceLook(this, Manager.classSetup, 0));
		Add(new AllowAction(this, true));

		Add(new Pause(this, 20000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		
		Add(new IndexJump(this, "Search"));
		
		Add(new Index(this, "Proximity"));
		
		Add(new Complete(this)); 
	}

	@EventHandler
	public void UpdateProximity(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (!AllowAction())
			return;

		if (_prox)
			return;

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.classSetup) < 3)
		{
			IndexJump("Proximity");
			_prox = true;
		}
	}

	
	@Override
	public Part GetNext() 
	{
		return new D02_InfernoBuild(Manager, Data, GetPlayer());
	}
}
