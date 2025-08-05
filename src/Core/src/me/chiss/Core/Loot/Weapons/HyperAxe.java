package me.chiss.Core.Loot.Weapons;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Loot.LootBase;
import me.chiss.Core.Loot.LootFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilEvent.ActionType;

public class HyperAxe extends LootBase
{
	private HashMap<Player, Long> _rate = new HashMap<Player, Long>();

	public HyperAxe(LootFactory factory) 
	{
		super(factory, "Hyper Axe", new String[] 
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "3",
				C.cGray + "Ability: " + C.cYellow + "Hyper Speed",
				C.cGray + "Passive: " + C.cYellow + "Hyper Attack",	
				C.cGray + "Knockback: " + C.cYellow + "25%",
				"",
				C.cWhite + "Rumoured to attack foes 500% faster",
				C.cWhite + "than any other weapon known to man.",
				""
				},
				Material.IRON_AXE, ActionType.R, 1);
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{

	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void Rate(CustomDamageEvent event)
	{
		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;

		if (!GetUsers().contains(damager))
			return;

		if (!damager.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;

		//Rate
		if (_rate.containsKey(damager))
			if (!UtilTime.elapsed(_rate.get(damager), 80))
			{
				event.SetCancelled("Damage Rate");
				return;
			}

		_rate.put(damager, System.currentTimeMillis());
		event.SetIgnoreRate(true);	

		event.SetCancelled(GetName());
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void DoDamage(CustomDamageEvent event)
	{
		if (!event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = event.GetDamagerPlayer(false);
		if (damager == null)	return;
		
		if (!GetUsers().contains(damager))
			return;

		if (!event.GetCancellers().remove(GetName()))
			return;
		
		if (event.IsCancelled())
			return;

		//Negate
		event.AddMod("Negate", GetName(), -event.GetDamageInitial(), false);

		//Damage
		event.AddMod(damager.getName(), C.mLoot + GetName(), 3, true);
		event.AddKnockback(GetName(), 0.25);
		damager.getWorld().playSound(damager.getLocation(), Sound.NOTE_PLING, 0.5f, 2f);
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();

		if (!Factory.Energy().Use(player, GetName(), 40, false, true))
			return;

		if (!Recharge.Instance.use(player, GetName(), 18000, true))
			return;

		if (!Factory.Energy().Use(player, GetName(), 40, true, true))
			return;

		//Inform
		UtilPlayer.message(player, F.main(GetName(), "You used " + F.skill("Hyper Speed") + "."));

		//Add
		Factory.Condition().Factory().Speed(GetName(), player, player, 8, 3, false, true);
	}

	@EventHandler
	public void SwingSpeed(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
			Factory.Condition().Factory().DigFast(GetName(), cur, cur, 1.9, 1, false, false);
	}

	@Override
	public void ResetCustom(Player player)
	{
		_rate.remove(player);
	}
}
