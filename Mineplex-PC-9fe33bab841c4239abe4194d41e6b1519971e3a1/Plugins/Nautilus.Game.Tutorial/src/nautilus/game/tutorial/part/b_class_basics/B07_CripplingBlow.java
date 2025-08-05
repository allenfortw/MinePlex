package nautilus.game.tutorial.part.b_class_basics;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;

import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.events.SkillEvent;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class B07_CripplingBlow extends Part
{	
	private int _done = 0;

	public B07_CripplingBlow(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Index(this, "1"));

		Add(new Dialogue(this, "Another of your " + F.te("Passive Skills") + " is " + F.ts("Crippling Blow") + "."));
		Add(new Dialogue(this, "Your " + F.ta("Axe") + " attacks will " + F.te("Slow") + " the target for a short time."));
		Add(new Dialogue(this, "Try it on some sheep."));
		Add(new AllowAction(this, true));

		Add(new Pause(this, 20000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "1"));

		Add(new Index(this, "2"));

		Add(new Dialogue(this, "SPLAT!"));

		Add(new Index(this, "3"));

		Add(new Dialogue(this, "Please hit a few more sheep with " + F.ts("Crippling Blow") + "!"));
		Add(new Pause(this, 20000));

		Add(new IndexJump(this, "3"));

		Add(new Index(this, "4"));

		Add(new Dialogue(this, "Well done!"));
		Add(new Dialogue(this, "Did you notice a " + F.te("Spider Web") + " appears above the sheep?"));
		Add(new Dialogue(this, "This is a " + F.te("Condition Icon") + "."));
		Add(new Dialogue(this, "It is showing that the sheep has " + F.te("Slow") + "."));
		Add(new Dialogue(this, "Many different conditions have " + F.te("Condition Icons") + "."));
		Add(new Dialogue(this, "You use them to see what conditions someone has!"));

		Add(new Complete(this));
	}

	@EventHandler
	public void CripplingBlow(SkillEvent event)
	{
		if (!AllowAction())
			return;

		if (!event.GetSkillName().equals("Crippling Blow"))
			return;

		if (!event.GetPlayer().getName().equals(GetPlayerName()))
			return;
		
		if (event.GetTargets() == null)
			return;
		
		if (!(event.GetTargets().get(0) instanceof Sheep))
			return;
		
		if (_done == 0)
		{
			_done++;
			IndexJump("2");
		}
		else if (_done == 8) 
		{
			_done++;
			IndexJump("4");
		}
		else 
		{
			_done++;
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return new B99_Conclusion(Manager, Data, GetPlayer());
	}
}
