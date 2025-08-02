package me.chiss.Core.Loot.Weapons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Loot.LootBase;
import me.chiss.Core.Loot.LootFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;

public class GiantsSword extends LootBase
{
	public GiantsSword(LootFactory factory) 
	{
		super(factory, "Giants Broadsword", new String[] 
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "10",
				C.cGray + "Ability: " + C.cYellow + "Giant Recovery",
				C.cGray + "Knockback: " + C.cYellow + "100%",
				"",
				C.cWhite + "Forged by the ancient giants. It's blade",
				C.cWhite + "deals more damage than any other weapon.",
				C.cWhite + "Upon blocking, it protects its owner.",
				""
				},
				Material.IRON_SWORD, ActionType.R, 3);
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{
		event.AddMod("Negate", GetName(), -event.GetDamageInitial(), false);

		//Base Damage
		event.AddMod(event.GetDamagerPlayer(false).getName(), C.mLoot + GetName(), 10, true);	
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{
		
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
		{
			if (!cur.isBlocking())
				continue;
			
			UtilPlayer.health(cur, 1);
			Factory.Condition().Factory().Regen(GetName(), cur, cur, 0.9, 1, false, true);
			Factory.Condition().Factory().Protection(GetName(), cur, cur, 0.9, 1, false, true);
		}
	}

	@Override
	public void ResetCustom(Player player)
	{
		
	}
}
