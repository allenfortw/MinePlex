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

public class AlligatorsTooth extends LootBase
{
	private HashSet<Player> _active = new HashSet<Player>();

	public AlligatorsTooth(LootFactory factory) 
	{
		super(factory, "Alligators Tooth", new String[] 
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "7 + 2 in Water",
				C.cGray + "Ability: " + C.cYellow + "Alliagtor Rush",
				C.cGray + "Passive: " + C.cYellow + "Water Breathing",
				C.cGray + "Knockback: " + C.cYellow + "100%",
				"",
				C.cWhite + "A blade forged from hundreds of",
				C.cWhite + "alligators teeth. It's powers allow ",
				C.cWhite + "its owner to swim with great speed,",
				C.cWhite + "able to catch any prey.",
				""
				},
				Material.IRON_SWORD, ActionType.R, 1);
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{
		event.AddMod("Negate", GetName(), -event.GetDamageInitial(), false);

		//Base Damage
		event.AddMod(event.GetDamagerPlayer(false).getName(), C.mLoot + GetName(), 7, true);	
		
		if (event.GetDamageeEntity().getLocation().getBlock().isLiquid())
		{
			event.AddMod(event.GetDamagerPlayer(false).getName(), C.mLoot + "Water Bonus", 2, false);
			event.AddKnockback(GetName(), 0.5);
		}
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		
		if (!player.getLocation().getBlock().isLiquid())
		{
			UtilPlayer.message(player, F.main(GetName(), "You cannot use " + F.skill("Alligator Rush") + " out of water."));
			return;
		}

		if (!Factory.Energy().Use(player, "Alligator Rush", 10, true, true))
			return;

		_active.add(player);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() == UpdateType.TICK)
			for (Player cur : GetUsers())
			{
				if (!_active.contains(cur))
					continue;
				
				if (!cur.isBlocking())
				{
					_active.remove(cur);
					continue;
				}
				
				if (!cur.getLocation().getBlock().isLiquid())
					continue;
				
				if (!Factory.Energy().Use(cur, "Alligator Rush", 1, true, true))
				{
					_active.remove(cur);
					continue;
				}
	
				UtilAction.velocity(cur, 0.6, 0.1, 1, true);
	
				//Effect
				cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, 8);
				
				if (!cur.getEyeLocation().getBlock().isLiquid())
					cur.getWorld().playSound(cur.getEyeLocation(), Sound.SPLASH, 0.25f, 1f);
				
				cur.getWorld().playSound(cur.getLocation(), Sound.SPLASH, 0.25f, 1f);
			}
		
		if (event.getType() == UpdateType.FAST)
			for (Player cur : GetUsers())
				Factory.Condition().Factory().Breath(GetName(), cur, cur, 1.9, 0, false, false);
	}

	@Override
	public void ResetCustom(Player player)
	{
		_active.remove(player);
	}
}
