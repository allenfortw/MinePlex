package nautilus.game.tutorial.part.d_class_builds;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class D05_Tokens extends Part
{	
	private boolean _done = false;
	
	public D05_Tokens(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Pause(this, 4000));
		
		Add(new Dialogue(this, "Alright... back to " + F.te("Custom Builds") + ".")); 
		Add(new Dialogue(this, "Equipping " + F.te("Skills") + " requires " + F.te("Skill Tokens") + ".")); 
		Add(new Dialogue(this, "Equipping " + F.te("Weapons") + " and " + F.te("Items") + " requires " + F.te("Item Tokens") + ".")); 
		
		Add(new Dialogue(this, "You always have " + F.te("120 Skill Tokens") + " and " + F.te("120 Item Tokens") + "."));
		Add(new Dialogue(this, "They are displayed at the top of the " + F.te("Custom Build Menu") + "."));
		Add(new Dialogue(this, "You should always use all of your " + F.te("Tokens") + "!"));
		
		Add(new Pause(this, 2000));
		
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Create a " + F.te("Custom Build") + " for " + F.te("Ranger") + " which;"));
		Add(new Dialogue(this, "a.) Uses all " + F.te("120 Skill Tokens")));
		Add(new Dialogue(this, "b.) Uses all " + F.te("120 Item Tokens")));
		Add(new Dialogue(this, "Go! Go! Go!"));
		Add(new AllowClassSetup(this, true));
		 
		Add(new Pause(this, 30000));
		Add(new Dialogue(this, Dialogue.restartMessages));	
		Add(new IndexJump(this, "Start"));
		
		Add(new Index(this, "Equip"));
		
		Add(new Dialogue(this, "Incredible!"));
		
		Add(new Complete(this));
	}
	
	@EventHandler
	public void UpdateProximity(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (!AllowClassSetup())
			return;

		if (_done)
			return;
		
		if (GetPlayer().getOpenInventory() != null)
			if (GetPlayer().getOpenInventory().getType() == InventoryType.CHEST)
				return;

		CustomBuildToken token = Manager.Clients().Get(GetPlayer()).Donor().GetActiveCustomBuild(Manager.Classes().GetClass("Ranger"));
		
		if (token.ItemTokensBalance <= 0 && token.SkillTokensBalance <= 0)
		{
			IndexJump("Equip");
			_done = true;
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return new D99_Conclusion(Manager, Data, GetPlayer());
	}
}
