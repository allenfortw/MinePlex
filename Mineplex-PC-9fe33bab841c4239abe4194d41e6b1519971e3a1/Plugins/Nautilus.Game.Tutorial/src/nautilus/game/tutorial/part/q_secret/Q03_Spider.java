package nautilus.game.tutorial.part.q_secret;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class Q03_Spider extends Part
{	
	private long _timer = 0;
	private boolean _done = false;

	public Q03_Spider(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
		_timer = System.currentTimeMillis();
	}

	@Override
	public void CreateActions() 
	{
		Add(new SoundEffect(this, Sound.SPIDER_IDLE, 5f, 1f));
		Add(new Pause(this, 500));
		Add(new SoundEffect(this, Sound.SPIDER_IDLE, 5f, 1f));
		Add(new Pause(this, 500));
		Add(new SoundEffect(this, Sound.SPIDER_IDLE, 5f, 1f));
		Add(new Pause(this, 1000));
		Add(new SoundEffect(this, Sound.NOTE_PLING, 2f, 2f));
		
		Add(new Index(this, "Loop"));
		Add(new IndexJump(this, "Loop"));
		
		Add(new Index(this, "Failure"));
		Add(new SoundEffect(this, Sound.NOTE_BASS_GUITAR, 2f, 2f));
		Add(new EndTutorial(this));
		
		Add(new Index(this, "Proximity"));
		Add(new Complete(this));
	}

	@EventHandler
	public void UpdateProximity(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (_done)
			return;

		if (UtilTime.elapsed(_timer, 60000))
		{
			IndexJump("Failure");
			_done = true;
		}

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.secChick) < 2)
		{
			IndexJump("Proximity");
			_done = true;
		}
	}

	@Override
	public Part GetNext() 
	{
		return new Q04_Chicken(Manager, Data, GetPlayer());
	}
}
