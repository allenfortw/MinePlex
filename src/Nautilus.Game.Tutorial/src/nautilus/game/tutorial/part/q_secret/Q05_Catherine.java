package nautilus.game.tutorial.part.q_secret;

import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class Q05_Catherine extends Part
{	
	private boolean _done = false;

	public Q05_Catherine(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new SoundEffect(this, Sound.NOTE_PLING, 2f, 2f));
		Add(new Pause(this, 1000));
		Add(new SoundEffect(this, Sound.ZOMBIE_UNFECT, 2f, 2f));
		Add(new Teleport(this, Manager.spawnB));
		
		Add(new Index(this, "Loop"));
		Add(new IndexJump(this, "Loop"));
		
		Add(new Index(this, "Proximity"));
		
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 0.4f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 0.5f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 0.6f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 0.7f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 0.8f)); 
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 0.9f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.0f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.2f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.3f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.4f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.5f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.6f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.7f));	
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.8f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 1.9f));
		Add(new SoundEffect(this, Sound.GHAST_SCREAM, 0.4f, 2.0f));
		
		Add(new AllowAction(this, true));
	}

	@EventHandler
	public void UpdateProximity(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		if (AllowAction())
		{
			UtilPlayer.kick(GetPlayer(), "Catherine", "...");
		}
		
		if (_done)
		{
			GetPlayer().playEffect(EntityEffect.HURT);
			return;
		}
			

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.secCat) < 6)
		{
			for (int i=0 ; i<20 ; i++)
				GetPlayer().playSound(GetPlayer().getLocation(), Sound.ENDERDRAGON_DEATH, 1f, 1f);
			
			IndexJump("Proximity");
			_done = true;
		}
	}

	@Override
	public Part GetNext() 
	{
		return null;
	}
}
