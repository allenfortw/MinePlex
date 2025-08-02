package nautilus.game.tutorial.part.d_class_builds;

import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


import me.chiss.Core.Skill.ISkill;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class D03_Weapons extends Part
{	
	private boolean _done = false;
	
	public D03_Weapons(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Pause(this, 2000));
		
		Add(new Dialogue(this, "As well as " + F.te("Skills") + ", you must also choose " + F.te("Weapons") + "!"));
		
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "Open your " + F.te("Custom Build") + " again."));
		Add(new Dialogue(this, "In the top right corner, you will see " + F.te("Page Turner") + "."));
		Add(new Dialogue(this, F.ta("Left-Click") + " on " + F.te("Page Turner") + " to change page."));
		Add(new Dialogue(this, "On this page, you can choose " + F.te("Weapons") + " for combat."));
		Add(new Dialogue(this, "On the next page, you can choose " + F.te("Items") + " and " + F.te("Food") + "."));
		Add(new Dialogue(this, "Create a " + F.te("Custom Build") + " with " + F.ts("Inferno") + " and " + F.ts("Standard Sword") + "."));
		
		Add(new AllowClassSetup(this, true));
		 
		Add(new Pause(this, 30000));
		
		Add(new Dialogue(this, Dialogue.restartMessages));
		
		Add(new IndexJump(this, "Start"));
		
		Add(new Index(this, "Equip"));
		
		Add(new Dialogue(this, "Great job! You seem to be getting the hang of this!"));
		
		Add(new Complete(this));
	}

	@EventHandler
	public void UpdateEquip(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		if (_done)
			return;
		
		if (!AllowClassSetup())
			return;
		
		if (GetPlayer().getOpenInventory() != null)
			if (GetPlayer().getOpenInventory().getType() == InventoryType.CHEST)
				return;
		
		for (Entry<ISkill, Integer> skill : Manager.Clients().Get(GetPlayer()).Class().GetSkills())
		{
			if (skill.getValue() <= 0)
				continue;
			
			if (!skill.getKey().GetName().equals("Inferno"))
				continue;
			
			boolean sword = false;
			for (int i=0 ; i<9 ; i++)
			{
				ItemStack stack = GetPlayer().getInventory().getItem(i);
				if (stack != null && stack.getType() == Material.IRON_SWORD)
					sword = true;
			}
			if (!sword)
				continue;
			
			IndexJump("Equip");
			GetPlayer().closeInventory();
			
			_done = true;
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return new D04_Inferno(Manager, Data, GetPlayer());
	}
}
