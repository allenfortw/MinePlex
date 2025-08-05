package nautilus.game.tutorial.part.d_class_builds;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;


import me.chiss.Core.Skill.ISkill;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class D02_InfernoBuild extends Part
{	
	private boolean _done = false;
	
	public D02_InfernoBuild(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new Index(this, "Start"));
		
		Add(new Dialogue(this, "We'll create a " + F.te("Mage Custom Build") + " with " + F.ts("Inferno") + ".")); 
		
		Add(new Dialogue(this, "Just follow these steps;")); 
		
		Add(new Dialogue(this, "Step 1) " + F.ta("Right-Click") + " the " + F.te("Class Setup Table") + "."));
		Add(new Dialogue(this, "Step 2) " + F.ta("Left-Click") + " on " + F.te("Mage Custom Build") + "."));
		Add(new Dialogue(this, "Step 3) " + F.ta("Left-Click") + " on " + F.te("Edit & Save Build") + "."));
		Add(new Dialogue(this, "Step 4) " + F.ta("Left-Click") + " on " + F.ts("Inferno") + "."));
		Add(new Dialogue(this, "Step 5) Close the " + F.te("Class Setup Menu") + "."));
			
		Add(new AllowClassSetup(this, true));
		  
		Add(new Pause(this, 30000));
		
		Add(new Dialogue(this, Dialogue.restartMessages));
		
		Add(new IndexJump(this, "Start"));
		
		Add(new Index(this, "Equip"));
		
		Add(new Dialogue(this, "Well done! You equipped a " + F.te("Custom Build") + " with " + F.ts("Inferno") + "."));
		Add(new Dialogue(this, "However, you need a " + F.ta("Sword") + " to use " + F.ts("Inferno")  +  ".")); 
		
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
			
			IndexJump("Equip");
			GetPlayer().closeInventory();
			
			_done = true;
		}
	}
	
	@Override
	public Part GetNext() 
	{
		return new D03_Weapons(Manager, Data, GetPlayer());
	}
}
