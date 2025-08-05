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

public class C03_BlockToss extends Part
{	
	private int _throw = 0;
	
	public C03_BlockToss(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Dialogue(this, "System", "Inserting Block Toss..."));
		Manager.Clients().Get(GetPlayer()).Class().AddSkill("Block Toss", 3);
		 
		Add(new Pause(this, 1000));
		
		Add(new Dialogue(this, "Your Swords Active Skill is now " + F.ts("Block Toss") + "."));
		Add(new Dialogue(this, "This allows you to pick up blocks and throw them!"));
		
		Add(new Index(this, "1")); 
		
		Add(new Dialogue(this, F.ta("Hold Right-Click") + " to pick blocks up."));
		Add(new Dialogue(this, "Then " + F.ta("Release Right-Click") + " to throw!"));
		
		Add(new AllowAction(this, true));
		Add(new Dialogue(this, "Give it a try!"));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "1"));
		
		Add(new Index(this, "2"));
		
		Add(new Dialogue(this, "Well done!"));
		Add(new Dialogue(this, "You can use this to attack enemies, or to modify terrain!"));
		
		Add(new Index(this, "3"));
		
		Add(new Dialogue(this, "Try it a few more times, then we'll move on."));
		Add(new Pause(this, 20000));
		Add(new Dialogue(this, "Just throw a couple more blocks. It's good fun!"));	
		Add(new Pause(this, 20000));
		Add(new Dialogue(this, "I don't have all day..."));
		Add(new Pause(this, 20000));
		
		Add(new IndexJump(this, "3"));
		
		Add(new Index(this, "4"));
		
		Add(new Dialogue(this, "Perfect, you're a natural!"));
		
		Add(new Complete(this));
	}
	
	@EventHandler
	public void BlockToss(SkillEvent event)
	{
		if (!AllowAction())
			return;
				
		if (!event.GetSkillName().equals("Block Toss"))
			return;
		
		if (!event.GetPlayer().getName().equals(GetPlayerName()))
			return;
		
		if (_throw == 0)
		{
			_throw++;
			IndexJump("2");
		}
		else if (_throw == 5) 
		{
			_throw++;
			IndexJump("4");
		}
		else 
		{
			_throw++;
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		//Recharge Dwarf Toss
		Manager.Clients().Get(GetPlayerName()).Game().GetRecharge().remove("Block Toss");
	}
	
	@Override
	public Part GetNext() 
	{
		return new C04_FleshHook(Manager, Data, GetPlayer());
	}
}
