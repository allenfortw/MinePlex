package me.chiss.Core.Modules;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Module.AModule;

public class Give extends AModule
{
	public Give(JavaPlugin plugin) 
	{
		super("Give", plugin);
	}

	//Module Functions
	@Override
	public void enable() 
	{
		
	}

	@Override
	public void disable() 
	{
	
	}

	@Override
	public void config() 
	{
	
	}
	
	@Override
	public void commands() 
	{
		AddCommand("g");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (args.length == 0)
			help(caller);

		else if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		else if (args.length == 1)
			give(caller, caller.getName(), args[0], "1");

		else if (args.length == 2)
			give(caller, args[0], args[1], "1");

		else 
			give(caller, args[0], args[1], args[2]);

	}

	public void help(Player caller)
	{
		UtilPlayer.message(caller, F.main("Give", "Commands List;"));
		UtilPlayer.message(caller, F.help("/g <item> (amount)", "Give Item to Self", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/g <player> <item> (amount)", "Give Item to Player", Rank.ADMIN));
	}

	public void give(Player caller, String target, String name, String amount)
	{
		//Item
		LinkedList<Material> itemList = new LinkedList<Material>();
		itemList = Util().Items().matchItem(caller, name, true);
		if (itemList.isEmpty())			return;
		
		//Player
		LinkedList<Player> giveList = new LinkedList<Player>();
		
		if (target.equalsIgnoreCase("all"))
		{
			for (Player cur : UtilServer.getPlayers())
				giveList.add(cur);
		}
		else
		{
			giveList = UtilPlayer.matchOnline(caller, target, true);
			if (giveList.isEmpty())			return;
		}
		

		//Amount
		int count = 1;
		try
		{
			count = Integer.parseInt(amount);

			if (count < 1)
			{
				UtilPlayer.message(caller, F.main("Give", "Invalid Amount [" + amount + "]. Defaulting to [1]."));
				count = 1;
			}
		}
		catch (Exception e)
		{
			UtilPlayer.message(caller, F.main("Give", "Invalid Amount [" + amount + "]. Defaulting to [1]."));
		}

		//Create
		String givenList = "";
		for (Player cur : giveList)
			givenList += cur.getName() + " ";
		if (givenList.length() > 0)
			givenList = givenList.substring(0, givenList.length()-1);
		
		for (Material curItem : itemList)
		{
			for (Player cur : giveList)
			{
				ItemStack stack = ItemStackFactory.Instance.CreateStack(curItem, count);

				//Give
				if (UtilInv.insert(cur, stack))
				{
					//Inform
					if (!cur.equals(caller))
						UtilPlayer.message(cur, F.main("Give", "You received " + F.item(count + " " + ItemStackFactory.Instance.GetName(curItem, (byte)0, false)) + " from " + F.elem(caller.getName()) + "."));
				}
			}
			
			if (target.equalsIgnoreCase("all"))
				UtilPlayer.message(caller, F.main("Give", "You gave " + F.item(count + " " + ItemStackFactory.Instance.GetName(curItem, (byte)0, false)) + " to " + F.elem("ALL")) + ".");
			
			else if (giveList.size() > 1)
				UtilPlayer.message(caller, F.main("Give", "You gave " + F.item(count + " " + ItemStackFactory.Instance.GetName(curItem, (byte)0, false)) + " to " + F.elem(givenList) + "."));
			
			else
				UtilPlayer.message(caller, F.main("Give", "You gave " + F.item(count + " " + ItemStackFactory.Instance.GetName(curItem, (byte)0, false)) + " to " + F.elem(giveList.getFirst().getName()) + "."));
			
			
			//Log
			Log("Gave [" + count + " " + ItemStackFactory.Instance.GetName(curItem, (byte)0, false) + "] to [" + givenList + "] from [" + caller.getName() + "].");
		}
	}
}
