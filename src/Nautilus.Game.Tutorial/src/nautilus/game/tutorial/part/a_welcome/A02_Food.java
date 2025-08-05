package nautilus.game.tutorial.part.a_welcome;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.*;
import nautilus.game.tutorial.part.Part;

public class A02_Food extends Part
{	
	public A02_Food(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		ItemStack item;

		Add(new Dialogue(this, "Excellent, let's get started!"));
		Add(new Dialogue(this, "Before we begin, there is something important to do..."));
		Add(new Pause(this, 1200));
		
		item =  ItemStackFactory.Instance.CreateStack(Material.PUMPKIN_PIE);
		Add(new Dialogue(this, "...you'll need to eat this pie, I just baked it!"));
		Add(new ItemAdd(this, item));
		Add(new Dialogue(this, "Careful not to drop it, it's hot!"));
		
		Add(new Pause(this, 6000));
		Add(new Dialogue(this, "Not a fan of pumpkin, eh?"));
		Add(new ItemRemove(this, item));
		
		item =  ItemStackFactory.Instance.CreateStack(Material.COOKED_BEEF);
		Add(new Dialogue(this, "That's alright, I cook a mean steak."));
		Add(new ItemAdd(this, item));
		Add(new Dialogue(this, "Have a taste of this!"));

		Add(new Pause(this, 6000));
		Add(new Dialogue(this, "Vegetarian maybe? Fine, eat this..."));
		Add(new ItemRemove(this, item));
		
		item =  ItemStackFactory.Instance.CreateStack(Material.BREAD);
		Add(new Dialogue(this, "Freshly baked bread!"));
		Add(new ItemAdd(this, item));

		Add(new Pause(this, 6000));
		Add(new Dialogue(this, "You're not making this easy on me..."));
		Add(new ItemRemove(this, item));
		
		item =  ItemStackFactory.Instance.CreateStack(Material.APPLE);
		Add(new Dialogue(this, "Please... you must eat something!"));
		Add(new ItemAdd(this, item));
		Add(new Dialogue(this, "Apple?"));
		
		Add(new Pause(this, 6000));
		Add(new ItemRemove(this, item));

		item =  ItemStackFactory.Instance.CreateStack(Material.COOKIE);
		Add(new Dialogue(this, "How about a delicious cookie..?"));
		Add(new ItemAdd(this, item));

		Add(new Pause(this, 6000));
		Add(new Dialogue(this, "Fine! That's it! I've had enough..."));
		Add(new ItemRemove(this, item));

		item =  ItemStackFactory.Instance.CreateStack(Material.RAW_FISH);
		Add(new Dialogue(this, "Eat this fish."));
		Add(new ItemAdd(this, item));

		Add(new Index(this, "Fish"));
		
		Add(new Pause(this, 6000));
		Add(new Dialogue(this, Dialogue.fishMessage));
		
		Add(new IndexJump(this, "Fish"));
	}
	
	@Override
	public void FoodUpdate(Player player)
	{
		if (player.getFoodLevel() > 18)
			SetCompleted(true);
		
		player.setFoodLevel(18);
	}

	@Override
	public Part GetNext() 
	{
		return new A03_Teleport(Manager, Data, GetPlayer());
	}
}
