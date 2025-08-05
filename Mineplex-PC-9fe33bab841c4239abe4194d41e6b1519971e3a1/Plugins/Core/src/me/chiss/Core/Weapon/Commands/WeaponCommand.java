package me.chiss.Core.Weapon.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Weapon.IWeapon;
import me.chiss.Core.Weapon.WeaponFactory;

public class WeaponCommand extends CommandBase<WeaponFactory>
{
	public WeaponCommand(WeaponFactory plugin)
	{
		super(plugin, Rank.ADMIN, "wep", "weapon");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args == null || args.length == 0)
		{			
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Listing Weapons:"));
			
			for (IWeapon cur : Plugin.GetWeapons())
			{
				UtilPlayer.message(caller, cur.GetName());
			}
			
			UtilPlayer.message(caller, "Type " + F.elem("/weapon <Weapon>") + " to receive Weapon.");
			return;
		}
			
		for (IWeapon cur : Plugin.GetWeapons())
		{
			if (cur.GetName().toLowerCase().contains(args[0].toLowerCase()))
			{				
			    caller.getInventory().addItem(ItemStackFactory.Instance.CreateStack(cur.GetType(), cur.GetAmount()));

				UtilPlayer.message(caller, F.main("WeaponFactory", "You received " + F.elem(cur.GetName()) + "."));
			}
		}
	}
}
