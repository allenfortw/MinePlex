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

public class Z04_Resupply extends Part
{	
	private boolean _prox = false;
	
	public Z04_Resupply(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "Another " + F.te("Powerup") + " is " + F.te("Resupply Powerups") + "."));
		Add(new Dialogue(this, "When you collect one, it replenishes your " + F.te("Class Build") + "."));
		Add(new Dialogue(this, "So if you run out of arrows, you can just resupply!"));

		Add(new Index(this, "Search"));
		
		Add(new Dialogue(this, "An example " + F.te("Resupply Powerup") + " is just over here."));
		Add(new ForceLook(this, Manager.domResupply, 0));
		Add(new Dialogue(this, "Head to it, but be careful not to fall!"));
		Add(new AllowAction(this, true));

		Add(new Pause(this, 20000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "Search"));
		
		Add(new Index(this, "Proximity"));
		
		Add(new Dialogue(this, "After being collected, it takes " + F.ta("1 Minute") + " to regenerate."));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "While regenerating, the " + F.te("Gold Block") + " changes to " + F.te("Iron Block") + "."));
		Add(new Dialogue(this, "Use this to tell if its regenerating from a long distance."));
		
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

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.domResupply) < 2)
		{
			IndexJump("Proximity");
			_prox = true;
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return new Z99_Conclusion(Manager, Data, GetPlayer());
	}
}
