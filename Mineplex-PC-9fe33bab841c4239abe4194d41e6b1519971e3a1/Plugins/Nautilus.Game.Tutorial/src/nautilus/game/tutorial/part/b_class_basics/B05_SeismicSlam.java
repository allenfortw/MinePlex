package nautilus.game.tutorial.part.b_class_basics;

import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.events.SkillEvent;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B05_SeismicSlam extends Part
{	
	public HashSet<Entity> _sheep = new HashSet<Entity>();
	
	public B05_SeismicSlam(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Now let's look at your axe ability, " + F.ts("Seismic Slam") + "."));
		Add(new Dialogue(this, "Just " + F.ta("Right-Click") + " with your axe to do it!"));
		Add(new Dialogue(this, "You jump up and then slam down into the ground."));
		Add(new Dialogue(this, "This sends all nearby enemies flying!"));
		
		Add(new Dialogue(this, "You know what to do..."));
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, "Ok, maybe you don't know what to do."));
		Add(new Dialogue(this, "Use " + F.te("Seismic Slam") + " on some sheep!"));
		 
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, "You can do it! I beleive in you!"));
		
		Add(new Pause(this, 10000));
		Add(new Dialogue(this, "Come on " + GetPlayerName() + "!"));
		
		Add(new Pause(this, 10000));
		Add(new AllowAction(this, false));
		Add(new Dialogue(this, "I guess we'll try that again..."));
		
		Add(new IndexJump(this, "Start"));
		
		Add(new Index(this, "End"));
		
		Add(new Pause(this, 1000));
		Add(new Dialogue(this, "Weeeeeeeee!"));
		Add(new Dialogue(this, "You're a natural!"));
				
		Add(new Pause(this, 2000));
		
		Add(new Dialogue(this, "Lets look at your " + F.ta("Passive Skills") + " now!"));
		
		Add(new Complete(this));
	}
	
	@EventHandler
	public void SeismicSlam(SkillEvent event)
	{
		if (!AllowAction())
			return;
		
		if (event.GetTargets() == null)
			return;
		
		if (!event.GetSkillName().equals("Seismic Slam"))
			return;
		
		if (!event.GetPlayer().getName().equals(GetPlayerName()))
			return;
		
		for (Entity ent : event.GetTargets())
			if (ent instanceof Sheep)
			{
				SetAllowAction(false);
				IndexJump("End");
			}			
	}
	 
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		//Recharge Dwarf Toss
		Manager.Clients().Get(GetPlayerName()).Game().GetRecharge().remove("Seismic Slam");
	}
	
	@Override
	public Part GetNext() 
	{
		return new B06_Stampede(Manager, Data, GetPlayer());
	}
}
