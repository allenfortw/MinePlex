package nautilus.game.tutorial.part.c_locked_skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.chiss.Core.Shop.events.PurchasePackageEvent;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class C06_Purchase extends Part
{	
	public boolean browsed = false;

	public C06_Purchase(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions()  
	{
		//Dont give if already given
		if (!GetPointRemove())
		{
			Manager.GivePoints(GetPlayerName(), 4000);
			SetCreditRemove(true);
			Add(new Dialogue(this, "System", "You received " + F.ta("4000 Points") + "."));
		}

		Add(new Pause(this, 4000));

		Add(new Dialogue(this, "We are going to purchase " + F.ts("Inferno") + " for " + F.te("Mage") + "."));
		Add(new Dialogue(this, "Doesn't that sound fun!?"));
		Add(new Dialogue(this, "It's extremely simple, just follow these steps!"));

		Add(new Index(this, "Purchase")); 

		Add(new Dialogue(this, "Step 1) " + F.ta("Right-Click") + " the " + F.ts("Class Shop") + "."));
		Add(new Dialogue(this, "Step 2) " + F.ta("Left-Click") + " on " + F.ts("Mage Class") + "."));
		Add(new Dialogue(this, "Step 3) " + F.ta("Left-Click") + " on " + F.ts("Diamond") + " to change " + F.te("Currency") + "."));
		Add(new Dialogue(this, "Step 4) " + F.ta("Left-Click") + " on " + F.ts("Inferno") + "."));
		Add(new Dialogue(this, "Step 5) " + F.ta("Left-Click") + " on " + F.te(C.cYellow + "Confirm") + "."));

		Add(new AllowClassShop(this, true));
		Add(new Dialogue(this, "Give it a try now!"));

		Add(new Pause(this, 20000));

		Add(new Dialogue(this, Dialogue.restartMessages));

		Add(new ForceLook(this, Manager.classSetup, 0));

		Add(new IndexJump(this, "Purchase"));

		Add(new Index(this, "Purchased"));

		Add(new Dialogue(this, "Wow " + GetPlayerName() + ", you are just so smart!"));
		
		Add(new Complete(this));
	}

	@EventHandler
	public void ClassShop(PurchasePackageEvent event)
	{
		if (!event.GetPlayerName().equals(GetPlayerName()))
			return;

		if (!event.GetItemName().contains("Inferno"))
		{
			event.setCancelled(true);
			event.SetReason("Silly rabbit, this is not Inferno!");
			IndexJump("Purchase");
			return;
		}

		IndexJump("Purchased");
		this.SetCreditRemove(false);
	}

	@Override
	public Part GetNext() 
	{
		return new C99_Conclusion(Manager, Data, GetPlayer());
	}
}
