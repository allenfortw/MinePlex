package me.chiss.Core.Loot.Weapons;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;

public class LightningScythe extends LootBase
{
	public LightningScythe(LootFactory factory) 
	{
		super(factory, "Lightning Scythe", new String[] 
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "7",	
				C.cGray + "Ability: " + C.cYellow + "Lightning Strike",
				C.cGray + "Passive: " + C.cYellow + "Lightning Speed",
				C.cGray + "Passive: " + C.cYellow + "Electric Shock",
				C.cGray + "Knockback: " + C.cYellow + "100%",
				"",
				C.cWhite + "An old scythe that is infused with the",
				C.cWhite + "powers of the skies, able to strike ",
				C.cWhite + "lightning at the owners command.",
				""
				},
				Material.IRON_HOE, ActionType.R, 2);
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{
		event.AddMod("Negate", GetName(), -event.GetDamageInitial(), false);

		//Base Damage
		event.AddMod(event.GetDamagerPlayer(false).getName(), C.mLoot + GetName(), 7, true);		

		//Double Knockback
		event.SetKnockback(false);
		
		//Effect
		Factory.Condition().Factory().Shock(GetName(), event.GetDamageeEntity(), event.GetDamagerPlayer(false), 2, false, false);
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		
		Block target = event.getPlayer().getTargetBlock(null, 0);
		
		if (target == null)
			return;
		
		if (UtilMath.offset(target.getLocation(), player.getLocation()) > 24)
		{
			UtilPlayer.message(player, F.main(GetName(), "Target is out of range."));
			return;
		}

		if (!Factory.Energy().Use(player, "Lightning Strike", 20, false, true))
			return;
		
		if (!Recharge.Instance.use(player, "Lightning Strike", 8000, true))
			return;

		if (!Factory.Energy().Use(player, "Lightning Strike", 20, true, true))
			return;
		
		//Durability
		player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 6));
		if (player.getItemInHand().getDurability() > 251)
			player.setItemInHand(null);
		UtilInv.Update(player);
		
		//Condition
		for (Player cur : UtilPlayer.getNearby(target.getLocation(), 8))
			Factory.Condition().Factory().Lightning("Lightning Strike", cur, player, 0, 1, false, true);

		//Strike
		target.getWorld().strikeLightning(target.getLocation());
		
		UtilPlayer.message(player, F.main(GetName(), "You used " + F.skill("Lightning Strike") + "."));
		
		event.setCancelled(true);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : GetUsers())
			Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9, 0, false, true);
	}

	@Override
	public void ResetCustom(Player player)
	{
		
	}
}
