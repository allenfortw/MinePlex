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

public class Z02_CapturePoints extends Part
{	
	private boolean _prox = false;
	
	public Z02_CapturePoints(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "However, the best way " + F.ta("Score") + " is " + F.te("Control Points") + "."));
		Add(new Dialogue(this, "Every map has a number of " + F.te("Control Points") + " around it."));
		Add(new Dialogue(this, "You can easily see where they are, by the " + F.te("Beacon Signal") + "."));
		Add(new Dialogue(this, "For each " + F.te("Control Points") + " your team controls,"));
		Add(new Dialogue(this, "you receive " + F.ta("5 Score") + " per second."));

		Add(new Index(this, "Search"));
		
		Add(new Dialogue(this, "There is an example " + F.te("Control Point") + " over here."));
		Add(new ForceLook(this, Manager.domCP, 0));
		Add(new Dialogue(this, "Move onto it now."));
		Add(new AllowAction(this, true));

		Add(new Pause(this, 20000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "Search"));
		
		Add(new Index(this, "Proximity"));

		Add(new Dialogue(this, "When you stand on a " + F.te("Control Point") + ", you capture it for your team."));
		Add(new Dialogue(this, "It takes a short time for it to convert to your team."));
		Add(new Dialogue(this, "Once it turns your teams color, it has been captured!"));
		Add(new Dialogue(this, "After that, you can leave the " + F.te("Control Point") + "."));
		
		Add(new Pause(this, 2000));
		
		Add(new Dialogue(this, "Defending a " + F.te("Control Point") + " grants extra " + F.ta("1 Score") + " per second."));
		Add(new Dialogue(this, "To defend, just stay on a " + F.te("Control Point") + " you already own."));
		
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

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.domCP) < 3)
		{
			IndexJump("Proximity");
			_prox = true;
		}
	}

	
	@Override
	public Part GetNext() 
	{
		return new Z03_Emerald(Manager, Data, GetPlayer());
	}
}
