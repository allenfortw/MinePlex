package me.chiss.Core.Loot.Weapons;

import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;

public class MagneticBlade extends LootBase
{
	private HashSet<Player> _active = new HashSet<Player>();

	public MagneticBlade(LootFactory factory) 
	{
		super(factory, "Magnetic Blade", new String[] 
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "7",
				C.cGray + "Ability: " + C.cYellow + "Magnetic Pull",
				C.cGray + "Knockback: " + C.cYellow + "Negative 40%",
				"",
				C.cWhite + "The Magnetic Blade is said to be able",
				C.cWhite + "to pull nearby objects towards itself",
				C.cWhite + "with unstoppable force.",
				""
				},
				Material.IRON_SWORD, ActionType.R, 2);
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{
		event.AddMod("Negate", GetName(), -event.GetDamageInitial(), false);

		//Base Damage
		event.AddMod(event.GetDamagerPlayer(false).getName(), C.mLoot + GetName(), 7, true);	

		//Reverse Knockback
		event.AddKnockback(GetName(), -0.4);	
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();

		if (player.getLocation().getBlock().isLiquid())
		{
			UtilPlayer.message(player, F.main(GetName(), "You cannot use " + F.skill(GetName()) + " in water."));
			return;
		}

		if (!Factory.Energy().Use(player, GetName(), 10, true, true))
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
				continue;

			//Target
			Block target = cur.getTargetBlock(null, 0);

			if (target == null)
				continue;

			if (UtilMath.offset(target.getLocation(), cur.getLocation()) > 16)
				continue;
			
			//Energy
			if (!Factory.Energy().Use(cur, GetName(), 1, true, true))
			{
				_active.remove(cur);
				continue;
			}

			//Effect
			cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, 42);

			//Pull		
			for (Player other : cur.getWorld().getEntitiesByClass(Player.class))
			{				
				//Other to Block
				if (UtilMath.offset(target.getLocation(), other.getLocation()) > 3)		
					continue;

				//Other to Cur
				if (UtilMath.offset(cur, other) < 2)		
					continue;

				if (!Factory.Relation().CanHurt(cur, other))
					continue;

				UtilAction.velocity(other, UtilAlg.getTrajectory(other, cur), 
						0.3, false, 0, 0, 1, true);
			}

			for (Entity other : cur.getWorld().getEntitiesByClass(org.bukkit.entity.Item.class))
			{					
				//Other to Block
				if (UtilMath.offset(target.getLocation(), other.getLocation()) > 3)		
					continue;

				UtilAction.velocity(other, UtilAlg.getTrajectory(other.getLocation(), cur.getEyeLocation()), 
						0.3, false, 0, 0, 1, true);
			}
		}
	}

	@Override
	public void ResetCustom(Player player)
	{
		_active.remove(player);
	}
}
