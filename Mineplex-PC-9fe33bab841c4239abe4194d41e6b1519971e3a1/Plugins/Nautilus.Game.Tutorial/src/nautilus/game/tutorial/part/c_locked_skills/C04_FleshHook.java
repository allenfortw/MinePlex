package nautilus.game.tutorial.part.c_locked_skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.events.SkillEvent;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class C04_FleshHook extends Part
{	
	private int _hooked = 0;
	
	public C04_FleshHook(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "System", "Inserting Flesh Hook..."));
		Manager.Clients().Get(GetPlayer()).Class().AddSkill("Flesh Hook", 3);
		 
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "Your Swords Active Skill is now " + F.ts("Flesh Hook") + "."));
		Add(new Dialogue(this, "This allows you to pull enemies towards you!"));
		
		Add(new Index(this, "1"));
		
		Add(new Dialogue(this, F.ta("Hold Right-Click") + " to charge up " + F.ts("Flesh Hook") + "."));
		Add(new Dialogue(this, "Then " + F.ta("Release Right-Click") + " to throw it!"));

		Add(new AllowAction(this, true));
		Add(new Dialogue(this, "Try hooking some of those wooly bastards!"));
		
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "1"));
		
		Add(new Index(this, "2"));
		
		Add(new Dialogue(this, "Ahhhh, fresh meat!"));
		
		Add(new Index(this, "3"));
		
		Add(new Dialogue(this, "Torture a few more of these disgusting sheep."));
		Add(new Pause(this, 20000));
		Add(new Dialogue(this, "Give it a few more tries!"));	
		Add(new Pause(this, 20000));
		Add(new Dialogue(this, "Yawn..."));
		Add(new Pause(this, 20000));
		
		Add(new IndexJump(this, "3"));
		
		Add(new Index(this, "4"));
		
		Add(new Dialogue(this, "You're getting good at this!"));
		
		Add(new Complete(this));
	}
	
	@EventHandler
	public void FleshHook(SkillEvent event)
	{
		if (!AllowAction())
			return;
				
		if (!event.GetSkillName().equals("Flesh Hook"))
			return;
		
		if (!event.GetPlayer().getName().equals(GetPlayerName()))
			return;
		
		if (_hooked == 0)
		{
			_hooked++;
			IndexJump("2");
		}
		else if (_hooked == 3) 
		{
			_hooked++;
			IndexJump("4");
		}
		else 
		{
			_hooked++;
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		//Recharge Dwarf Toss
		Manager.Clients().Get(GetPlayerName()).Game().GetRecharge().remove("Flesh Hook");
	}
	
	@Override
	public Part GetNext() 
	{
		return new C05_LockedSkills(Manager, Data, GetPlayer());
	}
}
