package nautilus.game.tutorial.part.c_locked_skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import mineplex.core.Rank;
import me.chiss.Core.Skill.ISkill;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.minecraft.account.CoreClient;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class C05_LockedSkills extends Part
{	
	private boolean _prox = false;

	public C05_LockedSkills(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Pause(this, 2000));

		Add(new Dialogue(this, "Let's talk about " + F.te("Skill Ownership") + "."));
		Add(new Dialogue(this, "You already own the " + F.te("Skills") + " in the " + F.te("Default Builds") + "."));
		
		Add(new Dialogue(this, "But other " + F.te("Skills") + " are " + F.te(C.cRed + "Locked") + "."));
		Add(new Dialogue(this, "You will need to " + F.ta("Unlock") + " them, before using them."));
		Add(new Dialogue(this, "You can do this at the " + F.te("Class Shop") + "."));

		Add(new Pause(this, 1000));

		Add(new Index(this, "Search"));

		Add(new Dialogue(this, "Head over to the " + F.te("Class Shop") + "."));
		Add(new ForceLook(this, Manager.classShop, 0));
		Add(new AllowAction(this, true));

		Add(new Pause(this, 20000));
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "Search"));

		Add(new Index(this, "Proximity"));

		Add(new Dialogue(this, "Here, you can " + F.ta("Unlock") + " new " + F.te("Skills") + "."));
		Add(new Dialogue(this, "You can do this with " + F.ta("Points") + " or " + F.te(C.cAqua + "Credits") + "."));
		Add(new Dialogue(this, "You earn " + F.ta("Points") + " by playing games, like " + F.te("Domination") + "."));
		Add(new Dialogue(this, "You receive " + F.te(C.cAqua + "Credits") + " for donating to " + F.te("BetterMC") + "."));

		Add(new Pause(this, 1000));

		Add(new Dialogue(this, "Let's purchase your first " + F.te("Skill") + "!"));
		Add(new Dialogue(this, "I'll give you some " + F.ta("Points") + "."));

		Add(new Complete(this));
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

		if (UtilMath.offset(GetPlayer().getLocation(), Manager.classShop) < 3)
		{
			IndexJump("Proximity");
			_prox = true;
		}
	}

	@Override
	public Part GetNext() 
	{
		ISkill inferno = Manager.Skills().GetSkill("Inferno");

		CoreClient client = Manager.Clients().Get(GetPlayer());
		
		if (client.Donor().GetSalesPackagesOwned().contains(inferno.GetSalesPackageId(1)) ||
				client.Rank().Has(Rank.DIAMOND, false))
		{
			return new C06b_Purchase(Manager, Data, GetPlayer());
		}
		else
		{
			return new C06_Purchase(Manager, Data, GetPlayer());
		}	
	}
}
