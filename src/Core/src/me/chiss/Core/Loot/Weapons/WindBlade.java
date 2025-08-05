package me.chiss.Core.Loot.Weapons;

import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Loot.LootBase;
import me.chiss.Core.Loot.LootFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;

public class WindBlade extends LootBase
{
	private HashSet<Player> _active = new HashSet<Player>();

	public WindBlade(LootFactory factory) 
	{
		super(factory, "Wind Blade", new String[]  
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "7",
				C.cGray + "Ability: " + C.cYellow + "Wind Rider",
				C.cGray + "Knockback: " + C.cYellow + "300%",
				"",	
				C.cWhite + "Once owned by the God Zephyrus,",
				C.cWhite + "it is rumoured the Wind Blade",
				C.cWhite + "grants its owner flight.",
				""
				},
				Material.IRON_SWORD, ActionType.R, 3);
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{
		event.AddMod("Negate", GetName(), -event.GetDamageInitial(), false);

		//Base Damage
		event.AddMod(event.GetDamagerPlayer(false).getName(), C.mLoot + GetName(), 7, true);	

		//Double Knockback
		event.AddKnockback("Wind Blade", 3);
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();

		if (player.getLocation().getBlock().isLiquid())
		{
			UtilPlayer.message(player, F.main(GetName(), "You cannot use " + F.skill("Wind Rider") + " in water."));
			return;
		}

		if (!Factory.Energy().Use(player, "Wind Rider", 20, true, true))
			return;

		_active.add(player);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{
			if (!_active.contains(cur))
				continue;

			if (!cur.isBlocking())
			{
				_active.remove(cur);
				continue;
			}

			if (cur.getLocation().getBlock().isLiquid())
			{
				UtilPlayer.message(cur, F.main(GetName(), "You cannot use " + F.skill("Wind Rider") + " in water."));
				_active.remove(cur);
				continue;
			}

			if (!Factory.Energy().Use(cur, "Wind Rider", 2, true, true))
			{
				_active.remove(cur);
				continue;
			}

			UtilAction.velocity(cur, 0.6, 0.1, 1, true);

			//Effect
			cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, 80);
			cur.getWorld().playSound(cur.getLocation(), Sound.FIZZ, 1.2f, 1.5f);
		}
	}

	@Override
	public void ResetCustom(Player player)
	{
		_active.remove(player);
	}
}
