package nautilus.game.tutorial.part.b_class_basics;

import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.minecraft.game.classcombat.events.SkillEvent;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B04_DwarfToss extends Part
{	
	public HashSet<Entity> _sheep = new HashSet<Entity>();
	
	public B04_DwarfToss(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Your swords " + F.te("Active Skill") + " is " + F.ts("Dwarf Toss") + "."));
		Add(new Dialogue(this, "This allows you to pick something up and throw it!"));
		Add(new Dialogue(this, F.ta("Hold Right-Click") + " to pick it up."));
		Add(new Dialogue(this, "Then " + F.ta("Release Right-Click") + " to throw!"));
		
		Add(new Dialogue(this, "Lets give it a try..."));
		Add(new Dialogue(this, "See those sheep over there?"));
		Add(new ForceLook(this, Manager.sheepPit, 0));
		
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "I hate sheep..."));
		Add(new Dialogue(this, "Throw one off the island for me!"));
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, "You can do it, just toss a sheep away."));
		
		Add(new Pause(this, 10000));
		Add(new Dialogue(this, "They don't have souls, i promise!"));
		
		Add(new Pause(this, 10000));
		Add(new Dialogue(this, "Do you like sheep or something...?"));
		
		Add(new Pause(this, 10000));
		Add(new AllowAction(this, false));
		Add(new Dialogue(this, "You must be confused..."));
		Add(new Dialogue(this, "Let's start over!"));
		
		Add(new IndexJump(this, "Start"));
		
		Add(new Index(this, "End"));
		
		Add(new Dialogue(this, "YES! Goodbye you wooly freak."));
		Add(new Dialogue(this, "Oh " + GetPlayerName() + ", I think we could be good friends!"));
		
		Add(new Complete(this));
	}
	
	@EventHandler
	public void DwarfToss(SkillEvent event)
	{
		if (!AllowAction())
			return;
		
		if (event.GetTargets() == null)
			return;
		
		if (event.GetTargets().size() != 1)
			return;
		
		if (!event.GetSkillName().equals("Dwarf Toss"))
			return;
		
		if (!event.GetPlayer().getName().equals(GetPlayerName()))
			return;
		
		Entity ent = event.GetTargets().get(0);
		
		if (!(ent instanceof Sheep))
			return;
		
		_sheep.add(ent);
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		//Recharge Dwarf Toss
		Manager.Clients().Get(GetPlayerName()).Game().GetRecharge().remove("Dwarf Toss");
		
		HashSet<Entity> remove = new HashSet<Entity>();
		
		for (Entity cur : _sheep)
			if (cur.isDead() || !cur.isValid() || (UtilEnt.isGrounded(cur) && cur.getVehicle() == null))
				remove.add(cur);
			
		for (Entity cur : remove)
			_sheep.remove(cur);
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent event)
	{
		if (!AllowAction())
			return;
		
		if (event.getCause() != DamageCause.VOID)
			return;
		
		if (!(event.getEntity() instanceof Sheep))
			return;

		if (!_sheep.contains(event.getEntity()))
			return;

		SetAllowAction(false);
		IndexJump("End");
	}
	
	@Override
	public Part GetNext() 
	{
		return new B05_SeismicSlam(Manager, Data, GetPlayer());
	}
}
