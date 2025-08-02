package nautilus.game.tutorial.part.a_welcome;

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

public class A04_Tutorials extends Part
{	
	private boolean _prox = false;
	
	public A04_Tutorials(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "Welcome to " + F.te("Tutorial Islands") + "."));
		Add(new Dialogue(this, "Here you can do a number of different tutorials."));
		
		Add(new Pause(this, 1000));
		
		Add(new Index(this, "1"));
		
		Add(new Dialogue(this, "Head over to the " + F.te("Tutorial Minions") + "."));
		Add(new ForceLook(this, Manager.tutorials, 0));
		
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "1"));
		
		Add(new Index(this, "2"));

		Add(new Dialogue(this, "These are my minons, they do whatever I say."));
		Add(new Dialogue(this, "Simply " + F.ta("Right-Click") + " them to start a tutorial."));
		Add(new Dialogue(this, "Good luck!"));
		
		Add(new CompleteTutorial(this));
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

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.tutorials) < 8)
		{
			IndexJump("2");
			_prox = true;
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return null;
	}
}
