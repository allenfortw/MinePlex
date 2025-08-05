package nautilus.game.tutorial.part.d_class_builds;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;

import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class D04_Inferno extends Part
{	
	private int _burns = 0;
	
	public D04_Inferno(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, F.ts("Inferno") + " is a " + F.te("Sword Skill") + "."));
		Add(new Dialogue(this, "Simply " + F.ta("Hold Right-Click") + " with your sword use it!"));
		Add(new Dialogue(this, "It breathes out a flurry of fire that ignites players."));
		
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Burn some of those wooly bastards for me!"));
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new IndexJump(this, "Start"));
		
		Add(new Index(this, "End"));
		
		Add(new Dialogue(this, "Ohhhh..."));
		Add(new Dialogue(this, "Mmm...."));
		Add(new Dialogue(this, "That was great.")); 
		
		Add(new Complete(this));
	}
		
	@EventHandler
	public void InfernoDamage(CustomDamageEvent event)
	{
		if (!AllowAction())
			return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)
			return;

		if (!(event.GetDamageeEntity() instanceof Sheep))
			return;

		if (!damager.equals(GetPlayer()))
			return;

		if (!event.GetReason().contains("Inferno"))
			return;

		_burns++;
		
		if (_burns == 15)
			IndexJump("End");
	}
	 
	@Override
	public Part GetNext() 
	{
		return new D05_Tokens(Manager, Data, GetPlayer());
	}
}
