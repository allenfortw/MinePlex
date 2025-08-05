package nautilus.game.tutorial.part.b_class_basics;

import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;

import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.events.SkillEvent;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B06_Stampede extends Part
{	
	public HashSet<Entity> _sheep = new HashSet<Entity>();
	
	public boolean _speedDone = false;
	
	public B06_Stampede(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "One of your " + F.te("Passive Skills") + " is " + F.ts("Stampede") + "."));
		Add(new Dialogue(this, F.ts("Stampede") + " will give you " + F.te("Speed") + " after sprinting for a while."));
		Add(new Dialogue(this, "Go for a little run and get some Speed!"));
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, "Need me to explain it again?"));
		Add(new IndexJump(this, "Start"));
		
		
		Add(new Index(this, "Speed"));
		Add(new Dialogue(this, "Easy, huh?! But it gets even better!"));
		
		
		
		Add(new Index(this, "Attack"));

		Add(new Dialogue(this, "Gain some Speed, then attack something."));
		Add(new Dialogue(this, "It'll send them flying!"));
		Add(new AllowAction(this, true));
		
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		
		Add(new IndexJump(this, "Attack"));
		
		
		
		Add(new Index(this, "End1"));
		Add(new Dialogue(this, "Mmmmmm... yes..."));
		Add(new Dialogue(this, "I'm so proud of you, I may cry."));
		Add(new Pause(this, 2000));
		Add(new Complete(this));
		
		
		
		Add(new Index(this, "End2"));
		Add(new Dialogue(this, "That wasn't a sheep... but... well done anyway."));
		Add(new Pause(this, 2000));
		Add(new Complete(this));
	}
	
	@EventHandler
	public void Stampede(SkillEvent event)
	{
		if (!AllowAction())
			return;
		
		if (!event.GetSkillName().equals("Stampede"))
			return;
		
		if (!event.GetPlayer().getName().equals(GetPlayerName()))
			return;

		if (event.GetTargets() == null)
		{
			if (_speedDone)
				return;
			
			IndexJump("Speed");
			SetAllowAction(false);
			_speedDone = true;
		}
		else
		{
			if (event.GetTargets().get(0) instanceof Sheep)
			{
				IndexJump("End1");
				SetAllowAction(false);
			}
			else
			{
				IndexJump("End2");
				SetAllowAction(false);
			}
		}
	} 
	
	@Override
	public Part GetNext() 
	{
		return new B07_CripplingBlow(Manager, Data, GetPlayer());
	}
}
