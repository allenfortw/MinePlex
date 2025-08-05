package nautilus.game.tutorial.part.z_domination;

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

public class Z01_Introduction extends Part
{	
	private boolean _prox = false;
	
	public Z01_Introduction(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "Hello."));
		Add(new Dialogue(this, "Let's learn about " + F.te("Domination") + "!"));
		
		Add(new Index(this, "Search"));
		
		Add(new Dialogue(this, "Let's start by heading over to " + F.te("Domination Island") + "."));
		Add(new ForceLook(this, Manager.domIsland, 0));
		Add(new AllowAction(this, true));

		Add(new Pause(this, 20000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "Search"));
		
		Add(new Index(this, "Proximity"));

		Add(new Dialogue(this, "Great!"));
		Add(new Dialogue(this, F.te("Domination") + " is a competitve 5v5 game!"));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "It uses " + F.te("Classes") + " and " + F.te("Skills") + "."));
		Add(new Dialogue(this, "You can choose your " + F.te("Class Build") + " at your teams base."));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "The goal is to get " + F.ta("15000 Score") + " before the enemy team."));
		Add(new Dialogue(this, "You can receive " + F.ta("Score") + " in a number of ways..."));
		Add(new Dialogue(this, "When you kill someone, your team receives " + F.ta("25 Score") + "."));
		
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

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.domIsland) < 15)
		{
			IndexJump("Proximity");
			_prox = true;
		}
	}

	
	@Override
	public Part GetNext() 
	{
		return new Z02_CapturePoints(Manager, Data, GetPlayer());
	}
}
